package ca.bc.gov.hlth.hnsecure.message;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

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
		if (StringUtils.isNotBlank(errorMessage.getFieldValue())) {
			sb.append(errorMessage.getErrorSequence() + "  " + errorMessage.getErrorMessage() + " "
					+ errorMessage.getFieldValue());
		} else {
			sb.append(errorMessage.getErrorSequence() + "  " + errorMessage.getErrorMessage());
		}
		sb.append(Util.HL7_DELIMITER);

		return sb.toString();
	}

}
