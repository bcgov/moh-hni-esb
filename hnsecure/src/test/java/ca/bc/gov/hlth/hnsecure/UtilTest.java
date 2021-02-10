/**
 * 
 */
package ca.bc.gov.hlth.hnsecure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.io.UnsupportedEncodingException;
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

	public static String encodeMsg45 = "TVNIfF5+XCZ8SFJ8QkMwMDAwMDA5OHxSQUlHRVQtRE9DLVNVTXxCQzAwMDMwMDB8MTk5OTEwMDQxMDMwMzl8bGhhcnJpc3xFNDV8MTk5ODA5MTUwMDAwMTV8RHwyLjNIRFJ8fHxUUkFJTklOR0FkbWluDQpTRlR8MS4wfHx0ZXN0b3JnXl5vcmdpZF5eXk1PSHwxLjB8YmFyZWJvbmVzfHwNClFQRHxFNDVeXkhORVQwMDAzfDF8Xl4wMDAwMDAwMV5eXkNBTkJDXlhYXk1PSHxeXjAwMDAwMDAxXl5eQ0FOQkNeWFheTU9IfF5eMDAwMDA3NTReXl5DQU5CQ15YWF5NT0h8OTAyMDE5ODc0Nl5eXkNBTkJDXkpITl5NT0h8fDE5NDIxMTEyfHx8fHx8MTk5ODA2MDF8fFBWQ15eSE5FVDk5MDl8fA0KUkNQfEl8";
	
	public static String msgR50 = "MSH|^~\\&|HNWeb|BC01000030|RAIENROL-EMP|BC00002041|20210121120533|train96|R50^Z05|20210121120533|D|2.4||\r\n" + 
			"ZHD|20210121120533|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + 
			"PID|||||||19890101|M\r\n" + 
			"ZIA||20200801|||||||||||||Gordon^Tom^^^^^L|1102 hilda st^^^^^^^^^^^^^^^^^^^Victoria^BC^v8v2z3^^H~1102 hilda st^^^^^^^^^^^^^^^^^^^Victoria^BC^v8v2z3^^M||||||||S|AB\r\n" + 
			"IN1||||||||6337109||||20210101|20221"; 
	
	public static String msgR09 = "MSH|^~\\&|HNWeb|BC01000030|RAIPRSN-NM-SRCH|BC00002041|20191108082211|train96|R09|20191108082211|D|2.4||\r\n" + 
			"ZHD|20191108082211|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + 
			"QRD|||||||^RD||PSN\r\n" + 
			"PID||^^^BC^PH|||||1989|M\r\n" + 
			"ZIA|||||||||||||||branton\r\n";


	public static String msgR03="MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" + 
			"ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + 
			"PID||0000053655^^^BC^PH\r\n";
	
	public static String msgR15= "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D|2.4||\r\n" + 
			"ZHD|20201015092224|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + 
			"PID||0000073721^^^BC^PH\r\n" + 
			"IN1||||||||||||20190228\r\n";
	
	public static String invalidBase64Charaters = "yk===klsdfklk";
	
	
	public static String nullValue = null;
	public static String emptyValue = "";
	

	@Test(expected = IllegalArgumentException.class)
	public void testNull_RequireNonBlank(){
		Util.requireNonBlank(nullValue,"This is an expected exception!!" );
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testEmpty_RequireNonBlank(){
		Util.requireNonBlank(emptyValue,"This is an expected exception!!" );
	}
	
	@Test
	public void testNullEncodeBase64() {
		assertNull(Util.encodeBase64(nullValue));
	}

	@Test
	public void testEncodeBase64() {
		String expectedValue = encodeMsg45;
		String actualValue = Util.encodeBase64(msgE45);
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testNullDecodeBase64() throws UnsupportedEncodingException {
		assertNull(Util.decodeBase64(nullValue));
	}

	@Test
	public void testDecodeBase64() throws UnsupportedEncodingException {
		String expectedValue = msgE45;
		String	actualValue = Util.decodeBase64(encodeMsg45);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testBothEncode_Decode64() throws UnsupportedEncodingException{
		String expectedValue = msgE45;
		String	actualValue = Util.decodeBase64(Util.encodeBase64(msgE45));
		assertEquals(expectedValue, actualValue);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDecodeException() throws UnsupportedEncodingException{
		Util.decodeBase64(invalidBase64Charaters);
	}
	
	@Test
	public void testGetRecivingAppE45() {
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
	public void testGetRecivingAppR50() {
		String expectedValue ="RAIENROL-EMP";
		String actualValue = Util.getReceivingApp(msgR50);
		assertEquals(expectedValue, actualValue);
	}
	
	//this method needs to be confirmed
	@Test
	public void testGetMsgTypeR50() {
		String expectedValue ="R50";
		String actualValue = Util.getMsgType(msgR50);
		assertEquals(expectedValue, actualValue);
	}
	
	@Test
	public void testGetRecivingAppR03() {
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
	public void testGetRecivingAppR15() {
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