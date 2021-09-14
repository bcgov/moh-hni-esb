package ca.bc.gov.hlth.hnsecure;

import org.apache.camel.builder.RouteBuilder;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateJMSMessageHeader;
import ca.bc.gov.hlth.hnsecure.parsing.ProtocolEvaluator;

public class HIBCRoute extends RouteBuilder implements MQRoute {

	@Override
	public void configure() throws Exception {

		String hibcUrl = String.format(MQ_URL_FORMAT, System.getenv("HIBC_REQUEST_QUEUE"), System.getenv("HIBC_REPLY_QUEUE"));

		from("direct:hibc").routeId("hibc-route")
			.process(new ProtocolEvaluator()).id("ProtocolEvaluator")
			.choice()
				.when(exchangeProperty("MESSAGE_PROTOCOL").isEqualTo("HTTP"))
					.to("direct:hibcHTTP")
				.when(exchangeProperty("MESSAGE_PROTOCOL").isEqualTo("MQ"))
					.to("direct:hibcMQ")
				.otherwise()
					.log("Protocol for message type ${exchangeProperty.messageType} not found. Defaulting to MQ")
					.to("direct:hibcMQ")
			.end();

		from("direct:hibcHTTP")
	     	.log("Message identified as RTrans message. Preparing message for RTrans.")
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")           		
	     	.log("Sending to HIBC")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	     	.wireTap("direct:audit").end()
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")		            
	     	.to("{{hibc.uri}}").id("ToHibcHTTP")
	     	.log("Received response from HIBC: ${body}")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	     	.wireTap("direct:audit").end();

		from("direct:hibcMQ")
	        .log("Processing HIBC messages. Request Queue : ${sysenv.HIBC_REQUEST_QUEUE}, ReplyQ:${sysenv.HIBC_REPLY_QUEUE}")
	        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
	        .bean(new PopulateJMSMessageHeader()).id("PopulateJMSMessageHeaderHIBC")
			.log("HIBC request message ::: ${body}")
			.setHeader("CamelJmsDestinationName", constant(String.format(JMS_DESTINATION_NAME_FORMAT, System.getenv("HIBC_REQUEST_QUEUE"))))	           		        	
	    	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	    	.wireTap("direct:audit").end()
			.to(hibcUrl).id("ToHIBCUrl")
	        .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	        .wireTap("direct:audit").end()
	        .log("Received response message from HIBC queue ::: ${body}");
	}
	
}