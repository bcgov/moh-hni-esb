package ca.bc.gov.hlth.hnsecure.audit;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.entities.AffectedParty;
import ca.bc.gov.hlth.hnsecure.audit.persistence.AbstractAuditPersistence;
import ca.bc.gov.hlth.hnsecure.parsing.Util;

public class TransactionEndProcessor extends AbstractAuditPersistence implements Processor {

    private static Logger logger = LoggerFactory.getLogger(TransactionEndProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		
    	String methodName = LoggingUtil.getMethodName();
        logger.debug("Begin {}", methodName);

		String transactionId = (String)exchange.getProperty(Exchange.CORRELATION_ID);
		String messageType = (String)exchange.getIn().getHeader(Util.HEADER_MESSAGE_TYPE);
    	String v2Message = (String)exchange.getIn().getBody();
		
        //Affected Party - Get info for R03, R09, R15, E45, R50, R09(only for response)        
        List<AffectedParty> affectedParties = createAffectedParties(v2Message, transactionId);
        insertList(affectedParties);
        
        logger.debug("End {}", methodName);
	}
}
