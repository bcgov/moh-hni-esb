/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;


/**
 * @author Tony.Ma
 * @date Feb.3, 2021
 *
 */
public class PopulateReqHeader {
	private static final Logger logger = LoggerFactory.getLogger(PopulateReqHeader.class);
	public static final String RECEIVING_APP = "receivingApp";
	public static final String MESSAGE_TYPE = "messageType";
	public static final String SENDING_FACILITY = "sendingFacility";

	/**
	 * This method is for parsing V2 message to set the HTTP headers.
	 * 
	 * @param exchange
	 * @param hm
	 * @param v2Message
	 * @throws Exception
	 */
	@Handler
	public void populateReqHeader(Exchange exchange, @Headers Map<String, Object> hm, String v2Message)
			throws Exception {
		final String methodName = LoggingUtil.getMethodName();
		String recApp = Util.getReceivingApp(v2Message);
		String msgType = Util.getMsgType(v2Message);
		String accessToken = (String) exchange.getIn().getHeader(AUTHORIZATION);
		String sendingFacility = Util.getSendingFacility(accessToken);
		hm.put(RECEIVING_APP, recApp);
		hm.put(MESSAGE_TYPE, msgType);
		hm.put(SENDING_FACILITY, sendingFacility);
		hm.put(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK_200);

		logger.info("{} - TransactionId: {}, The exchange id is : {}, The receiving application is : {},The transaction type is :{} ", 
				methodName, exchange.getExchangeId(),
				exchange.getIn().getMessageId(),  recApp , msgType);
	}
	
	
}
