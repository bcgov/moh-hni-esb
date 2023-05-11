package ca.bc.gov.hlth.hnsecure.rapid;

import org.apache.commons.lang3.StringUtils;

public class RPBSPMC0 {
	private RPBSHeader rpbsHeader = new RPBSHeader();
	private RPBSPMC0Data rpbsmc0Data = new RPBSPMC0Data();

	public RPBSPMC0() {
		super();
	}

	public RPBSPMC0(String message) {
		String headerText = StringUtils.substring(message, 0, RPBSHeader.SEGMENT_LENGTH);
		String bodyText = StringUtils.substring(message, RPBSHeader.SEGMENT_LENGTH);

		rpbsHeader = new RPBSHeader(headerText);

		rpbsmc0Data = new RPBSPMC0Data(bodyText);
	} 

	public RPBSPMC0Data getRpbsmc0Data() {
		return rpbsmc0Data;
	}

	public void setRpbsmc0Data(RPBSPMC0Data rpbsmc0Data) {
		this.rpbsmc0Data = rpbsmc0Data;
	}

	public RPBSHeader getRpbsHeader() {
		return rpbsHeader;
	}

	public void setRpbsHeader(RPBSHeader rpbsHeader) {
		this.rpbsHeader = rpbsHeader;
	}

	public String serialize() {
		return rpbsHeader.serialize() + rpbsmc0Data.serialize();
	}

}
