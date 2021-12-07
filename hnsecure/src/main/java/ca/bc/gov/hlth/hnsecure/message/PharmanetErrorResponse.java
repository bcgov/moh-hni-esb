package ca.bc.gov.hlth.hnsecure.message;

import ca.bc.gov.hlth.hnsecure.parsing.Util;

/**
 * This class builds error response for Pharmanet
 * Follows MSH+ZCA+ZCB+ZZZ format
 *
 */
public class PharmanetErrorResponse extends ResponseSegment {
	
	private static final String ZCA_IDENTIFIER = "ZCA";
	private static final String ZCB_IDENTIFIER  = "ZCB";
	private static final String ZZZ_IDENTIFIER  = "ZZZ";
	private static final String RESPONSE_STATUS = "1";
	private static final String TRANSACTION_CODE = "50";


	/**
	 * Builds error response for Pharmanet
	 * @return formatted message in MSH+ZCA+ZCB+ZZZ format
	 */ 
	@Override
	public String constructResponse(HL7Message messageObj, ErrorMessage error) {
		
		return constructMSH(messageObj) + buildZCA() + buildZCB() + buildZZZ(error);
	}
	
	/**
	 * This method builds ZCA segment for Pharmanet Error response
	 * @return ZCA segment to build pharmanet response
	 */
	/*This message is building a constant string:  "ZCA|||50|||\n".
	 *We are expecting a custom message in future, thats why using StringBuilder here.  
	 */
	public String buildZCA() {
		StringBuilder sb = new StringBuilder(ZCA_IDENTIFIER );
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(TRANSACTION_CODE);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.LINE_BREAK);
		return sb.toString();
	}

	/**
	 * This method builds ZCB segment for Pharmanet Error response
	 * @return ZCB segment to build pharmanet response
	 */
	/*REVIEW This message is building a constant string:  "ZCB|||\n".
	 *We are expecting a custom message in future, thats why using StringBuilder here  
	 */

	public String buildZCB() {
		StringBuilder sb = new StringBuilder(ZCB_IDENTIFIER );
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.LINE_BREAK);
		return sb.toString();
	}
	
	/**
	 * This method builds ZZZ segment for Pharmanet Error response
	 * @return ZZZ segment to build pharmanet response
	 */
	public String buildZZZ(ErrorMessage errorMessage) {
		StringBuilder sb = new StringBuilder(ZZZ_IDENTIFIER );
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(RESPONSE_STATUS);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		sb.append(errorMessage.getErrorSequence() + "  " + errorMessage.getErrorMessage());
		sb.append(Util.HL7_DELIMITER);
		sb.append(Util.HL7_DELIMITER);
		return sb.toString();
	}

}
