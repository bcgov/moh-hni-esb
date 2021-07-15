package ca.bc.gov.hlth.hnsecure.audit;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.entities.Transaction;
import ca.bc.gov.hlth.hnsecure.audit.persistence.AbstractAuditPersistence;
import ca.bc.gov.hlth.hnsecure.parsing.Util;

/**
 * Processor for handling Transaction auditing
 *
 */
public class TransactionProcessor extends AbstractAuditPersistence implements Processor {

    private static Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		
    	String methodName = LoggingUtil.getMethodName();
        logger.debug("Begin {}", methodName);

		String transactionId = (String)exchange.getProperty(Exchange.CORRELATION_ID);
    	String v2Message = (String)exchange.getIn().getBody();
		String accessToken = (String) exchange.getIn().getHeader(Util.AUTHORIZATION);
		String organizationId = Util.getSendingFacility(accessToken);
		Date eventTime = (Date)exchange.getIn().getHeader(Util.HEADER_TRANSACTION_EVENT_TIME);
		Transaction transaction = createTransaction(v2Message, transactionId, organizationId, eventTime);
        	
        try {        	
        	insert(transaction);
        } catch (Exception ex) {
        	logger.error("Exception {}", ex.getMessage());
        }			
        
        logger.debug("End {}", methodName);
	}
}
