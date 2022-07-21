package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CUSTOM_ERROR_MQ_NOT_ENABLED;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.*;
import static org.apache.camel.component.http.HttpMethods.POST;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateJMSMessageHeader;
import ca.bc.gov.hlth.hnsecure.parsing.ProtocolEvaluator;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.jsse.SSLContextParameters;


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
		String r15Protocol = properties.getValue("E45.protocol");

		// Setup web endpoint config
		setupSSLContextHibcRegistry(getContext());
		String hibcHttpUrl = String.format(
				properties.getValue(HIBC_HTTP_URI) + "?bridgeEndpoint=true&sslContextParameters=#%s&authMethod=Basic&authUsername=%s&authPassword=%s",
				SSL_CONTEXT_HIBC, properties.getValue(HIBC_USER), properties.getValue(HIBC_PASSWORD));
		String basicAuthToken = RouteUtils.buildBasicAuthToken(properties.getValue(HIBC_USER), properties.getValue(HIBC_PASSWORD));

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
			.setHeader(AUTHORIZATION, simple(basicAuthToken))
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
	     	.to(hibcHttpUrl).id("ToHibcHttpUrl")
			.to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
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
	
}