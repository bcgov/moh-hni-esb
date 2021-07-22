package ca.bc.gov.hlth.hnsecure.parsing;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public final class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	public final static String DOUBLE_BACKSLASH = "\\"; // For using specific string in regex mathces
	public final static String HL7_DELIMITER = "|";
	public final static String R50_SPEC_CHAR = "^";
	public final static String ZCB_SEGMENT = "ZCB";
	public final static String RECEIVING_APP_PNP = "PNP";
	public final static String MESSAGE_TYPE_PNP = "ZPN";
	public final static String RECEIVING_APP_HNSECURE = "HNSECURE";
	public final static String PHARMA_PATTERN = "yyyy/MM/dd HH:mm:ss";
	public final static String DATE_PATTERN = "yyyyMMddHHmmss";
	public final static String GENERIC_PATTERN = "yyyyMMddHHmmss Z";
	public final static String LINE_BREAK = "\n";
	public static final String AUTHORIZATION = "Authorization";
	public static final String ACK = "ACK";
	public static final String PHARMACY_ID = "pharmacyId";
	public static final String TRACING_ID = "traceId";
	public static final String R03 = "R03";
	public static final String R07 = "R07";
	public static final String R09 = "R09";

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
		String[] hl7Fields = hlMsg.split(DOUBLE_BACKSLASH + HL7_DELIMITER);
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

		if (StringUtils.isEmpty(hlMsg)) {
			return msgType;
		}

		String[] hl7MessageAtt = hlMsg.split(DOUBLE_BACKSLASH + HL7_DELIMITER);
		if (hl7MessageAtt.length > 8) {
			msgType = hl7MessageAtt[8];
			// When response is generated, acknowledgment identifier is added at MSH(8)
			if (msgType.equals(ACK) && hl7MessageAtt.length > 9) {
				msgType = hl7MessageAtt[9];
			}
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
	 * returns the message id based on the HL7 message.
	 * @param hlMsg
	 * @return
	 */
	public static String getMsgId(String hlMsg) {

		String msgId = "";
		
		if (StringUtils.isEmpty(hlMsg)) {
			return msgId;
		}
		

		String[] hl7MessageAtt = hlMsg.split(DOUBLE_BACKSLASH + HL7_DELIMITER);
		if (hl7MessageAtt.length > 9) {
			msgId = hl7MessageAtt[9];
		}
	
		return msgId;
	}
	
	/*
	 * The FacilityId is the legacy way to track connected clients and is now set as
	 * the ClientId of the client application In the access token this is the 'azp'
	 * field
	 */
	public static String getSendingFacility(String auth) {
		String clientId = "";
		if (!StringUtils.isEmpty(auth)) {
			String[] split = auth.split("\\.");
			String decodeAuth = Util.decodeBase64(split[1]);
			try {
				JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
				JSONObject jsonObject = (JSONObject) jsonParser.parse(decodeAuth);
				clientId = (String) jsonObject.get("azp");
			} catch (net.minidev.json.parser.ParseException e) {
				logger.error(e.getMessage());
			}
		}
		return clientId;
	}

	/**
	 * Checks if a segment is present in incoming HL7v2 message
	 * 
	 * @param v2Message
	 * @param segmentType
	 * @return
	 */
	public static boolean isSegmentPresent(String v2Message, String segmentType) {

		String[] v2DataLines_Pharmanet = v2Message.split(LINE_BREAK);

		for (String segment : v2DataLines_Pharmanet) {

			if (segment.startsWith(segmentType)) {
				String[] messageSegments = segment.split(DOUBLE_BACKSLASH + HL7_DELIMITER);
				if (messageSegments[0].equalsIgnoreCase(segmentType)) {
					return true;
				}
			}

		}
		return false;
	}
	
	/**
	 * @param v2Message
	 * @param segmentType
	 * @return
	 */
	public static String getZCBSegment(String v2Message, String segmentType) {
		String[] v2DataLinesPharmanet = v2Message.split(LINE_BREAK);

		for (String segment : v2DataLinesPharmanet) {

			if (segment.startsWith(segmentType)) {
				String[] messageSegments = segment.split(DOUBLE_BACKSLASH + HL7_DELIMITER);
				if (messageSegments[0].equalsIgnoreCase(segmentType)) {
					return segment;
				}
			}

		}	
		
		return null;
	}
	
	
	/**
	 * @param zcbSegment
	 * ZCB|PharmacyId|DateTime|TraceNumber
	 * @return Pharmacy id
	 */
	public static String getPharmacyId(String zcbSegment) {		
		if(StringUtils.isNotBlank(zcbSegment)) {
			String[] zcbDataSegment = zcbSegment.split(DOUBLE_BACKSLASH + HL7_DELIMITER);
			if(zcbDataSegment.length >1) {
				return zcbDataSegment[1];
			}
		}
		return "";
	}
	
	
	/**
	 * @param zcbSegment
	 * ZCB|PharmacyId|DateTime|TraceNumber
	 * @return trace number
	 */
	public static String getTraceNumber(String zcbSegment) {		
		if(StringUtils.isNotEmpty(zcbSegment)) {
			String[] zcbDataSegment = zcbSegment.split(DOUBLE_BACKSLASH + HL7_DELIMITER);
			if(zcbDataSegment.length >3) {
				return zcbDataSegment[3];
			}
		}
		return "";
	}
	
	/**
	 * @return datetime in 'yyyymmddhhmmss' format for file drops
	 */
	public static String getDateTime() {
	
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_PATTERN);

		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}


	/**
	 * @return datetime in 'yyyy/mm/dd_hh:mm:ss' format for Pharmanet response
	 */
	public static String getPharmanetDateTime() {
	
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(PHARMA_PATTERN);

		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	/**
	 * @return datetime in 'yyyyMMddHHmmss Z' format for Non-Pharmanet response
	 */
	public static String getGenericDateTime() {

		DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(GENERIC_PATTERN);

		return ZonedDateTime.now().format(FORMATTER);

	}
	
    /**
     * Return a list of values from a comma delimited property
     * If string does not have 
     * @param commaDelimitedProperties a String of comma delimited values
     * @return List
     */
    public static List<String> getPropertyAsList(String commaDelimitedProperties) {
        List<String> propertyList = Collections.emptyList();
        if (commaDelimitedProperties != null && !commaDelimitedProperties.isBlank()) {
            propertyList = Arrays.asList(commaDelimitedProperties.split("\\s*,\\s*"));
        }
        return propertyList;
    }
    
	/**
	 * @param exchange
	 * @return filename in the format
	 *         {messageid}-{messagetype}-{facilityid}-{messagedate}-{request/response}.txt
	 */
	public static String buildFileName(String sendingFacility, String transactionId,
			String msgType) {
		String dateTime = Util.getDateTime();
		String fileName = transactionId + "-" + msgType + "-" + sendingFacility + "-" + dateTime + "-";
		return fileName;
	}

    /**
     * Return a set of values from a comma delimited property
     *
     * @param commaDelimitedProperties a String of comma delimited values
     * @return Set
     */
    public static Set<String> getPropertyAsSet(String commaDelimitedProperties) {
        return new HashSet<>(getPropertyAsList(commaDelimitedProperties));
    }
    
    
}
