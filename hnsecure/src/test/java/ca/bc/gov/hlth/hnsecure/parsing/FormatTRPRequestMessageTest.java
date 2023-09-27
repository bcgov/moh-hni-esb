package ca.bc.gov.hlth.hnsecure.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.test.TestMessages;

/**
 * Unit tests for TRP formal format
 *
 */
public class FormatTRPRequestMessageTest {

	@Test
	public void testFormat_when_requestIsNotFormal() throws CustomHNSException {

		String formatResponse = new FormatTRPRequestMessage().format(TestMessages.MSG_TRP_REQUEST);
		assertEquals(TestMessages.MSG_FORMATTED_TRP_REQUEST, formatResponse);

	}
	
	@Test
	public void testFormat_when_requestIsFormal() throws CustomHNSException {

		String formatResponse = new FormatTRPRequestMessage().format(TestMessages.MSG_TRP_FORMAL_REQUEST);
		assertEquals(TestMessages.MSG_FORMATTED_TRP_REQUEST, formatResponse);

	}

}
