package ca.bc.gov.hlth.hnsecure.message;

public enum ErrorMessage {

	HL7_ERROR_NO_INPUT	("VLDT004E", "No HL7 Message was supplied as input"),
	HL7_ERROR_MSH_SEGMENT_MISSING("VLDT007E", "The MSH Segment from the HL7 Message is missing."),
	HL7_ERROR_FACILITY_ID_MISMATCH("VLDT008E", "The Client Facility and HL7 Sending Facility IDs do not match: %s"),
	HL7_ERROR_MISSING_RECEIVING_FACILITY("VLDT009E", "The Receiving Facility ID was not specified in the HL7 Message."),
	HL7_ERROR_INVALID_FORMAT("VLDT014E", "The Supplied HL7 Message was improperly formatted"),
	HL7_ERROR_UNKNOWN("VLDT999E", "An unknown error has occurred."),
	
	HL7_ERROR_ENCRYPTION_ERROR("TXFR029E", "Encryption protocols failed with remote facility."),
	
	HL7_ERROR_INVALID_MSH_SEGMENT("HNPS002E", "Invalid MSH segment format"),
	HL7_ERROR_UNKNOWN_RECEIVING_APPLICATION("HNPS005E", "Unknown receiving application: %s"),
	
	// Pharmanet error message
	HL7_ERROR_TRANSACTION_FORMAT_ERROR("PNPA004E", "Transaction format error detected"),
	
	// MQ series error message
	HL7_ERROR_MQ_TIMEOUT("MQSE001E", "No response before timeout"),
	HL7_ERROR_MQ_FAILED_TO_CONNECT_QM("MQSE002E", "Unable to connect to queue manager"),
	HL7_ERROR_MQ_SERIES_FAILURE("MQSE003E", "MQSeries failure"),
	
	// Custom error message so custom sequence added
	CUSTOM_ERROR_MISSING_AUTH_KEY("HNSE001E", "No authorization key passed in request header."),
	CUSTOM_ERROR_INVALID_AUTH_KEY("HNSE002E", "Invalid token passed in request header."),
	CUSTOM_ERROR_DOWN_STREAM_CONNECTION_FAILED("HNSE003E", "Downstream connection failed."),
	CUSTOM_ERROR_INVALID_REQUEST("HNSE004E", "Invalid request."),
	CUSTOM_ERROR_MQ_NOT_ENABLED("HNSE005E", "Messages Queues are not enabled"),
	CUSTOM_ERROR_INVALID_RESPONSE_MAPPING("HNSE006E", "Invalid response mapping to v2 message.");

	private final String errorSequence;
	private final String message;

	ErrorMessage(String errorSequence, String errorMessage) {
		this.errorSequence = errorSequence;
		this.message = errorMessage;
	}

	public String getErrorSequence() {
		return errorSequence;
	}

	public String getMessage() {
		return message;
	}
	
	/**
	 * Formats the error message for v2 response
	 * @param hl7Message
	 * @return
	 */
	public String formatErrorMessage(HL7Message hl7Message) {
		switch(this) {
		case HL7_ERROR_UNKNOWN_RECEIVING_APPLICATION:
			return String.format(message, hl7Message.getReceivingApplication());
		case HL7_ERROR_FACILITY_ID_MISMATCH:
			return String.format(message, hl7Message.getClientFacilityId());
		default:
			return message;
		}
	}
	
	
	@Override
	public String toString() {
		return errorSequence + " " + message;
	}


}

