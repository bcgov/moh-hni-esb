package ca.bc.gov.hlth.hnsecure.validation;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.exception.ValidationFailedException;

/**
 * This interface uses decorator pattern for adding validation in HNSecure
 * Abstract implementation of Validator should extend this interface
 * Reference: AbstractValidator.java
 *
 */
public interface Validator extends Processor {
	
	/**
	 * Returning boolean in case we have to catch exception and throw custom exception in process method.
	 * For future usage we can also return ValidationStatus object having attributes like status, failure messages in case of failures
	 * @throws Exception 
	 */
	public boolean validate(Exchange exchange) throws CustomHNSException, ValidationFailedException;
	
}
