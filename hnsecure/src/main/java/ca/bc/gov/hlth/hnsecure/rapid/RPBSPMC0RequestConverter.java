package ca.bc.gov.hlth.hnsecure.rapid;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.message.V2MessageUtil;

public class RPBSPMC0RequestConverter {

	private static final Logger logger = LoggerFactory.getLogger(RPBSPMC0RequestConverter.class);
	protected static final String PID_NAMESPACE_ID = "BC";

	protected static final String PID_ID_TYPE_CODE = "PH";

	protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddkkmmss");

	protected String messageDateTime = LocalDateTime.now().format(dateTimeFormatter);

	protected HL7Message mshDefaults;

	protected static DateTimeFormatter dateOnlyFormatter = DateTimeFormatter
			.ofPattern(V2MessageUtil.DATE_FORMAT_DATE_ONLY);

	private static final String TRAN_CODE = "RPBSPMC0";

	public RPBSPMC0RequestConverter() {
		super();
	}

	@Handler
	public String convertRequest(String v2Message) {
		RPBSHeader rpbsHeader = new RPBSHeader();
		rpbsHeader.setOrganization("00000010");
		rpbsHeader.setTranCode(TRAN_CODE);

		RPBSPMC0Data rpbsmc0Data = new RPBSPMC0Data();
		rpbsmc0Data.setPhn("9879869673");

		RPBSPMC0 rpbspmc0 = new RPBSPMC0();
		rpbspmc0.setRpbsHeader(rpbsHeader);
		rpbspmc0.setRpbsmc0Data(rpbsmc0Data);
		logger.info(rpbspmc0.toString());
		System.out.println(rpbspmc0.toString());

		return rpbspmc0.serialize();
	}

}