package ca.bc.gov.hlth.hnsecure.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.test.TestMessages;

/**
 * Unit tests for FormatRTransResponse
 *
 */
public class FormatRTransResponseTest{

	@Test
	public void testFormat_when_responseStartsWithMSH(){
		String formatResponse = new FormatRTransResponse().format(TestMessages.MSG_R09_WITH_PREFIX);
		assertEquals(TestMessages.MSG_R09, formatResponse);
	}
	
	@Test
	public void testFormat_when_responseNotStartsWithMSH(){
		String formatResponse = new FormatRTransResponse().format(TestMessages.MSG_R09);
		assertEquals(TestMessages.MSG_R09, formatResponse);
	}
}
