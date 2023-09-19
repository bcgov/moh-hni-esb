package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CUSTOM_ERROR_MQ_NOT_ENABLED;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType.E45;
import static ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType.R15;
import static ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType.R50;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_CERT;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_CERT_PASSWORD;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_ELIGIBILITY_PASSWORD;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_ELIGIBILITY_PATH;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_ELIGIBILITY_USER;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_ENROLLMENT_PASSWORD;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_ENROLLMENT_PATH;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_ENROLLMENT_USER;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_HTTP_URI;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_REPLY_QUEUE;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_REQUEST_QUEUE;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.IS_MQ_ENABLED;
import static org.apache.camel.component.http.HttpMethods.POST;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.builder.PredicateBuilder;
import org.apache.camel.support.jsse.SSLContextParameters;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateJMSMessageHeader;
import ca.bc.gov.hlth.hnsecure.parsing.ProtocolEvaluator;
import ca.bc.gov.hlth.hnsecure.parsing.Util;


public class HIBCRoute extends BaseRoute {

	private static final String DIRECT_AUDIT = "direct:audit";
	private static final String DIRECT_HIBC_MQ = "direct:hibcMQ";
	private static final String DIRECT_HIBC_HTTP = "direct:hibcHTTP";

	private static final String SSL_CONTEXT_HIBC = "ssl_context_hibc";

	private static final String CAMEL_HTTP_METHOD = "CamelHttpMethod";

	@Override
	public void configure() throws Exception {

		boolean isMQEnabled = Boolean.parseBoolean(properties.getValue(IS_MQ_ENABLED));
		String e45Protocol = properties.getValue("E45.protocol");
		String r15Protocol = properties.getValue("R15.protocol");

		// Setup web endpoint config
		setupSSLContextHibcRegistry(getContext());
		String eligibilityHttpUrl = String.format(
				properties.getValue(HIBC_HTTP_URI) + "/" + properties.getValue(HIBC_ELIGIBILITY_PATH) + "?bridgeEndpoint=true&sslContextParameters=#%s",
				SSL_CONTEXT_HIBC);
		String enrollmentHttpUrl = String.format(
				properties.getValue(HIBC_HTTP_URI) + "/" + properties.getValue(HIBC_ENROLLMENT_PATH) + "?bridgeEndpoint=true&sslContextParameters=#%s",
				SSL_CONTEXT_HIBC);
		
		// Setup authentication
		String eligibilityBasicAuthToken = RouteUtils.buildBasicAuthToken(properties.getValue(HIBC_ELIGIBILITY_USER), properties.getValue(HIBC_ELIGIBILITY_PASSWORD));
		String enrollmentBasicAuthToken = RouteUtils.buildBasicAuthToken(properties.getValue(HIBC_ENROLLMENT_USER), properties.getValue(HIBC_ENROLLMENT_PASSWORD));

		// Setup MQ config
		String hibcRequestQueue = properties.getValue(HIBC_REQUEST_QUEUE);
		String hibcReplyQueue = properties.getValue(HIBC_REPLY_QUEUE);
		String hibcMqUrl = String.format(MQ_URL_FORMAT, hibcRequestQueue, hibcReplyQueue);

		handleExceptions();

		from("direct:hibc").routeId("hibc-route")
			.process(new ProtocolEvaluator()).id("ProtocolEvaluator") // Check if message type is configured for MQ or HTTP
			.choice()
				.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_HTTP))
					.to(DIRECT_HIBC_HTTP)
				.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_MQ))
					.to(DIRECT_HIBC_MQ)
				.otherwise()
					.log("Protocol for HIBC message type ${exchangeProperty.messageType} not found or not valid. Defaulting to HTTP")
					.to(DIRECT_HIBC_HTTP)
			.end();

		from(DIRECT_HIBC_HTTP).routeId("hibc-http-route")
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	     	.wireTap(DIRECT_AUDIT).end()
	     	.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
	        .setHeader(CAMEL_HTTP_METHOD, POST)
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
            .choice()
			.when(isEligibility())
				.setHeader(AUTHORIZATION, simple(eligibilityBasicAuthToken))
				.log("Sending to Eligibility endpoint")
				.to(eligibilityHttpUrl).id("ToHibcEligibility")
			.when(isEnrollment())
				.setHeader(AUTHORIZATION, simple(enrollmentBasicAuthToken))
				.log("Sending to Enrollment endpoint")
				.to(enrollmentHttpUrl).id("ToHibcEnrollment")
            .otherwise()
            	.log("Found unexpected message of type: ${exchangeProperty.messageType}")
            .end()
            .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
			.convertBodyTo(String.class)
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	     	.wireTap(DIRECT_AUDIT).end();

		// Don't start up this MQ route if neither E45 nor R15 will use it
		if (isMQEnabled && (e45Protocol.equals(Util.PROTOCOL_MQ) || r15Protocol.equals(Util.PROTOCOL_MQ))) {
			from(DIRECT_HIBC_MQ).routeId("hibc-mq-route")
		        .log(String.format("Processing HIBC messages. Request Queue : %s, ReplyQ: %s", hibcRequestQueue, hibcReplyQueue))
		        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
		        .bean(new PopulateJMSMessageHeader()).id("PopulateJMSMessageHeaderHIBC")
				.log("HIBC request message ::: ${body}")
				.setHeader("CamelJmsDestinationName", constant(String.format(JMS_DESTINATION_NAME_FORMAT, hibcRequestQueue)))	           		        	
		    	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
		    	.wireTap(DIRECT_AUDIT).end()
				.to(hibcMqUrl).id("ToHibcMqUrl")
				.removeHeaders("JMS*")	
		        .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
		        .wireTap(DIRECT_AUDIT).end()
		        .log("Received response message from HIBC queue ::: ${body}");
		} else {
			from(DIRECT_HIBC_MQ).routeId("hibc-mq-route")
	    	.log("MQ routes are disabled.")
	    	.throwException(new CustomHNSException(CUSTOM_ERROR_MQ_NOT_ENABLED));
		}
	}

	private void setupSSLContextHibcRegistry(CamelContext camelContext) {
		SSLContextParameters sslContextParameters = RouteUtils.setupSslContextParameters(properties.getValue(HIBC_CERT), properties.getValue(HIBC_CERT_PASSWORD));

		Registry registry = camelContext.getRegistry();
		registry.bind(SSL_CONTEXT_HIBC, sslContextParameters);
	}
	
	private Predicate isEligibility() {
		Predicate isE45 = exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(E45);	
		Predicate isR15 = exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(R15);
		
		return PredicateBuilder.or(isR15, isE45);
	}
    
	private Predicate isEnrollment() {
		return exchangeProperty(Util.PROPERTY_MESSAGE_TYPE).isEqualToIgnoreCase(R50);
	}
    
}