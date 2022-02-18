package ca.bc.gov.hlth.hnsecure.routes;

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
     		.log("Message identified as RTrans message. Preparing message for RTrans.")
     		.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")           		
     		.setBody().method(new FormatRTransMessage()).id("FormatRTransMessage")
     		.log("Sending to RTrans")
     		.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
     		.wireTap("direct:audit").end()
     		.to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
     		.to(rTransUrl).id("ToRTrans")     		
     		.setBody().method(new FormatRTransResponse()).id("FormatRTransResponse")
     		.log("Received response from RTrans: ${body}")
     		.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
     		.wireTap("direct:audit").end();
	}

}
