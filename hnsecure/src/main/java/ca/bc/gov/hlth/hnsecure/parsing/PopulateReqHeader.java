package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_MESSAGE_TYPE;
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
 * Used to populate common request headers
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
	public void populateReqHeader(Exchange exchange, @Headers Map<String, Object> hm, String v2Message) {
		final String methodName = LoggingUtil.getMethodName();
		String transactionUUID = exchange.getExchangeId();

		// The following values are properties used to control workflow
		// and do not actually belong in the message header (which is converted to http
		// request headers)
		Map<String, Object> exchangeProperties = exchange.getProperties();
		
		String msgType = V2MessageUtil.getMsgType(v2Message);			
		String accessToken = (String) exchange.getIn().getHeader(AUTHORIZATION);
		String sendingFacility = Util.getSendingFacility(accessToken);
		exchangeProperties.put(PROPERTY_SENDING_FACILITY, sendingFacility);
		exchangeProperties.put(PROPERTY_MESSAGE_TYPE, msgType);
				
		exchangeProperties.put(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK_200);

		if (StringUtils.equals(Util.MESSAGE_TYPE_PNP, msgType)) {
			String zcbSegment = V2MessageUtil.getDataSegment(v2Message, Util.ZCB_SEGMENT);
			String pharmacyID = V2MessageUtil.getPharmacyId(zcbSegment);
			String traceID = V2MessageUtil.getTraceNumber(zcbSegment);
			logger.info("{} - Transaction Id : {}, Pharmacy Id : {}, TraceId : {}", methodName, transactionUUID,
					pharmacyID, traceID);
		}

		logger.info(
				"{} - Transaction Id : {}, Transaction type : {}, Sending Facility : {} ",
				methodName, transactionUUID, msgType, sendingFacility);
	}

}
