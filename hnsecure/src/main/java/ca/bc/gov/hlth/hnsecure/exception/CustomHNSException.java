package ca.bc.gov.hlth.hnsecure.exception;

/**
 * Custom exception created for HN Error handling 
 * @author pankaj.kathuria
 *
 */
public class CustomHNSException extends Exception {

	/**
	 * Added for warning 
	 */
	private static final long serialVersionUID = 1L;

	public CustomHNSException(String msg) {
		super(msg);
	}
	
}
