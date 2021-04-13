package ca.bc.gov.hlth.hnscommon.json;

import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessV2ToPharmaNetJson implements Processor {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessV2ToPharmaNetJson.class);

	
	public void process(Exchange exchange) throws Exception {

		Object exchangeBody = exchange.getIn().getBody();

		// TODO it should be impossible for the body to be empty here (the handshake server or base64 encoder should catch that)
		// if we keep this, then we should throw an exception that causes an HL7Error_Msg_NoInputHL7 response if it is empty
		if (exchangeBody == null || StringUtils.isBlank(exchangeBody.toString())) {
			throw new IllegalArgumentException("v2Message can't be null or empty");
		} else {
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
			String message = exchangeBody.toString();							
			String transactionUUID = UUID.randomUUID().toString();
			logger.info(String.format("Transaction UUID: %s", transactionUUID));
			exchange.getIn().setBody(PharmaNetJsonUtil.createJsonObjectPharmanet(transactionUUID, message).toString());
		}
	}
	
}
