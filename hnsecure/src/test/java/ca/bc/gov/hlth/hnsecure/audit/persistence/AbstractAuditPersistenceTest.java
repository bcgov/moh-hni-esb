package ca.bc.gov.hlth.hnsecure.audit.persistence;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.BCPHN;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_PHARMANET_REQUEST;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_PHARMANET_RESPONSE;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_PHARMANET_ERROR_RESPONSE;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R03_REQUEST;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R03_RESPONSE;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R09_REQUEST;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R15_REQUEST;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R15_RESPONSE;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R32_CARRIAGE_RETURN_EOL;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R32_MULTI_PID_RESPONSE;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R50_Z05;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R50_ZO6;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R09_RESPONSE;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_E45_REQUEST;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_E45_RESPONSE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	public void testCreateAffectedParties_R15_INBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R15_REQUEST, AffectedPartyDirection.INBOUND,  transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("0314500001", affectedParty.getIdentifier()); //PHN from PID
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.INBOUND.getValue(), affectedParty.getDirection());
	}
	
	@Test
	public void testCreateAffectedParties_R15_OUTBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R15_RESPONSE, AffectedPartyDirection.OUTBOUND,  transactionId.toString());	
		assertTrue(aps.isEmpty());		
	}

	@Test
	public void testCreateAffectedParties_R50_Z05_INBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R50_Z05, AffectedPartyDirection.INBOUND, transactionId.toString());	
		assertTrue(aps.isEmpty()); //Z05 has no PHN		
	}
	
	@Test
	public void testCreateAffectedParties_R50_Z06_INBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R50_ZO6, AffectedPartyDirection.INBOUND, transactionId.toString());	
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("9878259011", affectedParty.getIdentifier()); //PHN from PID
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.INBOUND.getValue(), affectedParty.getDirection());		
	}
	
	@Test
	public void testCreateAffectedParties_R09_INBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		
		//R09 sample has no Affected Parties in request	 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R09_REQUEST, AffectedPartyDirection.INBOUND, transactionId.toString());		
		assertTrue(aps.isEmpty());		
	}

	@Test
	public void testCreateAffectedParties_R09_OUTBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		
		//R09 sample has multiple Affected Parties in repeating PID segments	 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R09_RESPONSE, AffectedPartyDirection.OUTBOUND, transactionId.toString());
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
	public void testCreateAffectedParties_MSG_PHARMANET_INBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		//PNP request sample has Affected Parties in ZCC segments	 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_PHARMANET_REQUEST, AffectedPartyDirection.INBOUND, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("0009735000001", affectedParty.getIdentifier()); //PHN in the ZCC
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.INBOUND.getValue(), affectedParty.getDirection());
	}
	
	@Test
	public void testCreateAffectedParties_MSG_PHARMANET_OUTBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		//PNP response sample has Affected Parties in ZCC segments	 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_PHARMANET_RESPONSE, AffectedPartyDirection.OUTBOUND, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("123456789", affectedParty.getIdentifier()); //PHN in the ZCC
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.OUTBOUND.getValue(), affectedParty.getDirection());
	}
	
	@Test
	public void testCreateAffectedParties_MSG_PHARMANET_ERROR() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		// PNP error response sample has no ZCC segment and no affected parties should be written	 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_PHARMANET_ERROR_RESPONSE, AffectedPartyDirection.OUTBOUND, transactionId.toString());
		assertTrue(aps.isEmpty());
	}
	
	@Test
	public void testCreateAffectedParties_MSG_R03_INBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R03_REQUEST, AffectedPartyDirection.OUTBOUND, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("0891250000", affectedParty.getIdentifier()); //PHN in the PID
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.OUTBOUND.getValue(), affectedParty.getDirection());
	}
	
	@Test
	public void testCreateAffectedParties_MSG_R03_OUTBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R03_RESPONSE, AffectedPartyDirection.OUTBOUND, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("123456789", affectedParty.getIdentifier());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.OUTBOUND.getValue(), affectedParty.getDirection());
	}
	
	@Test
	public void testCreateAffectedParties_MSG_R32_INBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R32_CARRIAGE_RETURN_EOL, AffectedPartyDirection.INBOUND, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("9306448169", affectedParty.getIdentifier());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.INBOUND.getValue(), affectedParty.getDirection());
	}
	
	@Test
	public void testCreateAffectedParties_MSG_R32_OUTBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		//R32 sample has multiple Affected Parties in repeating PID segments	 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R32_MULTI_PID_RESPONSE, AffectedPartyDirection.OUTBOUND, transactionId.toString());
		AffectedParty affectedParty1 = aps.get(0);
		AffectedParty affectedParty2 = aps.get(1);
		AffectedParty affectedParty3 = aps.get(2);
		assertEquals(transactionId, affectedParty1.getTransactionId());
		//Must have unique identifiers
		assertEquals(3, aps.size());
		assertEquals("9337796509", affectedParty1.getIdentifier());
		assertEquals("9360338021", affectedParty2.getIdentifier()); 
		assertEquals("9301073095", affectedParty3.getIdentifier()); 
		
		assertEquals(BCPHN, affectedParty1.getIdentifierType());
		assertEquals(AffectedPartyDirection.OUTBOUND.getValue(), affectedParty1.getDirection());
		assertEquals(AffectedPartyDirection.OUTBOUND.getValue(), affectedParty2.getDirection());
		assertEquals(AffectedPartyDirection.OUTBOUND.getValue(), affectedParty3.getDirection());
	}
	
	@Test
	public void testCreateAffectedParties_E45_INBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		//E45 sample has Affected Parties in QPD segment	 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_E45_REQUEST, AffectedPartyDirection.INBOUND,  transactionId.toString());	
		assertEquals(1, aps.size());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("9020198746", affectedParty.getIdentifier());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.INBOUND.getValue(), affectedParty.getDirection());
	}
	
	@Test
	public void testCreateAffectedParties_E45_OUTBOUND() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		//E45 sample has Affected Parties in QPD segments	 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_E45_RESPONSE, AffectedPartyDirection.OUTBOUND,  transactionId.toString());	
		assertEquals(1, aps.size());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("9390352021", affectedParty.getIdentifier());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(AffectedPartyDirection.OUTBOUND.getValue(), affectedParty.getDirection());
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
