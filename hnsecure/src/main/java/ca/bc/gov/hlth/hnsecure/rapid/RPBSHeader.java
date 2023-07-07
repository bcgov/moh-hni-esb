package ca.bc.gov.hlth.hnsecure.rapid;

import org.apache.commons.lang3.StringUtils;

public class RPBSHeader {
	public static final int SEGMENT_LENGTH = 144;
	public static final String IDENTIFER_ERRORMSG = "ERRORMSG";
	public static final String IDENTIFER_RESPONSE = "RESPONSE";
	
	/** 0 Not required */
	private String systemCode = "";
	/** 1	TranCode	String	Yes	0...8	1..1 */
	private String tranCode = "";
	/** 2	String	Yes	0...8	1..1 */	
	private String organization = "";
	/** 3	String	Yes	0...32	1..1 */
	private String userID = "";
	/** 4	String	Yes	0...8	1..1 */
	private String identifier = "";
	/** 5	String	No	0...8	.. */
	private String statusCode = "";
	/** 6	String	No	0...72	.. */
	private String statusText = "";//
	
	public RPBSHeader() {
		super();
	}
	
	public RPBSHeader(String message) {
		super();
		this.systemCode = StringUtils.substring(message, 0, 8);
		this.tranCode = StringUtils.substring(message, 8, 16);
		this.organization = StringUtils.substring(message, 16, 24);
		this.userID = StringUtils.substring(message, 24, 56);
		this.identifier = StringUtils.substring(message, 56, 64);
		this.statusCode = StringUtils.substring(message, 64, 72);
		this.statusText = StringUtils.substring(message, 72, SEGMENT_LENGTH);
	}
	
	public String serialize() {
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.rightPad(systemCode, 8));
		sb.append(StringUtils.rightPad(tranCode, 8));
		sb.append(StringUtils.rightPad(StringUtils.trimToEmpty(organization), 8));
		sb.append(StringUtils.rightPad(userID, 32));
		sb.append(StringUtils.rightPad(identifier, 8));
		sb.append(StringUtils.rightPad(statusCode, 8));
		sb.append(StringUtils.rightPad(statusText, 72));
		
		return sb.toString();
	}

	public String getTranCode() {
		return tranCode;
	}

	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getSystemCode() {
		return systemCode;
	}

}

