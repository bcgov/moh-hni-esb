/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_E45;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R03;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R15;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R50;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
}