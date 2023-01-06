package ca.bc.gov.hlth.hnsecure.message;

import ca.bc.gov.hlth.hnsecure.parsing.Util;

/**
 * This class builds error response for HNETDTTN request
 * Follows MSH+MSA+NCK
 *
 */
public class DateTimeResponse extends ResponseSegment{
	
	private static final String NCK_IDENTIFIER = "NCK";
	
	private static final String SEGMENT_IDENTIFIER = "MSA";

	private static final String ACK_CODE = "AA";
	
	public String constructResponse(HL7Message messageObj) {
		
		return constructMSH(messageObj) + constructMSA() + constructNCK();
	}

	private String constructMSA() {
		StringBuilder sb = new StringBuilder(SEGMENT_IDENTIFIER);
		sb.append(Util.HL7_DELIMITER);		
		sb.append(ACK_CODE);	
		sb.append(Util.HL7_DELIMITER);			
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.LINE_BREAK);

		return sb.toString();
	}
	
	private String constructNCK() {
		StringBuilder sb = new StringBuilder(NCK_IDENTIFIER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.getGenericDateTime());
	
		return sb.toString();
	}

}
