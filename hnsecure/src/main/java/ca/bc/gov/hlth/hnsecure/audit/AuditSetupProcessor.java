package ca.bc.gov.hlth.hnsecure.audit;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.parsing.Util;

/**
 * Processor for setting up meta data required for logging audits 
 *
 */
public class AuditSetupProcessor implements Processor {

    private static Logger logger = LoggerFactory.getLogger(AuditSetupProcessor.class);

    private TransactionEventType eventType;
    
	public AuditSetupProcessor(TransactionEventType eventType) {
		super();
		this.eventType = eventType;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		
    	String methodName = LoggingUtil.getMethodName();
		logger.info("{} - Begin {}", methodName, eventType);
        
        exchange.setProperty(Util.PROPERTY_TRANSACTION_EVENT_TIME, new Date());
        exchange.setProperty(Util.PROPERTY_TRANSACTION_EVENT_TYPE, eventType);
        
        logger.debug("{} - End", methodName);
	}
}
