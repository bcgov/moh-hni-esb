package ca.bc.gov.hlth.hnsecure.validation;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class validate the abstract token in the request
 * @author pankaj.kathuria
 *
 */
public class ValidateToken extends Validate {
	private static final Logger logger = LoggerFactory.getLogger(ValidateToken.class);
	private Validator validator;
	
	public ValidateToken(Validator validator) {
		super(validator);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void validateRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		validator.process(exchange);
		logger.info("token Validation complete");
	}

}
