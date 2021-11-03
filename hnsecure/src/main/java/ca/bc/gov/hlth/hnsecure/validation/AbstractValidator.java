package ca.bc.gov.hlth.hnsecure.validation;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;


/**
 * abstract class to validate 
 *
 */
public abstract class AbstractValidator implements Validator {
	private static final Logger logger = LoggerFactory.getLogger(AbstractValidator.class);

	public AbstractValidator() {
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("{} - AbstractValidator processing started",LoggingUtil.getMethodName());
		validate(exchange);
		logger.info("{} - AbstractValidator processing completed",LoggingUtil.getMethodName());
	}

	
	
}
