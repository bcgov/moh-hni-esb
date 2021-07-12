package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil.SegmentType;

/**
 * Utility class for V2 Message related tasks.
 * 
 */
public class V2MessageUtil {

	private static final Logger logger = LoggerFactory.getLogger(V2MessageUtil.class);

	public enum MessageType {
		ZPN,	//PharmaNet
		R03, R09, R15, E45, R50;
	}
	
	public static enum SegmentType {
		MSH, PID, QPD, ZCC, ZPA
	}

	/**
	 * This method is used to get the receiving application from a HL7 message.
	 * 
	 * @param v2Message
	 * @return the receiving application
	 */
	public static String getReceivingApp(String v2Message) {
	
		String recApp = "";
	
		if (v2Message == null || v2Message.isEmpty()) {
			return recApp;
		}
		String[] hl7Fields = v2Message.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
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
	 * @param v2Message
	 * @return
	 */
	public static String getMsgType(String v2Message) {
	
		String msgType = "";
	
		if (StringUtils.isEmpty(v2Message)) {
			return msgType;
		}
	
		String[] hl7MessageAtt = v2Message.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
		if (hl7MessageAtt.length > 8) {
			msgType = hl7MessageAtt[8];
			// When response is generated, acknowledgment identifier is added at MSH(8)
			if (msgType.equals(Util.ACK) && hl7MessageAtt.length > 9) {
				msgType = hl7MessageAtt[9];
			}
		}
		// there is a special case for R50 message which the value of MSH.8 is
		// "R50^Z05".
		if (msgType != null && !msgType.isEmpty() && msgType.contains(Util.CARET)) {
			int index = msgType.indexOf(Util.CARET);
			msgType = msgType.substring(0, index);
		}
		return msgType;
	}

	/**
	 * returns the message id based on the HL7 message.
	 * @param v2Message
	 * @return
	 */
	public static String getMsgId(String v2Message) {
	
		String msgId = "";
		
		if (StringUtils.isEmpty(v2Message)) {
			return msgId;
		}
		
	
		String[] hl7MessageAtt = v2Message.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
		if (hl7MessageAtt.length > 9) {
			msgId = hl7MessageAtt[9];
		}
	
		return msgId;
	}

	/**
	 * @param zcbSegment
	 * ZCB|PharmacyId|DateTime|TraceNumber
	 * @return trace number
	 */
	public static String getTraceNumber(String zcbSegment) {		
		if(StringUtils.isNotEmpty(zcbSegment)) {
			String[] zcbDataSegment = zcbSegment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
			if(zcbDataSegment.length >3) {
				return zcbDataSegment[3];
			}
		}
		return "";
	}

	/**
	 * @param zcbSegment
	 * ZCB|PharmacyId|DateTime|TraceNumber
	 * @return Pharmacy id
	 */
	public static String getPharmacyId(String zcbSegment) {		
		if(StringUtils.isNotBlank(zcbSegment)) {
			String[] zcbDataSegment = zcbSegment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
			if(zcbDataSegment.length >1) {
				return zcbDataSegment[1];
			}
		}
		return "";
	}

	/**
	 * @param v2Message
	 * @param segmentType
	 * @return
	 */
	public static String getZCBSegment(String v2Message, String segmentType) {
		String[] v2DataLinesPharmanet = v2Message.split(Util.LINE_BREAK);
	
		for (String segment : v2DataLinesPharmanet) {
	
			if (segment.startsWith(segmentType)) {
				String[] messageSegments = segment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
				if (messageSegments[0].equalsIgnoreCase(segmentType)) {
					return segment;
				}
			}
	
		}	
		
		return null;
	}

	/**
	 * Checks if a segment is present in incoming HL7v2 message
	 * 
	 * @param v2Message
	 * @param segmentType
	 * @return
	 */
	public static boolean isSegmentPresent(String v2Message, String segmentType) {
	
		String[] v2DataLines_Pharmanet = v2Message.split(Util.LINE_BREAK);
	
		for (String segment : v2DataLines_Pharmanet) {
	
			if (segment.startsWith(segmentType)) {
				String[] messageSegments = segment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
				if (messageSegments[0].equalsIgnoreCase(segmentType)) {
					return true;
				}
			}
	
		}
		return false;
	}

	public static String[] getMessageSegments(String v2Message) {
		String[] segments = null;
		if (v2Message.contains(Util.CARRIAGE_RETURN_LINE_BREAK)) {
			segments = v2Message.split(Util.CARRIAGE_RETURN_LINE_BREAK);
		} else if (v2Message.contains(Util.LINE_BREAK)) {
			segments = v2Message.split(Util.LINE_BREAK);
		} else {
			logger.warn("Can't split v2 message due to unknown EOL");
		}
		return segments;
	}
	
	public static String getSegment(String[] segments, V2MessageUtil.SegmentType segmentType) {
		for (String segment : segments) {						
			if (segment.startsWith(segmentType.name())) {
				return segment;
			}
		}
		logger.warn("Segment {} not found", segmentType);
		return null;
	}

	public static String[] getSegmentFields(String segment) {
		return segment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
	}

	public static String[] getIdentifierSections(String[] fields, int position) {
		String[] patientIdentifierSections = null;
		if (fields.length > position) {
			String patientIdentifierField = fields[position];	//e.g. 0891250000^^^BC^PH
			patientIdentifierSections = patientIdentifierField.split(Util.DOUBLE_BACKSLASH + Util.CARET);
		}
		return patientIdentifierSections; 
	}

	public static String[] getIdentifierSectionsPID(String[] fields) {
		return getIdentifierSections(fields, 2); 
	}

	public static String[] getIdentifierSectionsQPD(String[] fields) {
		return getIdentifierSections(fields, 6); 
	}

	public static String getIdentifierSectionZCC(String[] fields) {
		return getIdentifierSection(fields, 10);
	}

	public static String getIdentifierSection(String[] fields, int position) {
		String patientIdentifierField = null;
		if (fields.length > position) {
			patientIdentifierField = fields[position];	//e.g. 0009735000001
		}
		return patientIdentifierField; 
	}

}
		