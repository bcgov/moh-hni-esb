/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_SENDING_FACILITY;

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
		
		// The following values are properties used to control workflow
		// and do not actually belong in the message header (which is converted to http request headers)
		Map<String, Object> exchangeProperties = exchange.getProperties();
		String recApp = V2MessageUtil.getReceivingApp(v2Message);
		String msgType = V2MessageUtil.getMsgType(v2Message);
		String accessToken = (String) exchange.getIn().getHeader(AUTHORIZATION);
		String sendingFacility = Util.getSendingFacility(accessToken);
		exchangeProperties.put(PROPERTY_RECEIVING_APP, recApp);
		exchangeProperties.put(PROPERTY_MESSAGE_TYPE, msgType);
		exchangeProperties.put(PROPERTY_SENDING_FACILITY, sendingFacility);
		exchangeProperties.put(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK_200);
		
		if (StringUtils.equals(Util.MESSAGE_TYPE_PNP, msgType)) {
			String zcbSegment = V2MessageUtil.getZCBSegment(v2Message,Util.ZCB_SEGMENT);
			String pharmacyId = V2MessageUtil.getPharmacyId(zcbSegment);
			String traceId = V2MessageUtil.getTraceNumber(zcbSegment);
			
			//log PharmacyId and TraceId as per Pharmanet logging standards.		
			logger.info("{} -  PharmacyId: {}, TraceId: {} ",
					methodName, pharmacyId, traceId);
		}

		logger.info("{} - Transaction Id : {}, Receiving application : {}, Transaction type : {}, Sending Facility : {} ",
				methodName, exchange.getExchangeId(), recApp , msgType, sendingFacility);
	}
	
	
}
