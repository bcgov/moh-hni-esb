package ca.bc.gov.hlth.hnsecure.audit;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.entities.EventMessage;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEvent;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.audit.persistence.AbstractAuditPersistence;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;

/**
 * This doesn't actually follow the Processor pattern since it's directly invoked from other Processors.
 * But functionally it's similar to the other Audit Processors.
 */
public class EventMessageProcessor extends AbstractAuditPersistence {

    private static Logger logger = LoggerFactory.getLogger(EventMessageProcessor.class);

	public void process(Exchange exchange, TransactionEventType eventType) {
		String methodName = LoggingUtil.getMethodName();

		logger.debug("{} - Begin", methodName);		
		
		TransactionEvent transactionEvent = createTransactionEvent(exchange, eventType);		
		insert(transactionEvent);
		
		EventMessage eventMessage = new EventMessage();
		eventMessage.setErrorCode("401");
		eventMessage.setErrorLevel("WARNING");
		eventMessage.setMessageText(ErrorMessage.CustomError_Msg_MissingAuthKey.toString());
		eventMessage.setTransactionEventId(transactionEvent.getTransactionEventId());
		insert(eventMessage);
							
        logger.debug("{} - End", methodName);
	}
}
