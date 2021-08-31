/**
 * 
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;

/**
 * @author Tony.Ma * 
 * @date Feb 3, 2021
 *
 */
public class UtilTest {

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
	public void testhexadecimallength_whenMgCntrlIdIsBlank() {
	String hexa = Util.convertStringToHex(null);
	assertEquals(48,hexa.length());
	}
	
	@Test
	public void testhexadecimallength_whenMgCntrlIdIsNotBlank() {
	String hexaString = Util.convertStringToHex("20210831112325");
	assertEquals(48,hexaString.length());
	
	String hexa = Util.convertStringToHex("202108");
	assertEquals(48,hexa.length());
	}
	
	@Test
	public void testhexadecimalString_whenMgCntrlIdIsNotBlank() {
	String hexaString = Util.convertStringToHex("20210831112325");
	assertEquals("323032313038333131313233323500000000000000000000", hexaString);
	
	}
	
}