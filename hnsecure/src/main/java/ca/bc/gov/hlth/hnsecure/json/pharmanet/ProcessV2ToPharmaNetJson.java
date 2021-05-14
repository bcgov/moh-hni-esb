package ca.bc.gov.hlth.hnsecure.json.pharmanet;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to set the exchange body with JSON for PharmaNet
 * 
 * @author dave.p.barrett
 *
 */
public class ProcessV2ToPharmaNetJson {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessV2ToPharmaNetJson.class);
	
	/**
	 * Processes the message exchange by setting the exchange body with JSON for PharmaNet using the incoming exchange body and the exchange ID as the transaction UUID
	 * 
     * @param exchange the message exchange
	 * @return 
     * @throws Exception if an internal processing error has occurred.
	 */
	@Handler
	public String processV2ToPharmaNetJson(Exchange exchange) throws Exception {

		Object exchangeBody = exchange.getIn().getBody();

		// It should be impossible for the body to be empty here (the handshake server or base64 encoder should catch that) but handle it just in case
		if (exchangeBody == null || StringUtil.isBlank(exchangeBody.toString())) {
			// TODO (dbarrett) change to throw an exception that causes an HL7Error_Msg_NoInputHL7 response if it is empty. ca.bc.gov.hlth.hnclientv2.error.NoInputHL7Exception can be used when it is moved to common code project
			throw new IllegalArgumentException("v2Message can't be null or empty");
		} else {
			String message = exchangeBody.toString();							
			String transactionUUID = exchange.getExchangeId();
			logger.info(String.format("Transaction UUID: %s", transactionUUID));
			return PharmaNetJsonUtil.createJsonObjectPharmanet(transactionUUID, message).toString();
		}
	}
	
}
