package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType.E45;
import static ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType.R15;
import static ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType.R32;
import static ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType.R50;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.IS_AUDITS_ENABLED;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.IS_FILEDDROPS_ENABLED;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.IS_MQ_ENABLED;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.MQ_CHANNEL;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.MQ_HOST;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.MQ_PORT;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.MQ_QUEUEMANAGER;

import java.net.MalformedURLException;
import java.util.Properties;

import javax.jms.JMSException;

import org.apache.camel.Predicate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.support.builder.PredicateBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.mq.MQException;
import com.ibm.mq.jmqi.JmqiException;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jms.DetailedJMSException;
import com.ibm.msg.client.wmq.WMQConstants;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.TransactionIdGenerator;
import ca.bc.gov.hlth.hnsecure.audit.AuditProcessor;
import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.filedrops.RequestFileDropGenerater;
import ca.bc.gov.hlth.hnsecure.filedrops.ResponseFileDropGenerater;
import ca.bc.gov.hlth.hnsecure.parsing.FhirPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateReqHeader;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.validation.PayLoadValidator;
import ca.bc.gov.hlth.hnsecure.validation.TokenValidator;
import ca.bc.gov.hlth.hnsecure.validation.Validator;
import ca.bc.gov.hlth.hnsecure.validation.ValidatorImpl;

public class Route extends BaseRoute {
	
	private static final Logger logger = LoggerFactory.getLogger(Route.class);

	private static final String HTTP_REQUEST_ID_HEADER = "X-Request-Id";

	private static ApplicationProperties properties;
    
    private Validator validator;
    
	@Override
    public void configure() {
    	  	
    	init();
   
		String isFileDropsEnabled = properties.getValue(IS_FILEDDROPS_ENABLED);
		String isAuditsEnabled = properties.getValue(IS_AUDITS_ENABLED);
		Predicate isRTrans = isRTrans();
		Predicate isMessageForHIBC = isMessageForHIBC();

		handleExceptions();

		// Main (Jetty HTTP server) route
        from("jetty:http://{{hostname}}:{{port}}/{{endpoint}}?httpMethodRestrict=POST").routeId("hnsecure-route")

        	.log("HNSecure received a request")
			// If a transaction ID is provided in the HTTP request header, use it as the exchange id instead of the camel generated id
			.choice()
				.when(header(HTTP_REQUEST_ID_HEADER))
					.process(exchange -> {
						exchange.setExchangeId(exchange.getIn().getHeader(HTTP_REQUEST_ID_HEADER, String.class));
					}).id("SetExchangeIdFromHeader")
			.end()
        	.setProperty(Util.PROPERTY_IS_FILE_DROPS_ENABLED).simple(isFileDropsEnabled)
        	.setProperty(Util.PROPERTY_IS_AUDITS_ENABLED).simple(isAuditsEnabled)
        	// Extract the message using custom extractor and log 
        	.setBody().method(new FhirPayloadExtractor()).log("Decoded V2: ${body}")
        	// Added wireTap for asynchronous call to filedrop request
        	.process(new AuditSetupProcessor(TransactionEventType.TRANSACTION_START))
			.wireTap("direct:audit").end()
			.wireTap("direct:requestFileDrop").end()
        	// Validate the message
        	.process(validator).id("Validator")
            // Set the receiving app, message type into headers and properties
        	// XXX With the placement of this bean, the headers/properties aren't available to preceding Processors/Beans
        	// such as the RequestFileDropGenerator. Ideally it should be moved up but there could be some parsing issues
        	// since the validator wouldn't have been run yet
            .bean(PopulateReqHeader.class).id("PopulateReqHeader")
            .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
            .log("The message receiving application is <${exchangeProperty.receivingApp}> and the message type is <${exchangeProperty.messageType}>.")
             
            // Dispatch the message based on the receiving application code and message type
            .choice()
	            // Sending message to PharmaNet
            	.when(exchangeProperty(Util.PROPERTY_RECEIVING_APP).isEqualTo(Util.RECEIVING_APP_PNP))
            		.to("direct:pharmanet")
		            
		 	    // Sending message to RTrans     
				.when(isRTrans)
					.to("direct:rtrans")

		        // sending message to HIBC for ELIG
				.when(isMessageForHIBC)
					.to("direct:hibc")
                 
    	        // sending to JMB
                .when(exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualTo(R32))
                	.to("direct:jmb")

                // handle unexpected message types     
	            .otherwise()
	            	.log("Found unexpected message of type: ${exchangeProperty.messageType}")                     
            .end()
            // Format the response and perform file drops and auditing
            .to("direct:handleResponse").id("HandleResponse");
		      
        // Request File Drop route
        from("direct:requestFileDrop").log("wireTap direct:requestFileDrop")
        	.choice()
				.when(exchangeProperty(Util.PROPERTY_IS_FILE_DROPS_ENABLED).isEqualToIgnoreCase(Boolean.TRUE))
					.bean(RequestFileDropGenerater.class).id("RequestFileDropGenerater")
					.log("wireTap direct:requestFileDrop done")
			.end();
        // Response File Drop route
        from("direct:responseFileDrop").log("wireTap direct:responseFileDrop")
	        .choice()
	        	.when(exchangeProperty(Util.PROPERTY_IS_FILE_DROPS_ENABLED).isEqualToIgnoreCase(Boolean.TRUE))
					.bean(ResponseFileDropGenerater.class).id("ResponseFileDropGenerater")
					.log("wireTap direct:responseFileDrop done")
			.end();
        
        // Audit route
		from("direct:audit").log("wireTap direct:audit")
			.choice()
				.when(exchangeProperty(Util.PROPERTY_IS_AUDITS_ENABLED).isEqualToIgnoreCase(Boolean.TRUE.toString()))
					.process(new AuditProcessor()).log("wireTap audit done")				
			.end();		
    }
	
