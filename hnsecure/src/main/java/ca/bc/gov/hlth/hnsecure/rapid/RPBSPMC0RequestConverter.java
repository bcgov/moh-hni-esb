package ca.bc.gov.hlth.hnsecure.rapid;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_PROCESSING_DOMAIN;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RAPID_ORG;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RAPID_PHN;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_SENDING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_USER_INFO;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;

public class RPBSPMC0RequestConverter {
	
	private static final String TRAN_CODE = "RPBSPMC0";

	public RPBSPMC0RequestConverter() {
		super();
	}

	@Handler
	public String convertRequest(Exchange exchange, String message) {
		String v2Message = message;
		String zhdSegment = V2MessageUtil.getDataSegment(v2Message, Util.ZHD_SEGMENT);
		String pidSegment = V2MessageUtil.getDataSegment(v2Message, Util.PID_SEGMENT);
		String receivingFacility = V2MessageUtil.getReceivingFacility(v2Message);		
		String receivingApp = V2MessageUtil.getReceivingApp(v2Message);
		String sendingApp =  V2MessageUtil.getSendingApplication(v2Message);
		String userInfo = V2MessageUtil.getUserInfo(v2Message);
		String domain = V2MessageUtil.getProcessingDomain(v2Message);
		String org = V2MessageUtil.getOrg(zhdSegment);		
		String pid = V2MessageUtil.getPHN(pidSegment);
		
		Map<String, Object> exchangeProperties = exchange.getProperties();
		exchangeProperties.put(PROPERTY_USER_INFO, userInfo);
		exchangeProperties.put(PROPERTY_PROCESSING_DOMAIN, domain);			
		exchangeProperties.put(PROPERTY_RAPID_PHN, pid);
		exchangeProperties.put(PROPERTY_RAPID_ORG, org);		
		exchangeProperties.put(PROPERTY_RECEIVING_FACILITY, receivingFacility);
		exchangeProperties.put(PROPERTY_SENDING_APP, sendingApp);
		exchangeProperties.put(PROPERTY_RECEIVING_APP, receivingApp);

		RPBSHeader rpbsHeader = new RPBSHeader();
		rpbsHeader.setOrganization(org);
		rpbsHeader.setTranCode(TRAN_CODE);

		RPBSPMC0Data rpbsmc0Data = new RPBSPMC0Data();
		rpbsmc0Data.setPhn(pid);

		RPBSPMC0 rpbspmc0 = new RPBSPMC0();
		rpbspmc0.setRpbsHeader(rpbsHeader);
		rpbspmc0.setRpbsmc0Data(rpbsmc0Data);

		return rpbspmc0.serialize();
	}

}