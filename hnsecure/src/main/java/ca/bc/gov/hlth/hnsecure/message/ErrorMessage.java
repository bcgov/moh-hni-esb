package ca.bc.gov.hlth.hnsecure.message;

import java.util.HashMap;
import java.util.Map;


public enum ErrorMessage {
	
	
	HL7Error_Msg_NoInputHL7	(1,"No HL7 Message was supplied as input"),

	HL7Error_Msg_MSHSegmentMissing(2,"The MSH Segment from the HL7 Message is missing."),
	HL7Error_Msg_FacilityIDMismatch(3,"The Client Facility and HL7 Sending Facility IDs do not match."),
	HL7Error_Msg_MissingReceivingFacility(4,"The Receiving Facility ID was not specified in the HL7 Message."),
	
	HL7Error_Msg_InvalidDomain(5,"The Processing Domain is not one of P, T, E, or D."),
	HL7Error_Msg_InvalidHL7Format(6,"The Supplied HL7 Message was improperly formatted"),
	
	HL7Error_Msg_RemoteTimeOut(7,"Connection with remote facility timed out"),
	HL7Error_Msg_ServerTimedOut(8,"Connection to HL7Xfer Server timed out with no or partial response."),
	HL7Error_Msg_ServerUnavailable(9,"Server could not be contacted. HL7Xfer Server running?"),
	HL7Error_Msg_Unknown(10,"An unknown error has occurred.");

	private int errorSequence;
	private String errorMessage;
	
	public static Map<Integer, ErrorMessage> errorMessageByErrorSequence = new HashMap<Integer, ErrorMessage>();

	static {
		for (ErrorMessage errorMessage : ErrorMessage.values()) {
			errorMessageByErrorSequence.put(errorMessage.getErrorSequence(), errorMessage);
		}
	}

	private ErrorMessage(int errorSequence, String errorMessage) {
		this.errorSequence = errorSequence;
		this.errorMessage = errorMessage;
	}

	public int getErrorSequence() {
		return errorSequence;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public static ErrorMessage getErrorMessageBySequence(Integer errorSequence) {
		return errorMessageByErrorSequence.get(errorSequence);
	}

	public static String retrieveEnumByValue(Integer sequence) {
		if (sequence == null)
			return "";
		ErrorMessage businessError = ErrorMessage.getErrorMessageBySequence(sequence);
		return businessError.getErrorMessage();
	}

}
