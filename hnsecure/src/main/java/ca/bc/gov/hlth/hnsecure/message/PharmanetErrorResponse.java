package ca.bc.gov.hlth.hnsecure.message;

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

	@Override
	public String constructResponse(HL7Message messageObj, ErrorMessage error) {
		
		return constructMSH(messageObj)+buildZCA()+buildZCB()+buildZZZ(error);
	}
	
	private String buildZCA() {
		StringBuilder sb = new StringBuilder(ZCA_IDENTIFIER );
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(TRANSACTION_CODE);
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append("\n");
		return sb.toString();
	}


	private String buildZCB() {
		StringBuilder sb = new StringBuilder(ZCB_IDENTIFIER );
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append(getFieldseperator());
		sb.append("\n");
		return sb.toString();
	}
	;
	private String buildZZZ(ErrorMessage errorMessage) {
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
