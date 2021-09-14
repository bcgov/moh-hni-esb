package ca.bc.gov.hlth.hnsecure.audit.persistence;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.BCPHN;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.STATUS_CODE_ACTIVE;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.audit.entities.AffectedParty;
import ca.bc.gov.hlth.hnsecure.audit.entities.EventMessage;
import ca.bc.gov.hlth.hnsecure.audit.entities.EventMessageErrorLevel;
import ca.bc.gov.hlth.hnsecure.audit.entities.Transaction;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEvent;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;

/**
 * Handles persistence for ESB database audits.
 * 
 */
public abstract class AbstractAuditPersistence {    

    private static Logger logger = LoggerFactory.getLogger(AbstractAuditPersistence.class);

    private static final String PERSISTENCE_UNIT_HNI_ESB_AUDITS = "HNI-ESB-AUDITS";

    /** Audits properties **/
    
    private static final String DATABASE_HOST = System.getenv("DATABASE_HOST");
       
    private static final String DATABASE_PORT = System.getenv("DATABASE_PORT");
       
    private static final String DATABASE_NAME = ApplicationProperties.getInstance().getValue(ApplicationProperty.DATABASE_NAME);

   	private static final String DATABASE_USERNAME = System.getenv("DATABASE_USERNAME");

   	private static final String DATABASE_PASSWORD = System.getenv("DATABASE_PASSWORD");    
       
	private static final Boolean IS_AUDITS_ENABLED = Boolean.valueOf(ApplicationProperties.getInstance().getValue(ApplicationProperty.IS_AUDITS_ENABLED));

   	private static Map<String, String> persistenceUnitProperties = new HashMap<String, String>();
    
   	static {   
   		String url = String.format("jdbc:postgresql://%s:%s/%s", DATABASE_HOST, DATABASE_PORT, DATABASE_NAME);
        persistenceUnitProperties.put("javax.persistence.jdbc.url", url);
        persistenceUnitProperties.put("javax.persistence.jdbc.user", DATABASE_USERNAME);
		persistenceUnitProperties.put("javax.persistence.jdbc.password", DATABASE_PASSWORD);
    }
    
	private EntityManagerFactory emf;

