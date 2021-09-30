package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CustomError_Msg_MQNotEnabled;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.IS_MQ_ENABLED;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.JMB_REPLY_QUEUE;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.JMB_REQUEST_QUEUE;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateJMSMessageHeader;

public class JMBRoute extends BaseRoute {

	@Override
	public void configure() throws Exception {
		
		String isMQEnabled = properties.getValue(IS_MQ_ENABLED);
		String jmbRequestQueue = properties.getValue(JMB_REQUEST_QUEUE);
		String jmbReplyQueue = properties.getValue(JMB_REPLY_QUEUE);
		String jmbUrl = String.format(MQ_URL_FORMAT, jmbRequestQueue, jmbReplyQueue);
		
		handleExceptions();

		if (Boolean.valueOf(isMQEnabled)) {
			from("direct:jmb").routeId("jmb-route")
		    	.log(String.format("Processing MQ Series. RequestQ : %s, ReplyQ: %s", jmbRequestQueue, jmbReplyQueue))
		        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
		        .bean(new PopulateJMSMessageHeader()).id("PopulateJMSMessageHeader")
				.log("jmb request message for R32 ::: ${body}")
				.setHeader("CamelJmsDestinationName", constant(String.format(JMS_DESTINATION_NAME_FORMAT, jmbRequestQueue)))  
		    	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
		    	.wireTap("direct:audit").end()
				.to(jmbUrl).id("ToJmbUrl")
		        .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
		        .wireTap("direct:audit").end()
		        .log("Received response message for R32 ::: ${body}");
		} else {
			from("direct:jmb").routeId("jmb-route")
	    		.log("MQ routes are disabled.")
		    	.throwException(new CustomHNSException(CustomError_Msg_MQNotEnabled));
		} 
	}
	
}