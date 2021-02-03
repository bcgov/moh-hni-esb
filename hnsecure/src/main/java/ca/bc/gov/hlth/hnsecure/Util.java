package ca.bc.gov.hlth.hnsecure;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class Util {

    private Util() {
    }

	public static void requireNonBlank(String str, String msg) {
        if (str == null || str.trim().length() == 0) {
            throw new IllegalArgumentException(msg);
        }
    }
	
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
	 * @throws UnsupportedEncodingException
	 */
	public static String decodeBase64(String stringToDecode) throws UnsupportedEncodingException {
        if(stringToDecode == null || stringToDecode.isEmpty()) {
        	return null;
		}
		byte[] bytesToDecode = stringToDecode.getBytes(StandardCharsets.UTF_8);
        byte[] decodedBytes = Base64.getDecoder().decode(bytesToDecode);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        return decodedString;
    }

}
