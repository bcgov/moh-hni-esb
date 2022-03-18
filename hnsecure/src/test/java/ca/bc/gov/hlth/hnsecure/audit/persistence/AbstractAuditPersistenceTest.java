package ca.bc.gov.hlth.hnsecure.audit.persistence;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.BCPHN;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_PHARMANET;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R15;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R50_Z05;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.R09_RESPONSE_MESSAGE;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.mockito.Mockito;

import ca.bc.gov.hlth.hnsecure.audit.entities.AffectedParty;
import ca.bc.gov.hlth.hnsecure.audit.entities.AffectedPartyDirection;
import ca.bc.gov.hlth.hnsecure.audit.entities.EventMessage;
import ca.bc.gov.hlth.hnsecure.audit.entities.EventMessageErrorLevel;
import ca.bc.gov.hlth.hnsecure.audit.entities.Transaction;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEvent;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.MessageType;
import ca.bc.gov.hlth.hnsecure.test.TestPropertiesLoader;

/**
 * Test class for the non abstract methods in the {@link AbstractAuditPersistence}
 *
 */
public class AbstractAuditPersistenceTest extends TestPropertiesLoader {
	
	@Test
	public void testCreateTransaction() {
		UUID transactionId = UUID.randomUUID();
		Date eventTime = new Date();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		Transaction t = abstractAuditPersistence.createTransaction(MSG_R50_Z05, transactionId.toString(), "TEST_ORG", eventTime);
		assertEquals(transactionId, t.getTransactionId());
		assertEquals(eventTime, t.getStartTime());		
		assertEquals("HNWeb", t.getSource());		
		assertEquals("BC01000030", t.getFacilityId());		
		assertEquals("TEST_ORG", t.getOrganization());		
		assertEquals("train96", t.getUserId());		
		assertEquals(MessageType.R50.name(), t.getType());
	}

	@Test
	public void testCreateAffectedParties_R15() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R15, AffectedPartyDirection.INBOUND,  transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("0314500001", affectedParty.getIdentifier()); //PHN from PID
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.INBOUND.getValue(), affectedParty.getDirection());
	}

	@Test
	public void testCreateAffectedParties_R50() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R50_Z05, AffectedPartyDirection.INBOUND, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("", affectedParty.getIdentifier()); //Z05 has not PHN
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.INBOUND.getValue(), affectedParty.getDirection());
	}

	@Test
	public void testCreateAffectedParties_R09() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		
		//R09 sample has multiple Affected Parties in repeating PID segments	 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(R09_RESPONSE_MESSAGE, AffectedPartyDirection.OUTBOUND, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(2, aps.size());
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("1314500002", affectedParty.getIdentifier());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.OUTBOUND.getValue(), affectedParty.getDirection());
				
		affectedParty = aps.get(1);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("2564500001", affectedParty.getIdentifier());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.OUTBOUND.getValue(), affectedParty.getDirection());
	}

	@Test
	public void testCreateAffectedParties_MSG_PHARMANET() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_PHARMANET, AffectedPartyDirection.INBOUND, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("0009735000001", affectedParty.getIdentifier()); //PHN in the ZCC
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.INBOUND.getValue(), affectedParty.getDirection());
	}

	
	@Test
	public void testCreateTransactionEvent() {
		UUID transactionId = UUID.randomUUID();
		Date eventTime = new Date();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		
		TransactionEvent transactionEvent = abstractAuditPersistence.createTransactionEvent(transactionId.toString(), TransactionEventType.ERROR, eventTime, "10001");
		assertEquals(transactionId, transactionEvent.getTransactionId());
		assertEquals(eventTime, transactionEvent.getEventTime());
		assertEquals(TransactionEventType.ERROR.getValue(), transactionEvent.getType());
		assertEquals("10001", transactionEvent.getMessageId());				
	}

	@Test
	public void testCreateTransactionEvent_no_message_id() {
		UUID transactionId = UUID.randomUUID();
		Date eventTime = new Date();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		
		TransactionEvent transactionEvent = abstractAuditPersistence.createTransactionEvent(transactionId.toString(), TransactionEventType.ERROR, eventTime);
		assertEquals(transactionId, transactionEvent.getTransactionId());
		assertEquals(eventTime, transactionEvent.getEventTime());
		assertEquals(TransactionEventType.ERROR.getValue(), transactionEvent.getType());
		assertEquals(null, transactionEvent.getMessageId());				
	}


	@Test
	public void testCreateEventMessage() {
		UUID transactionId = UUID.randomUUID();
		Date eventTime = new Date();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		
		TransactionEvent transactionEvent = abstractAuditPersistence.createTransactionEvent(transactionId.toString(), TransactionEventType.ERROR, eventTime);

		EventMessage em = abstractAuditPersistence.createEventMessage(EventMessageErrorLevel.ERROR, "403", "403 Forbidden", transactionEvent);
		assertEquals(transactionEvent.getTransactionEventId(), em.getTransactionEventId());
		assertEquals(EventMessageErrorLevel.ERROR.getValue(), em.getErrorLevel());
		assertEquals("403", em.getErrorCode());
		assertEquals("403 Forbidden", em.getMessageText());
	}
}
