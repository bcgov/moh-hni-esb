package ca.bc.gov.hlth.hnsecure.exception;

import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;

public class ValidationFailedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private ErrorMessage errorMessage;
	
	public ValidationFailedException(ErrorMessage errorMessage) {
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
