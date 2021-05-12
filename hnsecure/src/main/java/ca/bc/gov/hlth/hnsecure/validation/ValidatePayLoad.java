package ca.bc.gov.hlth.hnsecure.validation;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatePayLoad extends Validate {
	private static final Logger logger = LoggerFactory.getLogger(ValidatePayLoad.class);
	
	private Validator validator;
	public ValidatePayLoad(Validator validator) {
		super(validator);
		this.validator = validator;

	}

	@Override
	public void validateRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void process(Exchange exchange) throws Exception {
		validator.process(exchange);
		logger.info("Validate pay load process compelte.");
	}

}
