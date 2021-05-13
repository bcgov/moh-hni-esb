package ca.bc.gov.hlth.hnsecure.validation;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * abstract class to validate 
 * @author pankaj.kathuria
 *
 */
public abstract class AbstractValidator implements Validator {
	private static final Logger logger = LoggerFactory.getLogger(AbstractValidator.class);

	public AbstractValidator() {
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("AbstractValidator processing started");
		validate(exchange);
		logger.info("AbstractValidator processing completed");
	}

	
	
}
