package ca.bc.gov.hlth.hnsecure.exception;

import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;

/**
 * Custom exception created for HN Error handling 
 *
 */
public class CustomHNSException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final ErrorMessage errorMessage;

	public CustomHNSException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
		this.errorMessage = errorMessage;
	}

	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

}
