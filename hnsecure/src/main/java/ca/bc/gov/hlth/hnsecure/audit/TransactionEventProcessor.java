package ca.bc.gov.hlth.hnsecure.audit;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEvent;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.audit.persistence.AbstractAuditPersistence;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;

/**
 * Processor for working with TransctionEvent.
 */
public class TransactionEventProcessor extends AbstractAuditPersistence implements Processor {

    private static Logger logger = LoggerFactory.getLogger(TransactionEventProcessor.class);

    @Override
	public void process(Exchange exchange) throws Exception {
		String methodName = LoggingUtil.getMethodName();

		// This assumes the processor is invoked via wiretap
		String transactionId = (String)exchange.getProperty(Exchange.CORRELATION_ID);
		TransactionEventType eventType = TransactionEventType.valueOf((String)exchange.getIn().getHeader(Util.HEADER_TRANSACTION_EVENT_TYPE));
		Date eventTime = (Date)exchange.getIn().getHeader(Util.HEADER_TRANSACTION_EVENT_TIME);
		
		logger.info("{} - Begin {}", methodName, eventType);
		
		String v2Message = (String)exchange.getIn().getBody();
		String messageId = null;
		switch (eventType) {
		case MESSAGE_RECEIVED:
		case TRANSACTION_START:
			messageId = V2MessageUtil.getMsgId(v2Message);
			break;
		default:
			break;		
		}

		TransactionEvent transactionEvent = createTransactionEvent(transactionId, eventType, eventTime, messageId);		
		insert(transactionEvent);
							
        logger.debug("{} - End {}", methodName, eventType);
	}


}