    /**
     * This method performs the steps required before configuring the route
     * 1. Set the MQ configuration
     * 2. Set the transaction id generator for messages
     * 3. Load application properties
     * 4. Initializes the validator classes
     */
    private void init() {   	
    	//The purpose is to set custom unique id for logging
    	getContext().setUuidGenerator(new TransactionIdGenerator());
    	injectProperties();
    	properties = ApplicationProperties.getInstance();
    	initMQ();
    	loadValidator();
    }
    
	/**
	 * This method is used to append multiple Predicates for RTrans message type
	 * Builds a compound predicate to use it in the Route
	 */
	private Predicate isRTrans() {		
		Predicate isR03 = exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(Util.R03);
		Predicate isR07 = exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(Util.R07);	
		Predicate isR09 = exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(Util.R09);	
		Predicate pBuilder = PredicateBuilder.or(isR03, isR07, isR09);
		return pBuilder;
	}
	
	/**
	 * This method is used to concat multiple Predicates for HIBC message types. It builds a compound 
	 * predicate to be used in the Route.
	 */
	private Predicate isMessageForHIBC() {		
		Predicate isE45 = exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(E45);	
		Predicate isR15 = exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(R15);
		Predicate isR50 = exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(R50);	
		Predicate pBuilder = PredicateBuilder.or(isR15, isE45, isR50);
		return pBuilder;
	}
    
	/**
     * This method injects application properties set in the context to ApplicationProperties class
     * This helps in using the properties across the application without extending classes as RouteBuilder.
     * Sample test code to use application properties: RouteTest.testApplicationPropertiesLoader()
     */
    public void injectProperties() {
    	Properties properties  = getContext().getPropertiesComponent().loadProperties();
    	ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    	applicationProperties.injectProperties(properties);
    }
    