	/**
	 * Constructor that sets up the Entity Manager Factory so that it is only created once.
	 * 
	 */
   	public AbstractAuditPersistence() {
   		if (Boolean.TRUE.equals(IS_AUDITS_ENABLED)) {
   			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_HNI_ESB_AUDITS, persistenceUnitProperties);
   		}
   	}
   	
   	/**
   	 * Inserts a single record.
   	 * 
   	 * @param <T>
   	 * @param record
   	 * @return
   	 */
	public <T> T insert(T record) {
		EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();
        
        et.begin();
        em.persist(record);
        et.commit();
        em.close();
        return record;
    }

	/**
	 * Handles insert a list of records.
	 * 
	 * @param <T>
	 * @param records
	 * @return
	 */
	public <T> List<T> insertList(List<T> records) {
		EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();
        
        et.begin();

        records.forEach(r -> em.persist(r));
        
        et.commit();
        em.close();
        
        return records;
    }

	/**
	 * Creates a {@link Transaction} entity populated with the values based on the transaction being processed 
	 * 
	 * @param v2Message
	 * @param transactionId
	 * @param organizationId
	 * @param eventTime
	 * @return
	 */
	public Transaction createTransaction(String v2Message, String transactionId, String organizationId, Date eventTime) {
		Transaction transaction = new Transaction();
    	transaction.setTransactionId(UUID.fromString(transactionId));
		String msgType = V2MessageUtil.getMsgType(v2Message);
		transaction.setType(msgType);		
		String hostname = getServer();
		transaction.setServer(hostname); 
		transaction.setSource(V2MessageUtil.getSendingApplication(v2Message));
    	transaction.setFacilityId(V2MessageUtil.getSendingFacility(v2Message));
		transaction.setOrganization(organizationId);
		transaction.setUserId(V2MessageUtil.getSecurity(v2Message));	
    	transaction.setStartTime(eventTime);
		return transaction;
	}

	private String getServer() {
		//TODO (dbarrett) check if we need this, it's the name of the server that processed the transaction. (this may not be relevant in openshift), and if so where it is taken from.		
		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			logger.debug("The hostname is {}", hostname);
        } catch (UnknownHostException e) {
			logger.warn("Could not get server name");
		}
		return hostname;
	}
	
	/**
	 * Create {@link AffectedParty} entity populated with values based on the persons in the v2 message being processed.
	 * 
	 * @param v2Message
	 * @param transactionId
	 * @return
	 */
	public List<AffectedParty> createAffectedParties(String v2Message, String transactionId) {
		List<AffectedParty> affectedParties = new ArrayList<AffectedParty>();
		AffectedParty affectedParty = null;
		UUID transactionUuid = UUID.fromString(transactionId);						
	
		if (StringUtils.isNotEmpty(v2Message)) {
			String[] segments = V2MessageUtil.getMessageSegments(v2Message);				
			
			String msgType = V2MessageUtil.getMsgType(v2Message);
			if (StringUtils.isNotEmpty(msgType)) {
				MessageType messageType = MessageType.valueOf(msgType);
				
				switch (messageType) {
				case ZPN:
					//PharmaNet has client info in ZCC and ZPA
					//ZCC e.g.
					//ZCC||||||||||0009735000001|
					String segment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.ZCC); 
					String[] segmentFields = V2MessageUtil.getSegmentFields(segment);
					String patientIdentifier = V2MessageUtil.getIdentifierSectionZCC(segmentFields);	//ZCC Provincial Health Care ID field e.g. 0009735000001
					affectedParty = new AffectedParty();		
					populateAffectedParty(affectedParty, transactionUuid, patientIdentifier);					
					affectedParties.add(affectedParty);
					break;
				case E45:
					//QPD e.g.
					//QPD|E45^^HNET0003|1|^^00000001^^^CANBC^XX^MOH|^^00000001^^^CANBC^XX^MOH|^^00000754^^^CANBC^XX^MOH|9020198746^^^CANBC^JHN^MOH||19421112||||||19980601||PVC^^HNET9909
					List<String> segmentsQPD = V2MessageUtil.getSegments(segments, V2MessageUtil.SegmentType.QPD);
					segmentsQPD.forEach(s -> {
						String [] fields = V2MessageUtil.getSegmentFields(s);
						String [] sections =  V2MessageUtil.getIdentifierSectionsQPD(fields);	//QPD Patient Identifier List e.g. 9020198746^^^CANBC^JHN^MOH
						String identifier = sections[0];
						AffectedParty ap = new AffectedParty();
						populateAffectedParty(ap, transactionUuid, identifier);					
						affectedParties.add(ap);
					});
					break;
				case R03:;
				case R09: 
				case R15:;
				case R50:
					/* PID e.g. 
					PID||0891250000^^^BC^PH 
					*/
					List<String> segmentsPID = V2MessageUtil.getSegments(segments, V2MessageUtil.SegmentType.PID);
					segmentsPID.forEach(s -> {
						String [] fields = V2MessageUtil.getSegmentFields(s);
						String [] sections =  V2MessageUtil.getIdentifierSectionsPID(fields);
						//affectedParty from PID External Patient ID e.g. 0891250000^^^BC^PH
						String identifier = sections[0];
						AffectedParty ap = new AffectedParty();
						populateAffectedParty(ap, transactionUuid, identifier);					
						affectedParties.add(ap);
					});
					break;
				}
			}
		}
		return affectedParties;
	}

	/**
	 * Set the fields in the {@link AffectedParty} entity with the provided or default values.
	 * 
	 * @param affectedParty
	 * @param transactionUuid
	 * @param identifier
	 */
	private void populateAffectedParty(AffectedParty affectedParty, UUID transactionUuid, String identifier) {
	    affectedParty.setTransactionId(transactionUuid);						
		affectedParty.setIdentifier(identifier);
		affectedParty.setIdentifierSource(null); //not required for this application
		affectedParty.setIdentifierType(BCPHN);
		affectedParty.setStatus(STATUS_CODE_ACTIVE);
	}

	/**
	 * Creates a {@link TransactionEvent} with the values provided. Defaults message ID to null.
	 * 
	 * @param transactionId
	 * @param eventType
	 * @param eventTime
	 * @return
	 */
	public TransactionEvent createTransactionEvent(String transactionId, TransactionEventType eventType, Date eventTime) {
		return createTransactionEvent(transactionId, eventType, eventTime, null);
	}
	
	/**
	 * Creates a {@link TransactionEvent} populated with the values provided.
	 * 
	 * @param transactionId
	 * @param eventType
	 * @param eventTime
	 * @param messageId
	 * @return
	 */
	public TransactionEvent createTransactionEvent(String transactionId, TransactionEventType eventType, Date eventTime, String messageId) {
		TransactionEvent transactionEvent = new TransactionEvent();
		transactionEvent.setTransactionId(UUID.fromString(transactionId));
		transactionEvent.setType(eventType.getValue());
		transactionEvent.setEventTime(eventTime);
		transactionEvent.setMessageId(messageId);
		
		return transactionEvent;
	}
    
	/**
	 * Creates a {@link EventMessage} populated with the values provided.
	 * @param errorLevel
	 * @param errorCode
	 * @param messageText
	 * @param transactionEvent
	 * @return
	 */
	public EventMessage createEventMessage(EventMessageErrorLevel errorLevel, String errorCode, String messageText,
			TransactionEvent transactionEvent) {
		EventMessage eventMessage = new EventMessage();
		eventMessage.setErrorLevel(errorLevel.name());
		eventMessage.setErrorCode(errorCode);
		eventMessage.setMessageText(messageText);
		eventMessage.setTransactionEventId(transactionEvent.getTransactionEventId());
		return eventMessage;
	}

}