package ca.bc.gov.hlth.hnsecure.rapid;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.parsing.Util;

public class RPBSPMC0RequestConverter {
	public static final String PROPERTY_RAPID_PHN = "phn";
	public static final String PROPERTY_RAPID_ORG = "organization";

	private static final Logger logger = LoggerFactory.getLogger(RPBSPMC0RequestConverter.class);

	private static final String TRAN_CODE = "RPBSPMC0";

	public RPBSPMC0RequestConverter() {
		super();
	}

	@Handler
	public String convertRequest(Exchange exchange, String v2Message) {
		String org = exchange.getProperty(Util.PROPERTY_RAPID_ORG).toString();
		String pid = exchange.getProperty(Util.PROPERTY_RAPID_PHN).toString();

		RPBSHeader rpbsHeader = new RPBSHeader();
		rpbsHeader.setOrganization(org);
		rpbsHeader.setTranCode(TRAN_CODE);

		RPBSPMC0Data rpbsmc0Data = new RPBSPMC0Data();
		rpbsmc0Data.setPhn(pid);

		RPBSPMC0 rpbspmc0 = new RPBSPMC0();
		rpbspmc0.setRpbsHeader(rpbsHeader);
		rpbspmc0.setRpbsmc0Data(rpbsmc0Data);
		logger.debug(rpbspmc0.toString());

		return rpbspmc0.serialize();
	}

}