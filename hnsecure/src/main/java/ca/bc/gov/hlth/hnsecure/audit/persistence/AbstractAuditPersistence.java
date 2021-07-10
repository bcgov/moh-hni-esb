package ca.bc.gov.hlth.hnsecure.audit.persistence;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.camel.Exchange;

import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEvent;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;

/**
 * Handles persistence for ESB database audits.
 * 
 */
public abstract class AbstractAuditPersistence {    

    private static final String PERSISTENCE_UNIT_HNI_ESB_AUDITS = "HNI-ESB-AUDITS";

	public <T> T insert(T record) {
        EntityManager em = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_HNI_ESB_AUDITS).createEntityManager();
        EntityTransaction et = em.getTransaction();
        
        et.begin();
        em.persist(record);
        et.commit();
        
        return record;
    }

	public TransactionEvent createTransactionEvent(Exchange exchange, TransactionEventType eventType) {
		return createTransactionEvent(exchange, eventType, null);
	}
	
	public TransactionEvent createTransactionEvent(Exchange exchange, TransactionEventType eventType, String messageId) {
		TransactionEvent transactionEvent = new TransactionEvent();
		transactionEvent.setTransactionId(UUID.fromString(exchange.getExchangeId()));
		transactionEvent.setType(eventType.getValue());
		transactionEvent.setMessageId(null);
		
		return transactionEvent;
	}
    
}