package ca.bc.gov.hlth.hnsecure.message;

import java.util.HashMap;
import java.util.Map;

public enum ErrorMessage {

	HL7Error_Msg_NoInputHL7	("VLDT004E", "No HL7 Message was supplied as input"),
	HL7Error_Msg_MSHSegmentMissing("VLDT007E", "The MSH Segment from the HL7 Message is missing."),
	HL7Error_Msg_FacilityIDMismatch("VLDT008E", "The Client Facility and HL7 Sending Facility IDs do not match."),
	HL7Error_Msg_MissingReceivingFacility("VLDT009E", "The Receiving Facility ID was not specified in the HL7 Message."),
	HL7Error_Msg_InvalidHL7Format("VLDT014E", "The Supplied HL7 Message was improperly formatted"),
	
	@Deprecated
	HL7Error_Msg_RemoteTimeOut("TXFR024E", "Connection with remote facility timed out"),
	@Deprecated
	HL7Error_Msg_ServerUnavailable("TXFR036E", "Server could not be contacted. HL7Xfer Server running?"),
	@Deprecated
	HL7Error_Msg_ServerTimedOut("TXFR039E", "Connection to HL7Xfer Server timed out with no or partial response."),
	@Deprecated
	HL7Error_Msg_InvalidDomain("VLDT013E", "The Processing Domain is not one of P, T, E, or D."),
	HL7Error_Msg_EncryptionError("TXFR029E","Encryption protocols failed with remote facility."),
	@Deprecated
	HL7Error_Msg_Unknown("TFXR999E","An unknown error has occurred."),
	
	HL7Error_Msg_InvalidMSHSegment("HNPS002E","Invalid MSH segment format"),
	HL7Error_Msg_UnknownReceivingApplication("HNPS005E","Unknown receiving application"),
	
	// Pharmanet error message
	HL7Error_Msg_TransactionFromatError("PNPA004E","Transaction format error detected"),
	
	// Custom error message so custom sequence added
	CustomError_Msg_MissingAuthKey("HNSE001E", "No authorization key passed in request header."),
	CustomError_Msg_InvalidAuthKey("HNSE002E", "Invalid authorization key."),
	CustomError_Msg_InvalidScopes("HNSE003E", "Invalid scopes for user."),
	CustomError_Msg_InvalidRequest("HNSE004E", "Invalid request.");

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

