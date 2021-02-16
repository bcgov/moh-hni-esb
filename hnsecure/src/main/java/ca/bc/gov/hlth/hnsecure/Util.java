package ca.bc.gov.hlth.hnsecure;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class Util {
	
	public final static String HL7_DELIMITER = "\\|";
	public final static String R50_SPEC_CHAR = "^";

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

	/**
	 * This method is used to get the receiving application from a HL7 message.
	 * 
	 * @param hlMsg
	 * @return the receiving application
	 */
	public static String getReceivingApp(String hlMsg) {

		String recApp = "";

		if (hlMsg == null || hlMsg.isEmpty()) {
			return recApp;
		}
		String[] hl7Fields = hlMsg.split(HL7_DELIMITER);
		if (hl7Fields.length > 4) {
			recApp = hl7Fields[4];
		}
		return recApp;
	}
	
	
	/**
	 * returns the message type based on the HL7 message.
	 * 
	 * Note: the pharmaNet message with the message type ZPN
	 * 
	 * @param hlMsg
	 * @return
	 */
	public static String getMsgType(String hlMsg) {

		String msgType="";
		
		if (hlMsg == null || hlMsg.isEmpty()) {
			return msgType;
		}
		
		String[] hl7MessageAtt = hlMsg.split(HL7_DELIMITER);
		if(hl7MessageAtt.length>8) {
			msgType = hl7MessageAtt[8];
		}
		//there is a special case for R50 message which the value of MSH.8 is "R50^Z05".
		if (msgType != null && !msgType.isEmpty() && msgType.contains(R50_SPEC_CHAR)) {
			int index = msgType.indexOf(R50_SPEC_CHAR);
			msgType = msgType.substring(0, index);
		}
		return msgType;
	}
}
