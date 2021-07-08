package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_E45;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R03;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R15;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R50;
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
		String actualValue = V2MessageUtil.getReceivingApp(MSG_R50);
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testGetMsgTypeR50() {
		String expectedValue ="R50";
		String actualValue = V2MessageUtil.getMsgType(MSG_R50);
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
}
