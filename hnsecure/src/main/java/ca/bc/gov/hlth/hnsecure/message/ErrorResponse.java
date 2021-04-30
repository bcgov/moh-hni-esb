package ca.bc.gov.hlth.hnsecure.message;

import java.util.Optional;

public class ErrorResponse extends ResponseSegment {
	
	private static final String segmentIdentifier = "MSA";
		
	private static final String ackKnowledgementCode = "AR";
	
	private static final String ack = "ACK";
	

	
	
	@Override
	public String constructResponse(HL7Message messageObj, ErrorMessage errorMessage) {
		return constructMSH(messageObj) + constructMSA(messageObj.getMessageControlId(), errorMessage);
	}
	
	public String constructMSA(String messageControlID, ErrorMessage errorMessage) {
		StringBuilder sb = new StringBuilder(segmentIdentifier);
		sb.append(getFieldseperator());
		sb.append(ackKnowledgementCode);
		sb.append(getFieldseperator());
		sb.append(Optional.ofNullable(messageControlID).orElse(""));
		sb.append(getFieldseperator());
		sb.append(errorMessage.getErrorSequence()+ "  " + errorMessage.getErrorMessage());
		sb.append(getFieldseperator());

		return sb.toString();
		
	}


	public String getAck() {
		return ack;
	}
}
