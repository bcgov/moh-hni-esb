package ca.bc.gov.hlth.hnsecure.validation;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * abstract class to validate 
 * @author pankaj.kathuria
 *
 */
public abstract class Validate implements Validator {
	private static final Logger logger = LoggerFactory.getLogger(Validate.class);

	private Validator validator;
	public Validate(Validator validator) {
		// TODO Auto-generated constructor stub
		this.validator = validator;
	}

	public abstract void validateRequest();
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		validator.process(exchange);
		logger.info("token Validation complete");
	}

	
	
}
