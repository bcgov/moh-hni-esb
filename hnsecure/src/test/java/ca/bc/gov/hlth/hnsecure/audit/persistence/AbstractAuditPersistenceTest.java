package ca.bc.gov.hlth.hnsecure.audit.persistence;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.BCPHN;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.STATUS_CODE_ACTIVE;
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
import ca.bc.gov.hlth.hnsecure.audit.entities.Transaction;
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
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R15, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("0314500001", affectedParty.getIdentifier()); //PHN from PID
		assertEquals(null, affectedParty.getIdentifierSource());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(STATUS_CODE_ACTIVE, affectedParty.getStatus());
	}

	@Test
	public void testCreateAffectedParties_R50() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_R50_Z05, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("", affectedParty.getIdentifier()); //Z05 has not PHN
		assertEquals(null, affectedParty.getIdentifierSource());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(STATUS_CODE_ACTIVE, affectedParty.getStatus());
	}

	@Test
	public void testCreateAffectedParties_R09() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
		
		//R09 sample has multiple Affected Parties in repeating PID segments	 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(R09_RESPONSE_MESSAGE, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("1314500002", affectedParty.getIdentifier());
		assertEquals(null, affectedParty.getIdentifierSource());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(STATUS_CODE_ACTIVE, affectedParty.getStatus());
		
		affectedParty = aps.get(1);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("2564500001", affectedParty.getIdentifier());
		assertEquals(null, affectedParty.getIdentifierSource());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(STATUS_CODE_ACTIVE, affectedParty.getStatus());
	}

	@Test
	public void testCreateAffectedParties_MSG_PHARMANET() {
		UUID transactionId = UUID.randomUUID();
		
		AbstractAuditPersistence abstractAuditPersistence = Mockito.mock(
				AbstractAuditPersistence.class, 
			    Mockito.CALLS_REAL_METHODS);
			 
		List<AffectedParty> aps = abstractAuditPersistence.createAffectedParties(MSG_PHARMANET, transactionId.toString());
		AffectedParty affectedParty = aps.get(0);
		assertEquals(transactionId, affectedParty.getTransactionId());
		assertEquals("0009735000001", affectedParty.getIdentifier()); //PHN in the ZCC
		assertEquals(null, affectedParty.getIdentifierSource());
		assertEquals(BCPHN, affectedParty.getIdentifierType());
		assertEquals(STATUS_CODE_ACTIVE, affectedParty.getStatus());
	}

}
