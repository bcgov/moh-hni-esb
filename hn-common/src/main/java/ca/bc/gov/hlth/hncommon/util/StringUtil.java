package ca.bc.gov.hlth.hncommon.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Class to contain utility methods related to manipulating Strings 
 * 
 * @author dave.p.barrett
 *
 */
public class StringUtil {

	/**
	 * return a Base64 encoding string
	 * 
	 * @param stringToEncode
	 * @return
	 */
	public static String encodeBase64(String stringToEncode) {
		if (stringToEncode == null || stringToEncode.isEmpty()) {
			return null;
		}
		return new String(Base64.getEncoder().encode(stringToEncode.getBytes()));
	}

	/**
	 * This method is for base64 decoding.
	 * 
	 * @param stringToDecode
	 * @return 64Encoding String
	 * @throws IllegalArgumentException
	 */
	public static String decodeBase64(String stringToDecode) {
		if (stringToDecode == null || stringToDecode.isEmpty()) {
			return null;
		}
		byte[] bytesToDecode = stringToDecode.getBytes(StandardCharsets.UTF_8);
		byte[] decodedBytes = Base64.getDecoder().decode(bytesToDecode);
		String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

		return decodedString;
	}

}
