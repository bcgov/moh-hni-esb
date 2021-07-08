/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.HEADER_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.HEADER_RECEIVING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.HEADER_SENDING_FACILITY;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;


/**
 * @author Tony.Ma
 * @date Feb.3, 2021
 *
 */
public class PopulateReqHeader {
	private static final Logger logger = LoggerFactory.getLogger(PopulateReqHeader.class);
	

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
		hm.put(HEADER_RECEIVING_APP, recApp);
		hm.put(HEADER_MESSAGE_TYPE, msgType);
		hm.put(HEADER_SENDING_FACILITY, sendingFacility);
		hm.put(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK_200);
		
		if(StringUtils.equals(Util.MESSAGE_TYPE_PNP, msgType)) {
			String zcbSegment = Util.getZCBSegment(v2Message,Util.ZCB_SEGMENT);
			String pharmacyID = Util.getPharmacyId(zcbSegment);
			String traceID = Util.getTraceNumber(zcbSegment);
			hm.put(Util.PHARMACY_ID, pharmacyID);
			hm.put(Util.TRACING_ID, traceID);
		}

		logger.info("{} - Transaction Id : {}, Receiving application : {}, Transaction type : {}, Sending Facility : {} ",
				methodName, exchange.getExchangeId(), recApp , msgType, sendingFacility);
	}
	
	
}
