package ca.bc.gov.hlth.hnsecure.messagevalidation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.message.PharmanetErrorResponse;

public class PharmanetErrorResponseTest {

	@Test
	public void test_buildZCA() {
		String expectedZCA = "ZCA|||50|||\n";
		PharmanetErrorResponse errorResponse = new PharmanetErrorResponse();
		String zca = errorResponse.buildZCA();
		assertEquals(zca, expectedZCA);
	}

	@Test
	public void test_buildZCB() {
		String expectedZCB = "ZCB|||\n";
		PharmanetErrorResponse errorResponse = new PharmanetErrorResponse();
		String zcb = errorResponse.buildZCB();
		assertEquals(zcb, expectedZCB);
	}

	@Test
	public void test_buildZZZ() {
		String expectedZZZ = "ZZZ||1|||||VLDT014E  The Supplied HL7 Message was improperly formatted||";
		PharmanetErrorResponse errorResponse = new PharmanetErrorResponse();
		String zzz = errorResponse.buildZZZ(ErrorMessage.HL7_ERROR_INVALID_FORMAT.getErrorSequence() + "  " + ErrorMessage.HL7_ERROR_INVALID_FORMAT.getMessage());
		assertEquals(zzz, expectedZZZ);
	}
}
