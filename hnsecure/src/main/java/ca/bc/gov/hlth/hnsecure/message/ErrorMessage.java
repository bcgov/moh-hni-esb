package ca.bc.gov.hlth.hnsecure.message;

public enum ErrorMessage {

	HL7Error_Msg_NoInputHL7	("VLDT004E", "No HL7 Message was supplied as input"),
	HL7Error_Msg_MSHSegmentMissing("VLDT007E", "The MSH Segment from the HL7 Message is missing."),
	HL7Error_Msg_FacilityIDMismatch("VLDT008E", "The Client Facility and HL7 Sending Facility IDs do not match."),
	HL7Error_Msg_MissingReceivingFacility("VLDT009E", "The Receiving Facility ID was not specified in the HL7 Message."),
	HL7Error_Msg_InvalidHL7Format("VLDT014E", "The Supplied HL7 Message was improperly formatted"),
	HL7Error_Msg_Unknown("VLDT999E", "An unknown error has occurred."),
	
	HL7Error_Msg_EncryptionError("TXFR029E", "Encryption protocols failed with remote facility."),
	
	HL7Error_Msg_InvalidMSHSegment("HNPS002E", "Invalid MSH segment format"),
	HL7Error_Msg_UnknownReceivingApplication("HNPS005E", "Unknown receiving application:"),
	
	// Pharmanet error message
	HL7Error_Msg_TransactionFormatError("PNPA004E", "Transaction format error detected"),
	
	// MQ series error message
	HL7Error_Msg_MQ_NoResponseBeforeTimeout("MQSE001E", "No response before timeout"),
	HL7Error_Msg_MQ_FailedToConnectQM("MQSE002E", "Unable to connect to queue manager"),
	HL7Error_Msg_MQ_MQSeriesFailure("MQSE003E", "MQSeries failure"),
	
	// Custom error message so custom sequence added
	CustomError_Msg_MissingAuthKey("HNSE001E", "No authorization key passed in request header."),
	CustomError_Msg_InvalidAuthKey("HNSE002E", "Invalid token passed in request header."),
	CustomError_Msg_DownstreamConnectionFailed("HNSE003E", "Downstream connection failed."),
	CustomError_Msg_InvalidRequest("HNSE004E", "Invalid request."),
	CustomError_Msg_MQNotEnabled("HNSE005E", "Messages Queues are not enabled");

	private final String errorSequence;
	private final String errorMessage;
	private String fieldValue;

	ErrorMessage(String errorSequence, String errorMessage) {
		this.errorSequence = errorSequence;
		this.errorMessage = errorMessage;
	}

	public String getErrorSequence() {
		return errorSequence;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	
	@Override
	public String toString() {
		return errorSequence + " " + errorMessage;
	}


}

