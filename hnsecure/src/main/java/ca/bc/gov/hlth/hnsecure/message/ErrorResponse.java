package ca.bc.gov.hlth.hnsecure.message;

import java.util.Optional;

import ca.bc.gov.hlth.hnsecure.parsing.Util;

public class ErrorResponse extends ResponseSegment {
	
	private static final String segmentIdentifier = "MSA";
		
	private static final String ackKnowledgementCode = "AR";

	@Override
	public String constructResponse(HL7Message messageObj, ErrorMessage errorMessage) {
		return constructMSH(messageObj) + constructMSA(messageObj.getMessageControlId(), errorMessage);
	}
	
	public String constructMSA(String messageControlID, ErrorMessage errorMessage) {
		StringBuilder sb = new StringBuilder(segmentIdentifier);
		sb.append(Util.HL7_DELIMITER);
		sb.append(ackKnowledgementCode);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Optional.ofNullable(messageControlID).orElse(""));
		sb.append(Util.HL7_DELIMITER);
		sb.append(errorMessage.getErrorSequence() + "  " + errorMessage.getErrorMessage());
		sb.append(Util.HL7_DELIMITER);

		return sb.toString();
	}

}
