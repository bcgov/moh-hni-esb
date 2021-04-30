package ca.bc.gov.hlth.hnsecure.message;

import ca.bc.gov.hlth.hnsecure.parsing.Util;

/**
 * This class builds error response for Pharmanet
 * Follows MSH+ZCA+ZCB+ZZZ format
 * @author anumeha.srivastava
 *
 */
public class PharmanetErrorResponse extends ResponseSegment {
	
	private static final String ZCA_IDENTIFIER = "ZCA";
	private static final String ZCB_IDENTIFIER  = "ZCB";
	private static final String ZZZ_IDENTIFIER  = "ZZZ";
	private static final String RESPONSE_STATUS = "1";
	private static final String TRANSACTION_CODE = "50";
	
	

	/*
	 * MSH|^~\&|HNCLIENT|BC01000161|PLEXIAPNP|BC01000161|2021/04/26
	 * 17:07:45|ANUMEHA.SRIVASTAVA|ZPN|18|D|2.1|| ZCA||03|00|PE|02|
	 * ZCB|BCXX000024|201126|18 ZZZ|TID|1|18|91|XYACA|0|TXFR029E Encryption
	 * protocols failed with remote facility.||
	 */

	@Override
	public String constructResponse(HL7Message messageObj, ErrorMessage error) {
		
		return constructMSH(messageObj)+buildZCA()+buildZCB()+buildZZZ(error);
	}
	
	/**
	 * This method builds ZCA segment for Pharmanet Error response
	 * @return 
	 */
	public String buildZCA() {
		StringBuilder sb = new StringBuilder(ZCA_IDENTIFIER );
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(TRANSACTION_CODE);
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(Util.LINE_BREAK);
		return sb.toString();
	}

	/**
	 * This method builds ZCB segment for Pharmanet Error response
	 * @return 
	 */
	public String buildZCB() {
		StringBuilder sb = new StringBuilder(ZCB_IDENTIFIER );
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(Util.LINE_BREAK);
		return sb.toString();
	}
	
	/**
	 * This method builds ZZZ segment for Pharmanet Error response
	 * @return 
	 */
	public String buildZZZ(ErrorMessage errorMessage) {
		StringBuilder sb = new StringBuilder(ZZZ_IDENTIFIER );
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(RESPONSE_STATUS);
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(errorMessage.getErrorSequence()+ "  " + errorMessage.getErrorMessage());
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		return sb.toString();
	}

}
