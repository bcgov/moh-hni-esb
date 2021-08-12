package ca.bc.gov.hlth.hnsecure.audit;

import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.entities.AffectedParty;
import ca.bc.gov.hlth.hnsecure.audit.entities.Transaction;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEvent;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.audit.persistence.AbstractAuditPersistence;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType;

/**
 * Processor for logging auditing information.
 * 
 */
public class AuditProcessor extends AbstractAuditPersistence implements Processor {

    private static Logger logger = LoggerFactory.getLogger(AuditProcessor.class);

    @Override
	public void process(Exchange exchange) throws Exception {
		String methodName = LoggingUtil.getMethodName();

		TransactionEventType eventType = (TransactionEventType)exchange.getProperty(Util.PROPERTY_TRANSACTION_EVENT_TYPE);
		logger.debug("{} - Begin {}", methodName, eventType);
		
		// This assumes the processor is invoked via wiretap
		String transactionId = (String)exchange.getProperty(Exchange.CORRELATION_ID);
		Date eventTime = (Date)exchange.getProperty(Util.PROPERTY_TRANSACTION_EVENT_TIME);
		
		String v2Message = (String)exchange.getIn().getBody();
		String msgType = V2MessageUtil.getMsgType(v2Message);
		String messageId = null;
		boolean logAffectedParties = false;
		
		switch (eventType) {
		case TRANSACTION_START:
			
			createTransactionAudit(exchange, transactionId, eventTime, v2Message);
	        
	        //Affected Party - On transaction start log affected party info for R03, R09, R15, E45, R50        
			if (!StringUtils.equals(msgType, MessageType.R09.name())) {
				logAffectedParties = true;
			}		
			
	        messageId = V2MessageUtil.getMsgId(v2Message);
			break;
		case MESSAGE_RECEIVED:
	        
			//Affected Party - On message received log affected party info for R09 as it's only available in the response.        
			if (StringUtils.equals(msgType, MessageType.R09.name())) {
				logAffectedParties = true;
			}

	        messageId = V2MessageUtil.getMsgId(v2Message);
			break;
		default:
			break;		
		}

		TransactionEvent transactionEvent = createTransactionEvent(transactionId, eventType, eventTime, messageId);		
		insert(transactionEvent);
		
		if (logAffectedParties) {
			List<AffectedParty> affectedParties = createAffectedParties(v2Message, transactionId);
			if (affectedParties.size() > 0) {
				insertList(affectedParties);
			}
		}		
        logger.debug("{} - End {}", methodName, eventType);
	}

	private void createTransactionAudit(Exchange exchange, String transactionId, Date eventTime, String v2Message) {
		String accessToken = (String) exchange.getIn().getHeader(Util.AUTHORIZATION);
		String organizationId = Util.getSendingFacility(accessToken);
		Transaction transaction = createTransaction(v2Message, transactionId, organizationId, eventTime);	        	
		insert(transaction);
	}
}
