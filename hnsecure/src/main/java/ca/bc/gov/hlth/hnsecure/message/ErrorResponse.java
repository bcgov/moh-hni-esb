package ca.bc.gov.hlth.hnsecure.message;

import java.util.Optional;

import ca.bc.gov.hlth.hnsecure.parsing.Util;

public class ErrorResponse extends ResponseSegment {

	private static final String segmentIdentifier = "MSA";

	private static final String ackKnowledgementCode = "AR";

	@Override
	public String constructResponse(HL7Message messageObj, ErrorMessage errorMessage) {
		String formattedResponseText = generateErrorText(messageObj, errorMessage);	
		return constructMSH(messageObj) + constructMSA(messageObj.getMessageControlId(), formattedResponseText);
	}

	public String constructMSA(String messageControlID, String errorResponse) {
		StringBuilder sb = new StringBuilder(segmentIdentifier);
		sb.append(Util.HL7_DELIMITER);
		sb.append(ackKnowledgementCode);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Optional.ofNullable(messageControlID).orElse(""));
		sb.append(Util.HL7_DELIMITER);
		sb.append(errorResponse);		
		sb.append(Util.HL7_DELIMITER);

		return sb.toString();
	}
	
	private String generateErrorText(HL7Message messageObj, ErrorMessage errorMessage) {
		String errorResponse = errorMessage.getErrorSequence() + " " + errorMessage.getErrorMessage();
	
		switch(errorMessage) {
			case HL7Error_Msg_UnknownReceivingApplication:
				errorResponse = errorMessage.getErrorSequence() + " " + String.format(errorMessage.getErrorMessage(), messageObj.getReceivingApplication());
				break;
			case HL7Error_Msg_FacilityIDMismatch:
				errorResponse =  errorMessage.getErrorSequence() + " " + String.format(errorMessage.getErrorMessage(), messageObj.getSendingFacility());
				break;
			default:
				break;			
		}
		
		return errorResponse;
	}
	
	

}
