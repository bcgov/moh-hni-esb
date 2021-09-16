package ca.bc.gov.hlth.hnsecure;

import org.apache.camel.builder.RouteBuilder;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateJMSMessageHeader;

public class JMBRoute extends RouteBuilder implements MQRoute {

	@Override
	public void configure() throws Exception {
		
		String jmbUrl = String.format(MQ_URL_FORMAT, System.getenv("JMB_REQUEST_QUEUE"), System.getenv("JMB_REPLY_QUEUE"));

		from("direct:jmb").routeId("jmb-route")
	    	.log("Processing MQ Series. RequestQ : ${sysenv.JMB_REQUEST_QUEUE}, ReplyQ:${sysenv.JMB_REPLY_QUEUE}")
	        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
	        .bean(new PopulateJMSMessageHeader()).id("PopulateJMSMessageHeader")
			.log("jmb request message for R32 ::: ${body}")
			.setHeader("CamelJmsDestinationName", constant(String.format(JMS_DESTINATION_NAME_FORMAT, System.getenv("JMB_REQUEST_QUEUE"))))  
	    	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	    	.wireTap("direct:audit").end()
			.to(jmbUrl).id("ToJmbUrl")
	        .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	        .wireTap("direct:audit").end()
	        .log("Received response message for R32 ::: ${body}");
	}
	
}