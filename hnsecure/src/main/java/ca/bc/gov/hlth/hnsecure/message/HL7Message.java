package ca.bc.gov.hlth.hnsecure.message;

public class HL7Message {

	public String segmentIdentifier = "MSH";
	
	private  String encodingCharacter;

	private  String sendingApplication;

	private  String sendingFascility;

	private  String receivingApplication;

	private  String receivingFascility;

	private  String dateTime;

	private  String security;

	private  String messageType;

	private  String messageControlId;

	private  String processingId;

	private  String versionId;

	private  String fieldSeperator = "|";

	public String getSegmentIdentifier() {
		return segmentIdentifier;
	}

	public void setSegmentIdentifier(String segmentIdentifier) {
		this.segmentIdentifier = segmentIdentifier;
	}

	public String getFieldSeperator() {
		return fieldSeperator;
	}

	public void setFieldSeperator(String fieldSeperator) {
		this.fieldSeperator = fieldSeperator;
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

	public String getSendingFascility() {
		return sendingFascility;
	}

	public void setSendingFascility(String sendingFascility) {
		this.sendingFascility = sendingFascility;
	}

	public String getReceivingApplication() {
		return receivingApplication;
	}

	public void setReceivingApplication(String receivingApplication) {
		this.receivingApplication = receivingApplication;
	}

	public String getReceivingFascility() {
		return receivingFascility;
	}

	public void setReceivingFascility(String receivingFascility) {
		this.receivingFascility = receivingFascility;
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

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

}
