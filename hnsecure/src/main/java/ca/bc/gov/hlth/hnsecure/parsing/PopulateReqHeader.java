/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tony.Ma
 * @date Feb.3, 2021
 *
 */
public class PopulateReqHeader {
	private static final Logger logger = LoggerFactory.getLogger(PopulateReqHeader.class);
	public static final String RECEIVING_APP = "receivingApp";
	public static final String MESSAGE_TYPE = "messageType";

	/**
	 * This method is for parsing V2 message to set the HTTP headers.
	 * 
	 * @param exchange
	 * @param hm
	 * @param v2Message
	 * @throws Exception
	 */
	@Handler
	public void populateReqHeader(Exchange exchange, @Headers Map<String, String> hm, String v2Message)
			throws Exception {
		String methodName = "populateReqHeader";
		String recApp = Util.getReceivingApp(v2Message);
		String msgType = Util.getMsgType(v2Message);
		hm.put(RECEIVING_APP, recApp);
		hm.put(MESSAGE_TYPE, msgType);
		logger.info("{} - TransactionId: {}, The exchange id is : {}, The receiving application is : {},The transaction type is :{} ", 
				methodName, exchange.getExchangeId(),
				exchange.getIn().getMessageId(),  recApp , msgType);
	}
}
