package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_E45;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R03;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R15;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R50_Z05;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;

/**
 * Unit test class for V2MessageUtil 
 *
 */
public class V2MessageUtilTest {

	@Test
	public void testGetSendingApplicationR50() {
		String expectedValue ="HNWeb";
		String actualValue = V2MessageUtil.getSendingApplication(MSG_R50_Z05);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetSendingFacilityR50() {
		String expectedValue ="BC01000030";
		String actualValue = V2MessageUtil.getSendingFacility(MSG_R50_Z05);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetReceivingAppE45() {
		String expectedValue ="RAIGET-DOC-SUM";
		String actualValue = V2MessageUtil.getReceivingApp(MSG_E45);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetMsgTypeE45() {
		String expectedValue ="E45";
		String actualValue = V2MessageUtil.getMsgType(MSG_E45);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetReceivingAppR50() {
		String expectedValue ="RAIENROL-EMP";
		String actualValue = V2MessageUtil.getReceivingApp(MSG_R50_Z05);
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testGetMsgTypeR50() {
		String expectedValue ="R50";
		String actualValue = V2MessageUtil.getMsgType(MSG_R50_Z05);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetReceivingAppR03() {
		String expectedValue ="RAIGT-PRSN-DMGR";
		String actualValue = V2MessageUtil.getReceivingApp(MSG_R03);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetMsgTypeR03() {
		String expectedValue ="R03";
		String actualValue = V2MessageUtil.getMsgType(MSG_R03);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetReceivingAppR15() {
		String expectedValue ="RAICHK-BNF-CVST";
		String actualValue = V2MessageUtil.getReceivingApp(MSG_R15);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetMsgTypeR15() {
		String expectedValue ="R15";
		String actualValue = V2MessageUtil.getMsgType(MSG_R15);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testisSegmentPresent_True() {
		String message = SamplesToSend.validPharmanetMessage;
		boolean isValid = V2MessageUtil.isSegmentPresent(message, Util.ZCB_SEGMENT);		
		assertTrue(isValid);
	}
	
	@Test
	public void testisSegmentPresent_False() {
		String message = SamplesToSend.inValidPhramanetMessage;
		boolean isValid = V2MessageUtil.isSegmentPresent(message, Util.ZCB_SEGMENT);		
		assertFalse(isValid);
	}
	
	@Test
	public void testGetZCBSegment() {
		String message = SamplesToSend.validPharmanetMessage;
		String actualValue = V2MessageUtil.getZCBSegment(message, Util.ZCB_SEGMENT);			
		assertTrue(actualValue.startsWith(Util.ZCB_SEGMENT));
	}
	
	@Test
	public void testPharmacyId() {
		String zcbSegment =  "ZCB|BC00007007|201222|9286";
		String actualValue = V2MessageUtil.getPharmacyId(zcbSegment);
		String expectedValue = "BC00007007";		
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testTraceId() {
		String zcbSegment =  "ZCB|BC00007007|201222|9286";
		String actualValue = V2MessageUtil.getTraceNumber(zcbSegment);
		String expectedValue = "9286";		
		assertEquals(expectedValue, actualValue);;
	}
	
	@Test
	public void testGetIdentifierSectionsPID() {
		//Expected 0891250000^^^BC^PH
		String[] segments = V2MessageUtil.getMessageSegments(MSG_R03);				
		String segment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.PID);
		String[] segmentFields = V2MessageUtil.getSegmentFields(segment);
		String[] patientIdentifierSections = V2MessageUtil.getIdentifierSectionsPID(segmentFields);			
		assertEquals("0891250000", patientIdentifierSections[0]);
		assertEquals("BC", patientIdentifierSections[3]);
		assertEquals("PH", patientIdentifierSections[4]);
	}

	@Test
	public void testGetIdentifierSectionsQPD() {
		//Expected 9020198746^^^CANBC^JHN^MOH
		String[] segments = V2MessageUtil.getMessageSegments(MSG_E45);				
		String segment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.QPD);
		String[] segmentFields = V2MessageUtil.getSegmentFields(segment);
		String[] patientIdentifierSections = V2MessageUtil.getIdentifierSectionsQPD(segmentFields);			
		assertEquals("9020198746", patientIdentifierSections[0]);
		assertEquals("CANBC", patientIdentifierSections[3]);
		assertEquals("JHN", patientIdentifierSections[4]);
	}
}
