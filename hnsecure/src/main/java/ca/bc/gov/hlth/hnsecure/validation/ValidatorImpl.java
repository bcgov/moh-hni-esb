package ca.bc.gov.hlth.hnsecure.validation;


import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the implementation class of decorator pattern interface
 * It can have an non-argument constructor, so this will be used as innermost argument in calling the validators
 * for example: Validator validator = new PayLoadValidator(new TokenValidator(new ValidatorImpl()));
 * In this order of invocation, initialization is happening in the order of
 * 1. PayLoadValidator
 * 2. TokenValidator
 * 3. ValidatorImpl
 * @author pankaj.kathuria
 *
 */

public class ValidatorImpl implements Validator {
	
	Logger logger = LoggerFactory.getLogger(ValidatorImpl.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("Validation processing started");
	}

	/**
	 * Currently this implementation does nothing to validate as we are using this implementation as innermost
	 * If a common validation is required for all the validators, we can move that business logic to this method.
	 * 
	 */
	@Override
	public boolean validate(Exchange exchange) {
		logger.info("Inner validation processing started");
		logger.info("Inner validation processing complete");
		return true;
	}

	
	
}
