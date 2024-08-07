package ca.bc.gov.hlth.hnsecure.routes;

import org.apache.camel.LoggingLevel;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.parsing.FormatRTransMessage;
import ca.bc.gov.hlth.hnsecure.parsing.FormatRTransResponse;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;

public class RTransRoute extends BaseRoute {
	
	@Override
	public void configure() throws Exception {
		String rTransUrl = properties.getValue(ApplicationProperty.RTRANS_URI);
		handleExceptions();
		
		from("direct:rtrans").routeId("rtrans-route")
     		.log(LoggingLevel.INFO, "TransactionId: ${exchange.exchangeId}, Preparing message for RTrans.")
     		.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")           		
     		.setBody().method(new FormatRTransMessage()).id("FormatRTransMessage")
     		.log(LoggingLevel.INFO, "TransactionId: ${exchange.exchangeId}, Sending to RTrans")
     		.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
     		.wireTap("direct:audit").end()
     		.to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
     		.to(rTransUrl).id("ToRTrans")     		
     		.setBody().method(new FormatRTransResponse()).id("FormatRTransResponse")
     		.log(LoggingLevel.INFO, "TransactionId: ${exchange.exchangeId}, Received response from RTrans.")
     		.log(LoggingLevel.DEBUG, "TransactionId: ${exchange.exchangeId}, Response from RTrans is: ${body}")
     		.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
     		.wireTap("direct:audit").end();
	}

}
