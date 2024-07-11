package ca.bc.gov.hlth.hnsecure.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.json.Base64Encoder;
import ca.bc.gov.hlth.hnsecure.json.fhir.ProcessV2ToJson;
import ca.bc.gov.hlth.hnsecure.parsing.Util;

public class HandleResponseRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("direct:handleResponse").routeId("handle-response-route")
			// create filedrops if enabled
			.wireTap("direct:responseFileDrop").end()
	        // Audit "Transaction Complete"
			.process(new AuditSetupProcessor(TransactionEventType.TRANSACTION_COMPLETE))
	        .wireTap("direct:audit").end()
			//encoding response before sending to consumer
			.setBody().method(new Base64Encoder()).id("Base64Encoder")
			.setBody().method(new ProcessV2ToJson()).id("ProcessV2ToJson")
			// Set any final headers
			.removeHeader(Util.AUTHORIZATION)
			.setHeader(Exchange.CONTENT_TYPE, constant(Util.MEDIA_TYPE_FHIR_JSON))
			.log(LoggingLevel.INFO, "TransactionId: ${exchange.exchangeId}, Completed.");
	}

}
