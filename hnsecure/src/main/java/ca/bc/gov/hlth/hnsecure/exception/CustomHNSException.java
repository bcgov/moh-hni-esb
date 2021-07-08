package ca.bc.gov.hlth.hnsecure.exception;

import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;

/**
 * Custom exception created for HN Error handling 
 * @author pankaj.kathuria
 *
 */
public class CustomHNSException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private ErrorMessage errorMessage;

	public CustomHNSException(ErrorMessage errorMessage) {
		super(errorMessage.getErrorMessage());
		this.errorMessage = errorMessage;
	}

	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(ErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

}
