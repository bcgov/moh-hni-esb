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
	
}