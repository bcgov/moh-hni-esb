package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CUSTOM_ERROR_MQ_NOT_ENABLED;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_REPLY_QUEUE;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.HIBC_REQUEST_QUEUE;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.IS_MQ_ENABLED;
import static org.apache.camel.component.http.HttpMethods.POST;

import org.apache.camel.Exchange;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.json.Base64Encoder;
import ca.bc.gov.hlth.hnsecure.json.fhir.ProcessV2ToJson;
import ca.bc.gov.hlth.hnsecure.parsing.FhirPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateJMSMessageHeader;
import ca.bc.gov.hlth.hnsecure.parsing.ProtocolEvaluator;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;

public class HIBCRoute extends BaseRoute {

	private static final String DIRECT_AUDIT = "direct:audit";
	private static final String DIRECT_HIBC_MQ = "direct:hibcMQ";

	@Override
	public void configure() throws Exception {

		String hibcHttpUrl = String.format(properties.getValue(ApplicationProperty.HIBC_HTTP_URI) + "?bridgeEndpoint=true");//NOSONAR
		
		boolean isMQEnabled = Boolean.parseBoolean(properties.getValue(IS_MQ_ENABLED));		
		String hibcRequestQueue = properties.getValue(HIBC_REQUEST_QUEUE);
		String hibcReplyQueue = properties.getValue(HIBC_REPLY_QUEUE);
		String hibcMqUrl = String.format(MQ_URL_FORMAT, hibcRequestQueue, hibcReplyQueue);

		handleExceptions();

		from("direct:hibc").routeId("hibc-route")
			.process(new ProtocolEvaluator()).id("ProtocolEvaluator")
			.choice()
				.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_HTTP))
					.to("direct:hibcHTTP")
				.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_MQ))
					.to(DIRECT_HIBC_MQ)
				.otherwise()
					.log("Protocol for HIBC message type ${exchangeProperty.messageType} not found or not valid. Defaulting to MQ")
					.to(DIRECT_HIBC_MQ)
			.end();

		// XXX This is currently just a simple HTTP route which will need additional configuration (auth, request/response conversion)
		// once the endpoint is available
		from("direct:hibcHTTP").routeId("hibc-http-route")
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")           		
	     	.log("Sending to HIBC")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	     	.wireTap(DIRECT_AUDIT).end()
	     	.setBody().method(new Base64Encoder()).id("HIBCBase64Encoder")
            .setBody().method(new ProcessV2ToJson()).id("HIBCProcessV2ToJson")
	     	.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
	        .setHeader("CamelHttpMethod", POST)
	     	.to("log:HttpLogger?level=INFO&showBody=true&showHeaders=true&multiline=true")
	     	.to(hibcHttpUrl).id("ToHibcHttpUrl")
	     	.log("Received response from HIBC")
	     	.bean(FhirPayloadExtractor.class)
	     	.log("Decoded V2: ${body}")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	     	.wireTap(DIRECT_AUDIT).end();

		if (isMQEnabled) {
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
	
}