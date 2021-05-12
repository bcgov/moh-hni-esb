package ca.bc.gov.hlth.hnsecure.validation;


import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class uses decorator pattern
 * @author pankaj.kathuria
 *
 */

public class ValidatorImpl implements Validator {
	
	Logger logger = LoggerFactory.getLogger(ValidatorImpl.class);
	
	public ValidatorImpl() {
		init();
	}
	
	public void init() {
		logger.info("Validator initialized");
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		logger.info("Validation processing complete");
	}

	
	
}
