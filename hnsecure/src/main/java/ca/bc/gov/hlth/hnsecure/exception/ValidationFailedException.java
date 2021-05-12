package ca.bc.gov.hlth.hnsecure.exception;

public class ValidationFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public ValidationFailedException(String message) {
		super(message);
	};

}
