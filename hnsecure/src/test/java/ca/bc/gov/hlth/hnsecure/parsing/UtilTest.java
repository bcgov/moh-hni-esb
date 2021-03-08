/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.io.UnsupportedEncodingException;

import ca.bc.gov.hlth.hnsecure.parsing.Util;
import org.junit.Test;

/**
 * @author Tony.Ma * 
 * @date Feb 3, 2021
 *
 */
public class UtilTest {

	public static String msgE45 = "MSH|^~\\&|HR|BC00000098|RAIGET-DOC-SUM|BC0003000|19991004103039|lharris|E45|19980915000015|D|2.3"
			+ "HDR|||TRAININGAdmin\r\n" + "SFT|1.0||testorg^^orgid^^^MOH|1.0|barebones||\r\n"
			+ "QPD|E45^^HNET0003|1|^^00000001^^^CANBC^XX^MOH|^^00000001^^^CANBC^XX^MOH|^^00000754^^^CANBC^XX^MOH|9020198746^^^CANBC^JHN^MOH||19420112||||||19980601||PVC^^HNET9909||\r\n"
			+ "RCP|I|";

	public static String msgR50 = "MSH|^~\\&|HNWeb|BC01000030|RAIENROL-EMP|BC00002041|20210121120533|train96|R50^Z05|20210121120533|D|2.4||\r\n" + 
			"ZHD|20210121120533|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + 
			"PID|||||||19890101|M\r\n" + 
			"ZIA||20200801|||||||||||||Gordon^Tom^^^^^L|1102 hilda st^^^^^^^^^^^^^^^^^^^Victoria^BC^v8v2z3^^H~1102 hilda st^^^^^^^^^^^^^^^^^^^Victoria^BC^v8v2z3^^M||||||||S|AB\r\n" + 
			"IN1||||||||6337109||||20210101|20221";

	public static String msgR03="MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" + 
			"ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + 
			"PID||0000053655^^^BC^PH\r\n";
	
	public static String msgR15= "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D|2.4||\r\n" + 
			"ZHD|20201015092224|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + 
			"PID||0000073721^^^BC^PH\r\n" + 
			"IN1||||||||||||20190228\r\n";
	
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
		String expectedValue = msgE45;
		System.out.println(Util.encodeBase64(msgE45));
		// When a message is encoded and then decoded
		String	actualValue = Util.decodeBase64(Util.encodeBase64(msgE45));
		// Expect the decoded message to match the original message
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testGetReceivingAppE45() {
		String expectedValue ="RAIGET-DOC-SUM";
		String actualValue = Util.getReceivingApp(msgE45);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetMsgTypeE45() {
		String expectedValue ="E45";
		String actualValue = Util.getMsgType(msgE45);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetReceivingAppR50() {
		String expectedValue ="RAIENROL-EMP";
		String actualValue = Util.getReceivingApp(msgR50);
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testGetMsgTypeR50() {
		String expectedValue ="R50";
		String actualValue = Util.getMsgType(msgR50);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetReceivingAppR03() {
		String expectedValue ="RAIGT-PRSN-DMGR";
		String actualValue = Util.getReceivingApp(msgR03);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetMsgTypeR03() {
		String expectedValue ="R03";
		String actualValue = Util.getMsgType(msgR03);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetReceivingAppR15() {
		String expectedValue ="RAICHK-BNF-CVST";
		String actualValue = Util.getReceivingApp(msgR15);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetMsgTypeR15() {
		String expectedValue ="R15";
		String actualValue = Util.getMsgType(msgR15);
		assertEquals(expectedValue, actualValue);
	}
}