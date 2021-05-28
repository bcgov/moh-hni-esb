/**
 * 
 */
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
 * @author Tony.Ma * 
 * @date Feb 3, 2021
 *
 */
public class UtilTest {

	@Test
	public void testGetReceivingAppE45() {
		String expectedValue ="RAIGET-DOC-SUM";
		String actualValue = Util.getReceivingApp(MSG_E45);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetMsgTypeE45() {
		String expectedValue ="E45";
		String actualValue = Util.getMsgType(MSG_E45);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetReceivingAppR50() {
		String expectedValue ="RAIENROL-EMP";
		String actualValue = Util.getReceivingApp(MSG_R50);
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testGetMsgTypeR50() {
		String expectedValue ="R50";
		String actualValue = Util.getMsgType(MSG_R50);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetReceivingAppR03() {
		String expectedValue ="RAIGT-PRSN-DMGR";
		String actualValue = Util.getReceivingApp(MSG_R03);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetMsgTypeR03() {
		String expectedValue ="R03";
		String actualValue = Util.getMsgType(MSG_R03);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetReceivingAppR15() {
		String expectedValue ="RAICHK-BNF-CVST";
		String actualValue = Util.getReceivingApp(MSG_R15);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetMsgTypeR15() {
		String expectedValue ="R15";
		String actualValue = Util.getMsgType(MSG_R15);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetSendingFacility() {
		String accessToken =SamplesToSend.AUTH_HEADER;
		String actualValue = Util.getSendingFacility(accessToken);
		String expectedValue = "moh_hnclient_dev";		
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetSendingFacility_whenBlank() {
		String accessToken =null;
		String actualValue = Util.getSendingFacility(accessToken);
		String expectedValue = "";		
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testisSegmentPresent_True() {
		String message = SamplesToSend.validPharmanetMessage;
		boolean isValid = Util.isSegmentPresent(message, Util.ZCB_SEGMENT);		
		assertTrue(isValid);
	}
	
	@Test
	public void testisSegmentPresent_False() {
		String message = SamplesToSend.inValidPhramanetMessage;
		boolean isValid = Util.isSegmentPresent(message, Util.ZCB_SEGMENT);		
		assertFalse(isValid);
	}
	
	@Test
	public void testGetZCBSegment() {
		String message = SamplesToSend.validPharmanetMessage;
		String actualValue = Util.getZCBSegment(message, Util.ZCB_SEGMENT);			
		assertTrue(actualValue.startsWith(Util.ZCB_SEGMENT));
	}
	
	@Test
	public void testPharmacyId() {
		String zcbSegment =  "ZCB|BC00007007|201222|9286";
		String actualValue = Util.getPharmacyId(zcbSegment);
		String expectedValue = "BC00007007";		
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testTraceId() {
		String zcbSegment =  "ZCB|BC00007007|201222|9286";
		String actualValue = Util.getTraceNumber(zcbSegment);
		String expectedValue = "9286";		
		assertEquals(expectedValue, actualValue);;
	}
}