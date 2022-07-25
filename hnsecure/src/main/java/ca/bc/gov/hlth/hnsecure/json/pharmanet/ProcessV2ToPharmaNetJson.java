package ca.bc.gov.hlth.hnsecure.json.pharmanet;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.HL7_ERROR_NO_INPUT;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;

/**
 * Class to set the exchange body with JSON for PharmaNet
 *
 */
public class ProcessV2ToPharmaNetJson {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessV2ToPharmaNetJson.class);
	
	/**
	 * Processes the message exchange by setting the exchange body with JSON for PharmaNet using the incoming exchange body and the exchange ID as the transaction UUID
	 * 
     * @param exchange the message exchange
	 * @return 
     * @throws CustomHNSException if an internal processing error has occurred.
	 */
	@Handler
	public String processV2ToPharmaNetJson(Exchange exchange) throws CustomHNSException {
		final String methodName = LoggingUtil.getMethodName();

		Object exchangeBody = exchange.getIn().getBody();

		// It should be impossible for the body to be empty here (the handshake server or base64 encoder should catch that) but handle it just in case
		if (exchangeBody == null || StringUtil.isBlank(exchangeBody.toString())) {
			throw new CustomHNSException(HL7_ERROR_NO_INPUT);
		} else {
			String message = exchangeBody.toString();							
			String transactionUUID = exchange.getExchangeId();
			logger.info("{} - transactionUUID: {}", methodName, transactionUUID);
			
			return PharmaNetJsonUtil.createJsonObjectPharmanet(transactionUUID, message).toString();
		}
	}
	
}
