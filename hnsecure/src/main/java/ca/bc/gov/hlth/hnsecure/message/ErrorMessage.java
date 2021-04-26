package ca.bc.gov.hlth.hnsecure.message;

import java.util.HashMap;
import java.util.Map;


public enum ErrorMessage {
	
	
	HL7Error_Msg_NoInputHL7	("VLDT001E","No HL7 Message was supplied as input"),

	HL7Error_Msg_MSHSegmentMissing("VLDT002E","The MSH Segment from the HL7 Message is missing."),
	HL7Error_Msg_FacilityIDMismatch("VLDT008E","The Client Facility and HL7 Sending Facility IDs do not match."),
	HL7Error_Msg_MissingReceivingFacility("VLDT003E","The Receiving Facility ID was not specified in the HL7 Message."),
	
	HL7Error_Msg_InvalidMSHSegment("HNPS002E","Invalid MSH segment format"),
	HL7Error_Msg_UnknownReceivingApplication("HNPS005E","Unknown receiving application"),
	HL7Error_Msg_EncryptionError("TXFR029E","Encryption protocols failed with remote facility."),
	
	HL7Error_Msg_InvalidDomain("VLDT006E","The Processing Domain is not one of P, T, E, or D."),
	HL7Error_Msg_InvalidHL7Format("VLDT014E","The Supplied HL7 Message was improperly formatted"),
	
	HL7Error_Msg_RemoteTimeOut("VLDT008E","Connection with remote facility timed out"),
	HL7Error_Msg_ServerTimedOut("VLDT009E","Connection to HL7Xfer Server timed out with no or partial response."),
	HL7Error_Msg_ServerUnavailable("VLDT010E","Server could not be contacted. HL7Xfer Server running?"),
	HL7Error_Msg_Unknown("VLDT011E","An unknown error has occurred."),
	
	//Pharmanet error message
	HL7Error_Msg_TransactionFromatError("PNPA004E","Transaction format error detected");

	private final String errorSequence;
	private final String errorMessage;
	
	public static Map<String, ErrorMessage> errorMessageByErrorSequence = new HashMap<>();

	static {
		for (ErrorMessage errorMessage : ErrorMessage.values()) {
			errorMessageByErrorSequence.put(errorMessage.getErrorSequence(), errorMessage);
		}
	}

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
