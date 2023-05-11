package ca.bc.gov.hlth.hnsecure.message;

public class HL7Message {

	private String segmentIdentifier;
	
	private String encodingCharacter;

	private String sendingApplication;

	private String sendingFacility;
	
	//This will hold the actual client facility id for error response
	private String clientFacilityId;

	private String receivingApplication;

	private String receivingFacility;

	private String dateTime;

	private String security;

	private String messageType;

	private String messageControlId;

	private String processingId;
	
	private String user;

	private String versionId;

	private static final String FIELD_SEPERATOR = "|";

	public String getSegmentIdentifier() {
		return segmentIdentifier;
	}

	public void setSegmentIdentifier(String segmentIdentifier) {
		this.segmentIdentifier = segmentIdentifier;
	}

	public String getFieldSeparator() {
		return FIELD_SEPERATOR;
	}

	public String getEncodingCharacter() {
		return encodingCharacter;
	}

	public void setEncodingCharacter(String encodingCharacter) {
		this.encodingCharacter = encodingCharacter;
	}

	public String getSendingApplication() {
		return sendingApplication;
	}

	public void setSendingApplication(String sendingApplication) {
		this.sendingApplication = sendingApplication;
	}

	public String getSendingFacility() {
		return sendingFacility;
	}

	public void setSendingFacility(String sendingFacility) {
		this.sendingFacility = sendingFacility;
	}

	public String getClientFacilityId() {
		return clientFacilityId;
	}

	public void setClientFacilityId(String sendingFacilityConf) {
		this.clientFacilityId = sendingFacilityConf;
	}

	public String getReceivingApplication() {
		return receivingApplication;
	}

	public void setReceivingApplication(String receivingApplication) {
		this.receivingApplication = receivingApplication;
	}

	public String getReceivingFacility() {
		return receivingFacility;
	}

	public void setReceivingFacility(String receivingFacility) {
		this.receivingFacility = receivingFacility;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageControlId() {
		return messageControlId;
	}

	public void setMessageControlId(String messageControlId) {
		this.messageControlId = messageControlId;
	}

	public String getProcessingId() {
		return processingId;
	}

	public void setProcessingId(String processingId) {
		this.processingId = processingId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

}
