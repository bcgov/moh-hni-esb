package ca.bc.gov.hlth.hnsecure.message;

import java.util.Optional;

import ca.bc.gov.hlth.hnsecure.parsing.Util;

public class ErrorResponse extends ResponseSegment {

	private static final String SEGMENT_IDENTIFIER = "MSA";

	private static final String ACK_CODE = "AR";

	@Override
	public String constructResponse(HL7Message messageObj, ErrorMessage errorMessage) {
		String formattedResponseText = errorMessage.getErrorSequence() + "  " + errorMessage.formatErrorMessage(messageObj);	
		return constructMSH(messageObj) + constructMSA(messageObj.getMessageControlId(), formattedResponseText);
	}

	public String constructMSA(String messageControlID, String errorResponse) {
		StringBuilder sb = new StringBuilder(SEGMENT_IDENTIFIER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(ACK_CODE);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Optional.ofNullable(messageControlID).orElse(""));
		sb.append(Util.HL7_DELIMITER);
		sb.append(errorResponse);		
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);

		return sb.toString();
	}

	

}
