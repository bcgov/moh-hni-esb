package ca.bc.gov.hlth.hnsecure.audit.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;

import ca.bc.gov.hlth.hnsecure.audit.entities.AffectedParty;
import ca.bc.gov.hlth.hnsecure.audit.entities.Transaction;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEvent;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType;

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

	public <T> List<T> insertList(List<T> records) {
        EntityManager em = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_HNI_ESB_AUDITS).createEntityManager();
        EntityTransaction et = em.getTransaction();
        
        et.begin();

        records.forEach(r -> em.persist(r));
        
        et.commit();
        
        return records;
    }

	public Transaction createTransaction(String v2Message, String transactionId) {
		Transaction transaction = new Transaction();
    	transaction.setTransactionId(UUID.fromString(transactionId));
		String msgType = V2MessageUtil.getMsgType(v2Message);
		transaction.setType(msgType);
		transaction.setServer(""); //TODO (dbarrett) check if we need this, it's the name of the server that processed the transaction. (this may not be relevant in openshift), and if so where it is taken from.
		transaction.setSource(V2MessageUtil.getSendingApplication(v2Message));
    	transaction.setFacilityId(V2MessageUtil.getSendingFacility(v2Message));
//		t.setOrganization();
//		t.setUserId();	
    	transaction.setTimestamp(new Date());
		return transaction;
	}
	
	
	public List<AffectedParty> createAffectedParties(String v2Message, String transactionId) {
		List<AffectedParty> affectedParties = new ArrayList<AffectedParty>();
		AffectedParty affectedParty = null;
		UUID transactionUuid = UUID.fromString(transactionId);						
	
		if (StringUtils.isNotEmpty(v2Message)) {
			String[] segments = V2MessageUtil.getMessageSegments(v2Message);				
			String segment;
			String[] segmentFields;
			String[] patientIdentifierSections;			
			
			String msgType = V2MessageUtil.getMsgType(v2Message);
			if (StringUtils.isNotEmpty(msgType)) {
				MessageType messageType = MessageType.valueOf(msgType);
				
				switch (messageType) {
				case ZPN:
					affectedParty = new AffectedParty();		
				    affectedParty.setTransactionId(transactionUuid);						
					//PharmaNet has client info in ZCC and ZPA
					//ZCC e.g.
					//ZCC||||||||||0009735000001|
					segment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.ZCC);
					segmentFields = V2MessageUtil.getSegmentFields(segment);
					affectedParty.setIdentifier(V2MessageUtil.getIdentifierSectionZCC(segmentFields));
					affectedParty.setIdentifierSource("");
					affectedParty.setIdentifierType("");
					affectedParty.setStatus("");
					affectedParties.add(affectedParty);
					break;
				case E45:
					//QPD e.g.
					//QPD|E45^^HNET0003|1|^^00000001^^^CANBC^XX^MOH|^^00000001^^^CANBC^XX^MOH|^^00000754^^^CANBC^XX^MOH|9020198746^^^CANBC^JHN^MOH||19421112||||||19980601||PVC^^HNET9909
					segment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.QPD);
					segmentFields = V2MessageUtil.getSegmentFields(segment);
					List<String> patientIdentifiers = V2MessageUtil.getIdentifiersQPD(segmentFields);
					
					affectedParties.addAll(patientIdentifiers.stream().map(pI -> {
						AffectedParty ap = new AffectedParty();
					    ap.setTransactionId(transactionUuid);						
						String [] sections = V2MessageUtil.getFieldSections(pI);
						ap.setIdentifier(sections[0]);
						ap.setIdentifierSource(sections[3]);
						ap.setIdentifierType(sections[4]);
						ap.setStatus("");
						return ap;

					}).collect(Collectors.toList()));
					break;
				case R03:;
				case R09:;
				case R15:;
				case R50:
					affectedParty = new AffectedParty();		
				    affectedParty.setTransactionId(transactionUuid);						
					/* PID e.g. 
					PID||0891250000^^^BC^PH 
					*/
					segment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.PID);
					segmentFields = V2MessageUtil.getSegmentFields(segment);
					patientIdentifierSections = V2MessageUtil.getIdentifierSectionsPID(segmentFields);					
					//affectedParty
					affectedParty.setIdentifier(patientIdentifierSections[0]);
					affectedParty.setIdentifierSource(patientIdentifierSections[3]);
					affectedParty.setIdentifierType(patientIdentifierSections[4]);
					affectedParty.setStatus("");
					affectedParties.add(affectedParty);
					break;				
				}
			}
		}
		return affectedParties;
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