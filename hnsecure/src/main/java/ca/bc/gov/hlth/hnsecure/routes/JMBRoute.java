package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CustomError_Msg_MQNotEnabled;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.IS_MQ_ENABLED;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.JMB_REPLY_QUEUE;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.JMB_REQUEST_QUEUE;
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

public class JMBRoute extends BaseRoute {

	@Override
	public void configure() throws Exception {
		String jmbHttpUrl = String.format(properties.getValue(ApplicationProperty.JMB_HTTP_URI) + "?bridgeEndpoint=true");
		
		String isMQEnabled = properties.getValue(IS_MQ_ENABLED);
		String jmbRequestQueue = properties.getValue(JMB_REQUEST_QUEUE);
		String jmbReplyQueue = properties.getValue(JMB_REPLY_QUEUE);
		String jmbUrl = String.format(MQ_URL_FORMAT, jmbRequestQueue, jmbReplyQueue);
		
		handleExceptions();
		
		from("direct:jmb").routeId("jmb-route")
		.process(new ProtocolEvaluator()).id("JmbProtocolEvaluator")
		.choice()
			.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_HTTP))
				.to("direct:jmbHTTP")
			.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_MQ))
				.to("direct:jmbMQ")
			.otherwise()
				.log("Protocol for JMB message type ${exchangeProperty.messageType} not found or not valid. Defaulting to MQ")
				.to("direct:jmbMQ")
		.end();
		
		// XXX This is currently just a simple HTTP route which will need additional configuration (auth, request/response conversion)
		// once the endpoint is available
		from("direct:jmbHTTP").routeId("jmb-http-route")
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")           		
	     	.log("Sending to JMB")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	     	.wireTap("direct:audit").end()
	     	.setBody().method(new Base64Encoder()).id("JMBBase64Encoder")
            .setBody().method(new ProcessV2ToJson()).id("JMBProcessV2ToJson")
	     	.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
	        .setHeader("CamelHttpMethod", POST)
	     	.to("log:HttpLogger?level=INFO&showBody=true&showHeaders=true&multiline=true")
	     	.to(jmbHttpUrl).id("ToJmbHttpUrl")
	     	.log("Received response from JMB for R32")
	     	.setBody().method(new FhirPayloadExtractor())
	     	.log("Decoded V2: ${body}")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	     	.wireTap("direct:audit").end();

		if (Boolean.valueOf(isMQEnabled)) {
			from("direct:jmbMQ").routeId("jmb-mq-route")
		    	.log(String.format("Processing MQ Series for ${exchangeProperty.messageType}. Request Queue : %s, Reply Queue: %s", jmbRequestQueue, jmbReplyQueue))
		        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
		        .bean(new PopulateJMSMessageHeader()).id("JmbPopulateJMSMessageHeader")
				.log("jmb request message for R32 ::: ${body}")
				.setHeader("CamelJmsDestinationName", constant(String.format(JMS_DESTINATION_NAME_FORMAT, jmbRequestQueue)))  
		    	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
		    	.wireTap("direct:audit").end()
				.to(jmbUrl).id("ToJmbUrl")				
				.removeHeaders("JMS*")				
		        .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
		        .wireTap("direct:audit").end()
		        .log("Received response message for ${exchangeProperty.messageType} ::: ${body}");
		} else {
			from("direct:jmbMQ").routeId("jmb-mq-route")
	    		.log("MQ routes are disabled.")
		    	.throwException(new CustomHNSException(CustomError_Msg_MQNotEnabled));
		} 
	}
	
}