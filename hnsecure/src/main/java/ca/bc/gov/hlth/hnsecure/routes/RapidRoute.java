package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CUSTOM_ERROR_MQ_NOT_ENABLED;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.RAPID_CERT;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.RAPID_CERT_PASSWORD;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.RAPID_HTTP_URI;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.RAPID_PASSWORD;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.RAPID_R32_PATH;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.RAPID_USER;
import static org.apache.camel.component.http.HttpMethods.POST;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.jsse.SSLContextParameters;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateJMSMessageHeader;
import ca.bc.gov.hlth.hnsecure.parsing.ProtocolEvaluator;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;
import ca.bc.gov.hlth.hnsecure.rapid.RPBSPMC0RequestConverter;
import ca.bc.gov.hlth.hnsecure.rapid.RPBSPMC0ResponseConverter;


public class RapidRoute extends BaseRoute {

	private static final String DIRECT_AUDIT = "direct:audit";
	private static final String DIRECT_JMB_MQ = "direct:jmbMQ";
	private static final String DIRECT_RAPID_HTTP = "direct:rapidHTTP";
	
	private static final String SSL_CONTEXT_RAPID = "ssl_context_rapid";

	private static final String CAMEL_HTTP_METHOD = "CamelHttpMethod";
	

	@Override
	public void configure() throws Exception {
		String r32Protocol = properties.getValue("R32.protocol");
		
		// Setup web endpoint config
		setupSSLContextRapidRegistry(getContext());
		
		String rapidHttpUrl = String.format(
				properties.getValue(RAPID_HTTP_URI) + "/" + properties.getValue(RAPID_R32_PATH) + "?bridgeEndpoint=true&sslContextParameters=#%s",
				SSL_CONTEXT_RAPID);
		String basicAuthToken = RouteUtils.buildBasicAuthToken(properties.getValue(RAPID_USER), properties.getValue(RAPID_PASSWORD));

		// Setup MQ config
		boolean isMQEnabled = Boolean.parseBoolean(properties.getValue(ApplicationProperty.IS_MQ_ENABLED));	
		String jmbRequestQueue = properties.getValue(ApplicationProperty.JMB_REQUEST_QUEUE);
		String jmbReplyQueue = properties.getValue(ApplicationProperty.JMB_REPLY_QUEUE);
		String jmbUrl = String.format(MQ_URL_FORMAT, jmbRequestQueue, jmbReplyQueue);
		
		
		handleExceptions();
		
		from("direct:rapid").routeId("rapid-route")
		.process(new ProtocolEvaluator()).id("rapidProtocolEvaluator")
		.choice()
			.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_HTTP))
				.to(DIRECT_RAPID_HTTP)
			.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_MQ))
				.to(DIRECT_JMB_MQ)
			.otherwise()
				.log("Protocol for message type ${exchangeProperty.messageType} not found or not valid. Defaulting to HTTP")
				.to(DIRECT_RAPID_HTTP)
		.end();
	
		from(DIRECT_RAPID_HTTP).routeId("rapid-http-route")		
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")           		
	     	.log("Sending to RAPID")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	        .setHeader(CAMEL_HTTP_METHOD, POST)
			.setHeader(AUTHORIZATION, simple(basicAuthToken))
			.setBody().method(new RPBSPMC0RequestConverter()).id("rapidRequest")
	     	.to("log:HttpLogger?level=INFO&showBody=true&showHeaders=true&multiline=true")
	     	.to(rapidHttpUrl).id("TorapidHttpUrl")
	     	.log("Received response from RAPID for R32")
	     	.setBody().method(new RPBSPMC0ResponseConverter()).id("rapidResponse")	     	
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	     	.wireTap(DIRECT_AUDIT).end();

		if (isMQEnabled && r32Protocol.equals(Util.PROTOCOL_MQ)) {
			from(DIRECT_JMB_MQ).routeId("jmb-mq-route")
		    	.log(String.format("Processing MQ Series for ${exchangeProperty.messageType}. Request Queue : %s, Reply Queue: %s", jmbRequestQueue, jmbReplyQueue))
		        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
		        .bean(new PopulateJMSMessageHeader()).id("JmbPopulateJMSMessageHeader")
				.log("jmb request message for R32 ::: ${body}")
				.setHeader("CamelJmsDestinationName", constant(String.format(JMS_DESTINATION_NAME_FORMAT, jmbRequestQueue)))  
		    	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
		    	.wireTap(DIRECT_AUDIT).end()
				.to(jmbUrl).id("ToJmbUrl")				
				.removeHeaders("JMS*")				
		        .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
		        .wireTap(DIRECT_AUDIT).end()
		        .log("Received response message for ${exchangeProperty.messageType} ::: ${body}");
		} else {
			from(DIRECT_JMB_MQ).routeId("jmb-mq-route")
	    		.log("MQ routes are disabled.")
		    	.throwException(new CustomHNSException(CUSTOM_ERROR_MQ_NOT_ENABLED));
		} 
	}
	
	private void setupSSLContextRapidRegistry(CamelContext camelContext) {
		SSLContextParameters sslContextParameters = RouteUtils.setupSslContextParameters(properties.getValue(RAPID_CERT), properties.getValue(RAPID_CERT_PASSWORD));

		Registry registry = camelContext.getRegistry();
		registry.bind(SSL_CONTEXT_RAPID, sslContextParameters);
	}
	
}