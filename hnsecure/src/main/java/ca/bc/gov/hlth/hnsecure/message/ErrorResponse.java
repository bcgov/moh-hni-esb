package ca.bc.gov.hlth.hnsecure.message;

public class ErrorResponse implements ResponseSegment {
	
	private String segmentIdentifier = "MSA";
	
	private String fieldSeperator = "|";
	
	private String ackKnowledgemetCode = "AR";
	
	private String section  = "VLDT";
	


	@Override
	public String consructResponse(HL7Message messageObj,String messageControlID, ErrorMessage errorMessage) {
		return constructMSH(messageObj) + constructMSA(messageControlID, errorMessage);
	}
	
	public String constructMSA(String messageControlID, ErrorMessage errorMessage) {
		StringBuilder sb = new StringBuilder(segmentIdentifier);
		sb.append(fieldSeperator);
		sb.append(ackKnowledgemetCode);
		sb.append(fieldSeperator);
		sb.append(messageControlID);
		sb.append(fieldSeperator);
		sb.append(section +errorMessage.getErrorSequence()+ "  " + errorMessage.getErrorMessage());
		sb.append(fieldSeperator);
		
		return sb.toString();
		
	}
	/**
	 * @param v2Segment
	 */
	public void initSegment(String[] v2Segment,HL7Message messageObj ) {
		if (v2Segment.length >= 12) {
			messageObj.segmentIdentifier = v2Segment[0];
			messageObj.setEncodingCharacter(v2Segment[1]);
			messageObj.setSendingApplication(v2Segment[2]);
			messageObj.setSendingFascility(v2Segment[3]);
			messageObj.setReceivingApplication(v2Segment[4]); 
			messageObj.setReceivingFascility(v2Segment[5]);
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
		
		sb.append(messageObj.segmentIdentifier);
		sb.append(messageObj.getFieldSeperator());
				
		sb.append(messageObj.getEncodingCharacter());
		sb.append(messageObj.getFieldSeperator());
		
		sb.append(messageObj.getSendingApplication());
		sb.append(messageObj.getFieldSeperator());
		
		sb.append(messageObj.getSendingFascility());
		sb.append(messageObj.getFieldSeperator());
		
		sb.append(messageObj.getReceivingApplication());
		sb.append(messageObj.getFieldSeperator());
		
		sb.append(messageObj.getReceivingFascility());
		sb.append(messageObj.getFieldSeperator());
		
		sb.append(messageObj.getDateTime());
		sb.append(messageObj.getFieldSeperator());
		
		sb.append(messageObj.getSecurity());
		sb.append(messageObj.getFieldSeperator());
		
		sb.append(messageObj.getMessageType());
		sb.append(messageObj.getFieldSeperator());
		
		sb.append(messageObj.getMessageControlId());
		sb.append(messageObj.getFieldSeperator());
		
		sb.append(messageObj.getProcessingId());
		sb.append(messageObj.getFieldSeperator());
		
		sb.append(messageObj.getVersionId());
		
		sb.append("\n");
		
		return sb.toString();
		
	}

}
