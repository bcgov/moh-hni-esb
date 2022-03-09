package ca.bc.gov.hlth.hnsecure.validation;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.exception.ValidationFailedException;


/**
 * abstract class to validate 
 *
 */
public abstract class AbstractValidator implements Validator {
	private static final Logger logger = LoggerFactory.getLogger(AbstractValidator.class);

	protected AbstractValidator() {
	}

	@Override
	public void process(Exchange exchange) throws CustomHNSException, ValidationFailedException {
		logger.info("{} - AbstractValidator processing started",LoggingUtil.getMethodName());
		validate(exchange);
		logger.info("{} - AbstractValidator processing completed",LoggingUtil.getMethodName());
	}

	
	
}
