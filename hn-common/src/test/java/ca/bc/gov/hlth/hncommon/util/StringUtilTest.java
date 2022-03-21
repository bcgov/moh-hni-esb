package ca.bc.gov.hlth.hncommon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringUtilTest {

	private String invalidBase64Characters = "yk===klsdfklk";

	@Test
	void testNullEncodeBase64() {
		// When a null message is passed in
		// Expect a null return
		assertNull(StringUtil.encodeBase64(null));
	}

	@Test
	void testNullDecodeBase64() {
		// When a null message is passed in
		// Expect a null return
		assertNull(StringUtil.decodeBase64(null));
	}

	@Test
	void testDecodeException() {
		// When a message is not valid base 64
		// Expect an exception
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			StringUtil.decodeBase64(invalidBase64Characters);
		});
	}

	@Test
	void testEncode64() {
		String input = "test string";
		String expectedValue = "dGVzdCBzdHJpbmc=";
		String actualValue = StringUtil.encodeBase64(input);
		assertEquals(expectedValue, actualValue);
	}

	@Test
	void testDecode64() {
		String input = "dGVzdCBzdHJpbmc=";
		String expectedValue = "test string";
		String actualValue = StringUtil.decodeBase64(input);
		assertEquals(expectedValue, actualValue);
	}

}