	/**
     * This method initializes the Validators. 
     * If there was an error in loading validators, HNSecure server will not start
     * Currently only TokenValidator is throwing error. 
     */
    public void loadValidator() {
    	try {
    		// This syntax will make sure validation is happening in the order of First Token validation then Payload validation then  ValidatorImpl
			validator = new TokenValidator(new PayLoadValidator(new ValidatorImpl()));
		} catch (MalformedURLException e) {
			logger.error("Error in server startup: ", e);
			logger.error("Stopping HNSecure Server. ");
			System.exit(0);
		}
    	
    }    

	/**
	 * Creates MQ connection and sets it on a JMS Component which is added to the camel context.
	 */
	private void initMQ() {
		final String methodName = LoggingUtil.getMethodName();

		boolean isMQEnabled = Boolean.valueOf(properties.getValue(IS_MQ_ENABLED));		
		if (isMQEnabled) {
			JmsComponent jmsComponent = new JmsComponent();
	    	MQQueueConnectionFactory mqQueueConnectionFactory = mqQueueConnectionFactory();
			jmsComponent.setConnectionFactory(mqQueueConnectionFactory);
	        getContext().addComponent("jmsComponent", jmsComponent);
			logger.info("{} - Added JMS Component to context with connection factory. {}", methodName);
		} else {
    		logger.info("{} - JMS Component has not been added as MQs are disabled. {}", methodName);
    	}
	}
    
    /**
     * Creates a MQ connection factory and sets its connection properties.
     * 
     * @return a {@link MQQueueConnectionFactory} factory
     */
    private MQQueueConnectionFactory mqQueueConnectionFactory()  {
		final String methodName = LoggingUtil.getMethodName();
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
      
        mqQueueConnectionFactory.setHostName(properties.getValue(MQ_HOST));
        try {
        	mqQueueConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);         
        	mqQueueConnectionFactory.setChannel(properties.getValue(MQ_CHANNEL));
        	mqQueueConnectionFactory.setPort(Integer.valueOf(properties.getValue(MQ_PORT)));
        	mqQueueConnectionFactory.setQueueManager(properties.getValue(MQ_QUEUEMANAGER));
    		logger.debug("{} - MQ connection factory has been created.", methodName);			
        } catch (JMSException je) {       	  
            logger.error("{} - MQ connection factory initialization failed with the error : {}", methodName, je.getMessage());
            
        	// Check for linked exceptions in JMSException
        	ExceptionUtils.getThrowableList(je).forEach(t -> {                    
                // Add on specific information depending on the type of exception
                if (t instanceof JMSException) {
                    JMSException je1 = (JMSException) t;
                    logger.error("{} - JMS Error code: {}", methodName, je1.getErrorCode());
                    
                    if (t instanceof DetailedJMSException){
                    	DetailedJMSException jed = (DetailedJMSException)je1;
                    	logger.error("{} - JMS Explanation: {}", methodName, jed.getExplanation());
                    	logger.error("{} - JMS User Action: {}", methodName, jed.getUserAction());
                    }                   
                } else if (t instanceof MQException) {
                    MQException mqe = (MQException) t;
                    logger.error("{} - WMQ Completion code: {}" , methodName, mqe.getCompCode());
                    logger.error("{} - WMQ Reason code: {}" , methodName, mqe.getReason());
                } else if (t instanceof JmqiException){
                    JmqiException jmqie = (JmqiException)t;
                    logger.error("{} - WMQ Log Message: {}", methodName, jmqie.getWmqLogMessage());
                    logger.error("{} - WMQ Explanation: {}", methodName, jmqie.getWmqMsgExplanation());
                    logger.error("{} - WMQ Msg Summary: {}", methodName, jmqie.getWmqMsgSummary());
                    logger.error("{} - WMQ Msg User Response: "
                                       + jmqie.getWmqMsgUserResponse());
                    logger.error("{} - WMQ Msg Severity: {}", methodName, jmqie.getWmqMsgSeverity());
                }
                                  	 
        	});
        }
        return mqQueueConnectionFactory;
    }  
}
