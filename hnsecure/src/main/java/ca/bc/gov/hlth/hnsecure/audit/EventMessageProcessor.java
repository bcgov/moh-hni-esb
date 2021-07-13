package ca.bc.gov.hlth.hnsecure.audit;

import java.util.Date;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.entities.EventMessage;
import ca.bc.gov.hlth.hnsecure.audit.entities.EventMessageErrorLevel;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEvent;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.audit.persistence.AbstractAuditPersistence;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;

/**
 * Processor for working with EventMessage.
 * This doesn't actually follow the Processor pattern since it's directly invoked from other Processors.
 * But functionally it's similar to the other Audit Processors.
 */
public class EventMessageProcessor extends AbstractAuditPersistence {

    private static Logger logger = LoggerFactory.getLogger(EventMessageProcessor.class);

	public void process(Exchange exchange, TransactionEventType eventType, EventMessageErrorLevel errorLevel, String errorCode, String messageText) {
		String methodName = LoggingUtil.getMethodName();

		logger.info("{} - Begin", methodName);

		String v2 = (String)exchange.getIn().getBody();
		String messageId = V2MessageUtil.getMsgId(v2);
		TransactionEvent transactionEvent = createTransactionEvent(exchange.getExchangeId(), eventType, new Date(), messageId);		
		insert(transactionEvent);

		EventMessage eventMessage = new EventMessage();
		eventMessage.setErrorLevel(errorLevel.name());
		eventMessage.setErrorCode(errorCode);
		eventMessage.setMessageText(messageText);
		eventMessage.setTransactionEventId(transactionEvent.getTransactionEventId());
		insert(eventMessage);
							
        logger.debug("{} - End", methodName);
	}

}
