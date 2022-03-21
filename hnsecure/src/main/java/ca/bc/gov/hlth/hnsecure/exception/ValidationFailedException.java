package ca.bc.gov.hlth.hnsecure.exception;

import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;

public class ValidationFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final ErrorMessage errorMessage;
	
	public ValidationFailedException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
		this.errorMessage = errorMessage;
	}

	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

}
