package ca.bc.gov.hlth.hnsecure.routes;

import static org.apache.camel.component.http.HttpMethods.POST;

import org.apache.camel.Exchange;
import org.apache.camel.PropertyInject;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.json.Base64Encoder;
import ca.bc.gov.hlth.hnsecure.json.fhir.ProcessV2ToJson;
import ca.bc.gov.hlth.hnsecure.parsing.FhirPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateJMSMessageHeader;
import ca.bc.gov.hlth.hnsecure.parsing.ProtocolEvaluator;
import ca.bc.gov.hlth.hnsecure.parsing.Util;

public class HIBCRoute extends BaseRoute {

	@PropertyInject(value = "hibc.uri")
    private String hibcUri;

	@Override
	public void configure() throws Exception {

		String hibcMqUrl = String.format(MQ_URL_FORMAT, System.getenv("HIBC_REQUEST_QUEUE"), System.getenv("HIBC_REPLY_QUEUE"));
		
		String hibcHttpUrl = String.format(hibcUri + "?bridgeEndpoint=true");
		
		handleExceptions();

		from("direct:hibc").routeId("hibc-route")
			.process(new ProtocolEvaluator()).id("ProtocolEvaluator")
			.choice()
				.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_HTTP))
					.to("direct:hibcHTTP")
				.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_MQ))
					.to("direct:hibcMQ")
				.otherwise()
					.log("Protocol for HIBC message type ${exchangeProperty.messageType} not found or not valid. Defaulting to MQ")
					.to("direct:hibcMQ")
			.end();
		// XXX This is currently just a simple HTTP route which will need additional configuration (auth, request/response conversion)
		// once the endpoint is available
		from("direct:hibcHTTP").routeId("hibc-http-route")
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")           		
	     	.log("Sending to HIBC")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	     	.wireTap("direct:audit").end()
	     	.setBody().method(new Base64Encoder()).id("HIBCBase64Encoder")
            .setBody().method(new ProcessV2ToJson()).id("HIBCProcessV2ToJson")
	     	.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
	        .setHeader("CamelHttpMethod", POST)
	     	.to("log:HttpLogger?level=INFO&showBody=true&showHeaders=true&multiline=true")
	     	.to(hibcHttpUrl).id("ToHibcHttpUrl")
	     	.log("Received response from HIBC")
	     	.setBody().method(new FhirPayloadExtractor())
	     	.log("Decoded V2: ${body}")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	     	.wireTap("direct:audit").end();

		from("direct:hibcMQ").routeId("hibc-mq-route")
	        .log("Processing HIBC messages. Request Queue : ${sysenv.HIBC_REQUEST_QUEUE}, ReplyQ:${sysenv.HIBC_REPLY_QUEUE}")
	        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
	        .bean(new PopulateJMSMessageHeader()).id("PopulateJMSMessageHeaderHIBC")
			.log("HIBC request message ::: ${body}")
			.setHeader("CamelJmsDestinationName", constant(String.format(JMS_DESTINATION_NAME_FORMAT, System.getenv("HIBC_REQUEST_QUEUE"))))	           		        	
	    	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	    	.wireTap("direct:audit").end()
			.to(hibcMqUrl).id("ToHibcMqUrl")
	        .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	        .wireTap("direct:audit").end()
	        .log("Received response message from HIBC queue ::: ${body}");
	}
	
}