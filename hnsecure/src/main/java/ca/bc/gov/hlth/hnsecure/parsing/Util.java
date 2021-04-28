package ca.bc.gov.hlth.hnsecure.parsing;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class Util {

	public final static String HL7_DELIMITER = "|";
	public final static String R50_SPEC_CHAR = "^";
	public final static String ZCB_SEGMENT = "ZCB";
	public final static String RECEIVING_APP_PNP = "PNP";
	public final static String MESSAGE_TYPE_PNP = "ZPN";
	public final static String RECEIVING_APP_HNSECURE = "HNSECURE";
	public final static String pharmaPattern = "yyyy/MM/dd HH:mm:ss";
	public final static String genericPattern = "yyyyMMddHHmmss Z";

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
	public static String decodeBase64(String stringToDecode) {
		if (stringToDecode == null || stringToDecode.isEmpty()) {
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

		String msgType = "";

		if (hlMsg == null || hlMsg.isEmpty()) {
			return msgType;
		}

		String[] hl7MessageAtt = hlMsg.split(HL7_DELIMITER);
		if (hl7MessageAtt.length > 8) {
			msgType = hl7MessageAtt[8];
		}
		// there is a special case for R50 message which the value of MSH.8 is
		// "R50^Z05".
		if (msgType != null && !msgType.isEmpty() && msgType.contains(R50_SPEC_CHAR)) {
			int index = msgType.indexOf(R50_SPEC_CHAR);
			msgType = msgType.substring(0, index);
		}
		return msgType;
	}

	/**
	 * Checks if a segment is present in incoming HL7v2 message
	 * 
	 * @param v2Message
	 * @param segmentType
	 * @return
	 */
	public static boolean isSegmentPresent(String v2Message, String segmentType) {

		String[] v2DataLines_Pharmanet = v2Message.split("\n");

		for (String segment : v2DataLines_Pharmanet) {

			if (segment.startsWith(segmentType)) {
				String[] messageSegments = v2Message.split("|");
				if (messageSegments[0].equalsIgnoreCase(segmentType)) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * @return datetime in 'yyyy/mm/dd_hh:mm:ss' format for Pharmanet response
	 */
	public static String getPharmanetDateTime() {
	
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pharmaPattern);

		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	/**
	 * @return datetime in 'yyyyMMddHHmmss Z' format for Non-Pharmanet response
	 */
	public static String getGenericDateTime() {

		DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(genericPattern);

		return ZonedDateTime.now().format(FORMATTER);

	}

}
