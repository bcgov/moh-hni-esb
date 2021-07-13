package ca.bc.gov.hlth.hnsecure.audit;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.entities.AffectedParty;
import ca.bc.gov.hlth.hnsecure.audit.entities.Transaction;
import ca.bc.gov.hlth.hnsecure.audit.persistence.AbstractAuditPersistence;

public class TransactionProcessor extends AbstractAuditPersistence implements Processor {

    private static Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		
    	String methodName = LoggingUtil.getMethodName();
        logger.debug("Begin {}", methodName);

    	String v2Message = (String)exchange.getIn().getBody();

    	String transactionId = exchange.getExchangeId();
		Transaction transaction = createTransaction(v2Message, transactionId);
        	
        try {        	
        	insert(transaction);
        } catch (Exception ex) {
        	logger.error("Exception {}", ex.getMessage());
        }			
        
        //Affected Party - Get info for R03, R09, R15, E45, R50, R09(only for response)        
        List<AffectedParty> affectedParties = createAffectedParties(v2Message, transactionId);
        insertList(affectedParties);
        
        logger.debug("End {}", methodName);
	}
}
