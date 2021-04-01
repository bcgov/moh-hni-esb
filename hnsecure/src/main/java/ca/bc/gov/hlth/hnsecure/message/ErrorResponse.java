package ca.bc.gov.hlth.hnsecure.message;

public class ErrorResponse implements ResponseSegment {
	
	private final String segmentIdentifier = "MSA";
	
	private final String fieldSeperator = "|";
	
	private final String ackKnowledgementCode = "AR";
	
	private final String section  = "VLDT";
	
	private final String ack = "ACK";
	
	@Override
	public String constructResponse(HL7Message messageObj, ErrorMessage errorMessage) {
		return constructMSH(messageObj) + constructMSA(messageObj.getMessageControlId(), errorMessage);
	}
	
	public String constructMSA(String messageControlID, ErrorMessage errorMessage) {
		StringBuilder sb = new StringBuilder(segmentIdentifier);
		sb.append(fieldSeperator);
		sb.append(ackKnowledgementCode);
		sb.append(fieldSeperator);
		sb.append(messageControlID);
		sb.append(fieldSeperator);
		sb.append(errorMessage.getErrorSequence()+ "  " + errorMessage.getErrorMessage());
		sb.append(fieldSeperator);

		return sb.toString();
		
	}
	/**
	 * @param v2Segment
	 */
	public void initSegment(String[] v2Segment,HL7Message messageObj ) {
		if (v2Segment.length >= 12) {
			messageObj.setSegmentIdentifier(v2Segment[0]) ;
			messageObj.setEncodingCharacter(v2Segment[1]);
			messageObj.setSendingApplication(v2Segment[2]);
			messageObj.setSendingFacility(v2Segment[3]);
			messageObj.setReceivingApplication(v2Segment[4]); 
			messageObj.setReceivingFacility(v2Segment[5]);
			messageObj.setDateTime(v2Segment[6]);
			messageObj.setSecurity(v2Segment[7]);
			messageObj.setMessageType(v2Segment[8]); 
			messageObj.setMessageControlId( v2Segment[9]); 
			messageObj.setProcessingId(v2Segment[10]); 
			messageObj.setVersionId(v2Segment[11]);
		}
	}
	
	@Override
	public String constructMSH(HL7Message messageObj) {
		StringBuilder sb = new StringBuilder();
		messageObj.setSegmentIdentifier("MSH");
		
		sb.append(messageObj.getSegmentIdentifier());
		sb.append(messageObj.getFieldSeparator());
				
		sb.append(messageObj.getEncodingCharacter());
		sb.append(messageObj.getFieldSeparator());
				
		sb.append(messageObj.getReceivingApplication());
		sb.append(messageObj.getFieldSeparator());
		
		sb.append(messageObj.getReceivingFacility());
		sb.append(messageObj.getFieldSeparator());
		
		sb.append(messageObj.getSendingApplication());
		sb.append(messageObj.getFieldSeparator());
		
		sb.append(messageObj.getSendingFacility());
		sb.append(messageObj.getFieldSeparator());
		
		sb.append(messageObj.getDateTime());
		sb.append(messageObj.getFieldSeparator());
		
		//sb.append(messageObj.getSecurity());
		sb.append(getAck());
		sb.append(messageObj.getFieldSeparator());
		
		sb.append(messageObj.getMessageType());
		sb.append(messageObj.getFieldSeparator());
		
		sb.append(messageObj.getMessageControlId());
		sb.append(messageObj.getFieldSeparator());
		
		sb.append(messageObj.getProcessingId());
		sb.append(messageObj.getFieldSeparator());
		
		sb.append(messageObj.getVersionId());
		
		sb.append("\n");
		
		return sb.toString();
		
	}

	public String getAck() {
		return ack;
	}

}
