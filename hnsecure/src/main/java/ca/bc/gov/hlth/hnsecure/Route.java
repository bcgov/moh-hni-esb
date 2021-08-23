package ca.bc.gov.hlth.hnsecure;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType.E45;
import static ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType.R15;
import static ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType.R50;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.IS_AUDITS_ENABLED;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.IS_FILEDDROPS_ENABLED;
import static org.apache.camel.component.http.HttpMethods.POST;

import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Properties;

import javax.jms.JMSException;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.Predicate;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.builder.PredicateBuilder;
import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.AuditProcessor;
import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.exception.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.filedrops.RequestFileDropGenerater;
import ca.bc.gov.hlth.hnsecure.filedrops.ResponseFileDropGenerater;
import ca.bc.gov.hlth.hnsecure.json.Base64Encoder;
import ca.bc.gov.hlth.hnsecure.json.fhir.ProcessV2ToJson;
import ca.bc.gov.hlth.hnsecure.json.pharmanet.ProcessV2ToPharmaNetJson;
import ca.bc.gov.hlth.hnsecure.messagevalidation.ExceptionHandler;
import ca.bc.gov.hlth.hnsecure.parsing.FhirPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.FormatRTransMessage;
import ca.bc.gov.hlth.hnsecure.parsing.PharmaNetPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateJMSMessageHeader;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateReqHeader;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.temporary.samplemessages.SampleMessages;
import ca.bc.gov.hlth.hnsecure.validation.PayLoadValidator;
import ca.bc.gov.hlth.hnsecure.validation.TokenValidator;
import ca.bc.gov.hlth.hnsecure.validation.Validator;
import ca.bc.gov.hlth.hnsecure.validation.ValidatorImpl;

