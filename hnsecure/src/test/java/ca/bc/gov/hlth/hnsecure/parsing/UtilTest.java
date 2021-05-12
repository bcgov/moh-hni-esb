/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.*;
import org.junit.Test;

/**
 * @author Tony.Ma * 
 * @date Feb 3, 2021
 *
 */
public class UtilTest {

	public static String invalidBase64Characters = "yk===klsdfklk";

	@Test
	public void testNullEncodeBase64() {
		// When a null message is passed in
		// Expect a null return
		assertNull(Util.encodeBase64(null));
	}

	@Test
	public void testNullDecodeBase64() {
		// When a null message is passed in
		// Expect a null return
		assertNull(Util.decodeBase64(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecodeException() {
		// When a message is not valid base 64
		// Expect an exception
		Util.decodeBase64(invalidBase64Characters);
	}

	// TODO it might be better just to split this into two methods testing each method
	@Test
	public void testBothEncode_Decode64() {
		String expectedValue = MSG_E45;
		System.out.println(Util.encodeBase64(MSG_E45));
		// When a message is encoded and then decoded
		String	actualValue = Util.decodeBase64(Util.encodeBase64(MSG_E45));
		// Expect the decoded message to match the original message
		assertEquals(expectedValue, actualValue);
	}

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