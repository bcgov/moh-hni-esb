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
	public final static String CARET = "^";
	public final static String TILDE = "~";
	public final static String ZCB_SEGMENT = "ZCB";
	public final static String RECEIVING_APP_PNP = "PNP";
	public final static String MESSAGE_TYPE_PNP = "ZPN";
	public final static String RECEIVING_APP_HNSECURE = "HNSECURE";
	public final static String PHARMA_PATTERN = "yyyy/MM/dd HH:mm:ss";
	public final static String DATE_PATTERN = "yyyyMMddHHmmss";
	public final static String GENERIC_PATTERN = "yyyyMMddHHmmssZ";
	public final static String LINE_BREAK = "\n";
	public final static String CARRIAGE_RETURN_LINE_BREAK = "\r\n";
	
	public static final String AUTHORIZATION = "Authorization";
	public static final String ACK = "ACK";
	public static final String PHARMACY_ID = "pharmacyId";
	public static final String TRACING_ID = "traceId";
	public static final String ENCODING_CHARACTERS = "^~\\&";

	public static final String HEADER_SENDING_APPLICATION = "sendingApplication";
	public static final String HEADER_SENDING_FACILITY = "sendingFacility";
	public static final String HEADER_RECEIVING_APP = "receivingApp";
	public static final String HEADER_MESSAGE_TYPE = "messageType";
	public static final String HEADER_TRANSACTION_EVENT_TYPE = "eventType";
	public static final String HEADER_TRANSACTION_EVENT_TIME = "eventTime";
	public static final String BCPHN = "BCPHN";

    public static final String STATUS_CODE_ACTIVE = "active";

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
