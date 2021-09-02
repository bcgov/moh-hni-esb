package ca.bc.gov.hlth.hnsecure.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.test.TestMessages;

/**
 * @author anumeha.srivastava
 *
 */
public class FormatRTransResponseTest{

	@Test
	public void testParseV2MessageInfoE45() throws Exception {
		String formatResponse = new FormatRTransResponse().format(TestMessages.MSG_R09_WITH_PREFIX);
		assertEquals(TestMessages.MSG_R09, formatResponse);
	}


}