public class Route extends RouteBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(Route.class);
	// HTTP Status codes for which the onCompletion logic will be invoked
	private static final String HTTP_STATUS_CODES_COMPLETION_REGEX = "^[245][0-9][0-9]$";

	private static final String KEY_STORE_TYPE_PKCS12 = "PKCS12";
    
    private static final String SSL_CONTEXT_PHARMANET = "ssl_context_pharmanet";

	private static final String CAMEL_HTTP_METHOD = "CamelHttpMethod";

	private static final String BASIC = "Basic ";

	private static final String HTTP_REQUEST_ID_HEADER = "X-Request-Id";

    // PharmaNet Endpoint values
	@PropertyInject(value = "pharmanet.uri")
    private String pharmanetUri;
		
	// PharmaNet Endpoint values
	@PropertyInject(value = "pharmanet.cert")
	private String pharmanetCert;
	
	  //MQ info 
	  
	@PropertyInject(value = "mq.host") 
	private String host;
	  
	@PropertyInject(value = "mq.port") 
	private String port;
	  
	@PropertyInject(value = "mq.queuemanager") 
	private String queueManager;
	  
	@PropertyInject(value = "mq.channel") 
	private String channel;
	  
	@PropertyInject(value = "mq.username") 
	private String userName;
	  
	@PropertyInject(value = "mq.password") 
	private String password;
	  
	@PropertyInject(value = "jmb.request.queue") 
	private String requestQ;
	  
	@PropertyInject(value = "jmb.reply.queue")
	private String replyQ;
	     
    private static final String pharmanetCertPassword = System.getenv("PHARMANET_CERT_PASSWORD");
    
    private static final String pharmanetUser = System.getenv("PHARMANET_USER");

    private static final String pharmanetPassword = System.getenv("PHARMANET_PASSWORD");
    
	private static ApplicationProperties properties;
    
    private Validator validator;
    
    @SuppressWarnings("unchecked")
	@Override
    public void configure() {
    	  	
    	JmsComponent jmsComponent = new JmsComponent();
        try {
        	MQQueueConnectionFactory mqQueueConnectionFactory = mqQueueConnectionFactory();
        	mqQueueConnectionFactory.createConnection(userName, password);
			jmsComponent.setConnectionFactory(mqQueueConnectionFactory);
		} catch (JMSException e) {	
			logger.error("{} - MQ connection failed with the error :{}", LoggingUtil.getMethodName(), e.getLinkedException().getLocalizedMessage());
			
		}
        getContext().addComponent("jmsComponent", jmsComponent);

    	init();
		setupSSLContextPharmanetRegistry(getContext());

		String pharmaNetUrl = String.format(pharmanetUri + "?bridgeEndpoint=true&sslContextParameters=#%s&authMethod=Basic&authUsername=%s&authPassword=%s", SSL_CONTEXT_PHARMANET, pharmanetUser, pharmanetPassword);
		log.info("Using pharmaNetUrl: " + pharmaNetUrl);
				
		String basicToken = buildBasicToken(pharmanetUser, pharmanetPassword);
		String isFileDropsEnabled = properties.getValue(IS_FILEDDROPS_ENABLED);
		String isAuditsEnabled = properties.getValue(IS_AUDITS_ENABLED);
		Predicate isRTrans = isRTrans();
		Predicate isMessageForHIBC = isMessageForHIBC();
		
    	onException(CustomHNSException.class, HttpHostConnectException.class, JMSException.class,ExchangeTimedOutException.class)
        	.process(new ExceptionHandler())
        	.handled(true);
        
        onException(ValidationFailedException.class)
                .log("Validation exception response: ${body}")
                .handled(true)
                .id("ValidationException");

        from("jetty:http://{{hostname}}:{{port}}/{{endpoint}}?httpMethodRestrict=POST").routeId("hnsecure-route")

			// This route is only invoked when the original route is complete as a kind
			// of completion callback.The onCompletion method is called once per route execution.
			// Making it global will generate two response file drops.
			.onCompletion().modeBeforeConsumer().onWhen(header(Exchange.HTTP_RESPONSE_CODE).regex(HTTP_STATUS_CODES_COMPLETION_REGEX)).id("Completion")
				// create filedrops if enabled
		    	.choice().when(exchangeProperty(Util.PROPERTY_IS_FILE_DROPS_ENABLED).isEqualToIgnoreCase(Boolean.TRUE))
		    		.bean(ResponseFileDropGenerater.class).id("ResponseFileDropGenerater")
				.end()
	            // Audit "Transaction Complete"
	    		.process(new AuditSetupProcessor(TransactionEventType.TRANSACTION_COMPLETE))
	            .wireTap("direct:audit").end()
	    		//encoding response before sending to consumer
	    		.setBody().method(new Base64Encoder()).id("Base64Encoder")
	    		.setBody().method(new ProcessV2ToJson()).id("ProcessV2ToJson")
	    		// Set any final headers
	    		.removeHeader(Util.AUTHORIZATION)
	    		.setHeader(Exchange.CONTENT_TYPE, constant(Util.MEDIA_TYPE_FHIR_JSON))
	    	.end()

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
			.wireTap("direct:start").end()
        	// Validate the message
        	.process(validator).id("Validator")
            // Set the receiving app, message type into headers and properties
        	// XXX With the placement of this bean, the headers/properties aren't available to preceding Processors/Beans
        	// such as the RequestFileDropGenerator. Ideally it should be moved up but there could be some parsing issues
        	// since the validator wouldn't have been run yet
            .bean(PopulateReqHeader.class).id("PopulateReqHeader")
            .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
            .log("The message receiving application is <${in.header.receivingApp}> and the message type is <${in.header.messageType}>.")
             
            // Dispatch the message based on the receiving application code and message type
            .choice()
	            // Sending message to PharmaNet
            	.when(exchangeProperty(Util.PROPERTY_RECEIVING_APP).isEqualTo(Util.RECEIVING_APP_PNP))
                	.log("Message identified as PharmaNet message. Preparing message for PharmaNet.")
                	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
                	.wireTap("direct:audit").end()
            		.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")
            		.setBody(body().regexReplaceAll("\r\n","\r"))
                    .setBody().method(new Base64Encoder())
		            .setBody().method(new ProcessV2ToPharmaNetJson()).id("ProcessV2ToPharmaNetJson")
		            .log("Sending to Pharamanet")
		            .removeHeader(Exchange.HTTP_URI) //clean this header as it has been set in the "from" section
		            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		            .setHeader(CAMEL_HTTP_METHOD, POST)
		            .setHeader(AUTHORIZATION, simple(basicToken))
		            .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
		            .to(pharmaNetUrl).id("ToPharmaNet")
		            .log("Received response from Pharmanet")
		            .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
		            .process(new PharmaNetPayloadExtractor())
		            .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
		            .wireTap("direct:audit").end()
		            
		 	       //Sending message to RTrans     
				.when(isRTrans)
				     .log("Message identified as RTrans message. Preparing message for RTrans.")
				     .to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")           		
		             .setBody().method(new FormatRTransMessage()).id("FormatRTransMessage")
				     .log("Sending to RTrans")		            
				     .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")		            
				     .to("{{rtrans.uri}}:{{rtrans.port}}").id("ToRTrans")
				     .log("Received response from RTrans")
				     .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")

		            // sending message to HIBC for ELIG
				.when(isMessageForHIBC)
	                .log("sending to message queue(ELIG).")
	                .transform(constant("TEST MESSAGE FOR CGICHANNEL"))
	                .to("jmsComponent:queue:HNST1.HIBC.ELIG.HNST1")
	            
	            // sending message to HIBC for ENROL
	            .when(simple("${in.header.messageType} == {{hibc-r50-endpoint}}"))
	                .log("the HIBC endpoint (${in.header.messageType}) is reached and message will be dispatched to message queue(ENROL).")
                    .setBody(simple(SampleMessages.r50ResponseMessage))
                 
	            // others sending to JMB
	            .otherwise()
                    .log("the JMB endpoint is reached and message will be dispatched to JMB!!")      
                    .to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")
                    .bean(new PopulateJMSMessageHeader()).id("PopulateJMSMessageHeader")
            		.log("jmb request message for R32 ::: ${body}")             		
            		.setHeader("CamelJmsDestinationName", constant("queue:///HNST1.JMBT1R.HNST1.HNRT1?targetClient=1"))             		
            		.setHeader("JMSCorrelationID", simple("${header.JMSMessageID}"))
            		.log("CorrelationId is set to: ${header.JMSCorrelationID}")
            		.to("jmsComponent:queue:HNST1.JMBT1R.HNST1.HNRT1?useMessageIDAsCorrelationID=true&exchangePattern=InOnly&replyTo=queue:///JMB01.HNST1.HNRT1.HNST1&includeSentJMSMessageID=true&preserveMessageQos=true&transacted=true")
            		.pollEnrich().simple("jmsComponent:queue:JMB01.HNST1.HNRT1.HNST1")
            		//.timeout(30000)
            		//.to("jmsComponent:queue:HNST1.JMBT1R.HNST1.HNRT1?replyTo=queue:///JMB01.HNST1.HNRT1.HNST1&exchangePattern=InOut&requestTimeout=40s&receiveTimeout=250&useMessageIDAsCorrelationID=false")        		
                   // .log("jmb response message for R32 ::: ${body}")
                   
            	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
            	.wireTap("direct:audit")
            		.endChoice()
	            .log("Received response from JMB")
	            .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	            .wireTap("direct:audit")
	            	.endChoice()
            .end(); 
		      
        from("direct:start").log("wireTap route")
        	.choice()
				.when(exchangeProperty(Util.PROPERTY_IS_FILE_DROPS_ENABLED).isEqualToIgnoreCase(Boolean.TRUE))
					.bean(RequestFileDropGenerater.class).id("V2FileDropsRequest").log("wire tap done")
			.end();
        
		from("direct:audit").log("wireTap audit")
			.choice()
				.when(exchangeProperty(Util.PROPERTY_IS_AUDITS_ENABLED).isEqualToIgnoreCase(Boolean.TRUE.toString()))
					.process(new AuditProcessor()).log("wireTap audit done")				
			.end();
		
    }

	private String buildBasicToken(String username, String password) {
		String usernamePassword = username + ":" + password;
		Charset charSet = Charset.forName("UTF-8");
		String token = new String(Base64.getEncoder().encode(usernamePassword.getBytes(charSet)));
		String basicToken = BASIC + token;
		return basicToken;
	}

	private void setupSSLContextPharmanetRegistry(CamelContext camelContext) {
		KeyStoreParameters ksp = new KeyStoreParameters();
        ksp.setResource(pharmanetCert);
        ksp.setPassword(pharmanetCertPassword);
        ksp.setType(KEY_STORE_TYPE_PKCS12);

        KeyManagersParameters kmp = new KeyManagersParameters();
        kmp.setKeyStore(ksp);
        kmp.setKeyPassword(pharmanetCertPassword);

        SSLContextParameters sslContextParameters = new SSLContextParameters();
        sslContextParameters.setKeyManagers(kmp);

        HttpComponent httpComponent = camelContext.getComponent("https", HttpComponent.class);
        //This is important to make your cert skip CN/Hostname checks
        httpComponent.setX509HostnameVerifier(new NoopHostnameVerifier());

        Registry registry = camelContext.getRegistry();
        registry.bind(SSL_CONTEXT_PHARMANET, sslContextParameters);
        registry.bind("ssl2", new SSLContextParameters()); //TODO (dbarrett) If there is only one bound SSL context then Camel will default to always use it in every URL. This is a workaround to stop this for now. Can be removed when another endpoint is configured with it's context. 
	}
	
    /**
     * This method performs the steps required before configuring the route
     * 1. Set the transaction id generator for messages
     * 2. Load application properties
     * 3. Initializes the validator classes
     */
    private void init() {
    	//The purpose is to set custom unique id for logging
    	getContext().setUuidGenerator(new TransactionIdGenerator());
    	injectProperties();
    	properties = ApplicationProperties.getInstance();
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
		Predicate isR15 = exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(R15);
		Predicate isE45 = exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(E45);	
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
    
    public MQQueueConnectionFactory mqQueueConnectionFactory()  {
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setHostName(host);
        try {
         mqQueueConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
          mqQueueConnectionFactory.setChannel(channel);
          mqQueueConnectionFactory.setPort(Integer.valueOf(port));
          mqQueueConnectionFactory.setQueueManager(queueManager);
          
        } catch (Exception e) {
        	 logger.error(e.getMessage(), e);
        	//throw new CustomHNSException(ErrorMessage.HL7Error_Msg_MQ_NoResponseBeforeTimeout);
         
        }
        return mqQueueConnectionFactory;
      }
}
