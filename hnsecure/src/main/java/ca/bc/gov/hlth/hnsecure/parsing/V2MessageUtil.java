package ca.bc.gov.hlth.hnsecure.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;

/**
 * Utility class for V2 Message related tasks.
 * 
 */
public class V2MessageUtil {

	private static final Logger logger = LoggerFactory.getLogger(V2MessageUtil.class);

	public enum MessageType {
		ZPN,			//PharmaNet
		R03, R07, R09,	//RTrans
		R32,
		R15, E45, R50; 	//HIBC
	}
	
	public enum SegmentType {
		MSH, PID, QPD, ZCC, ZPA, ZZZ
	}	

	/**
	 * This method is used to get the sending application from a HL7 v2 message.
	 * 
	 * @param v2Message
	 * @return the sending application
	 */
	public static String getSendingApplication(String v2Message) {	
		String sendingApplication = "";	
		if (!StringUtils.isBlank(v2Message)) {
			String[] segmentFields = getMshSegmentFields(v2Message);
			if (Arrays.stream(segmentFields).allMatch(Objects::nonNull) && segmentFields.length > 2) {
				sendingApplication = segmentFields[2];
			}
		}
		return sendingApplication;
	}

	/**
	 * This method is used to get the sending facility from a HL7 v2 message.
	 * 
	 * @param v2Message
	 * @return the sending facility
	 */
	public static String getSendingFacility(String v2Message) {	
		String sendingFacility = "";	
		if (StringUtils.isNotBlank(v2Message)) {
			String[] segmentFields = getMshSegmentFields(v2Message);
			if (Arrays.stream(segmentFields).allMatch(Objects::nonNull) && segmentFields.length > 3) {
				sendingFacility = segmentFields[3];
			}
		}
		return sendingFacility;
	}

	/**
	 * This method is used to get the sending facility from a HL7 v2 message.
	 * 
	 * @param v2Message
	 * @return the sending facility
	 */
	public static String getSecurity(String v2Message) {	
		String security = "";	
		if (StringUtils.isNotBlank(v2Message)) {
			String[] segmentFields = getMshSegmentFields(v2Message);
			if (Arrays.stream(segmentFields).allMatch(Objects::nonNull) && segmentFields.length > 7) {
				security = segmentFields[7];
			}
		}
		return security;
	}

	private static String getMSHSegment(String v2Message) {
		String mshSegment = "";
		if (StringUtils.isNotBlank(v2Message)) {
			String trimmedMessage = StringUtils.startsWith(v2Message, V2MessageUtil.SegmentType.MSH.toString()) ? v2Message : v2Message.substring(8);
			String[] segments = V2MessageUtil.getMessageSegments(trimmedMessage);				
			mshSegment = getSegment(segments, SegmentType.MSH);
		}
		return mshSegment;
	}

	public static String[] getMshSegmentFields(String v2Message) {
		String [] segmentFields = {};
		if (StringUtils.isNotBlank(v2Message)) {
			String mshSegment = getMSHSegment(v2Message);
			segmentFields = getSegmentFields(mshSegment);			
		}
		return segmentFields;
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
	 * This method is used to get the receiving application from a HL7 message.
	 * 
	 * @param v2Message
	 * @return the receiving application
	 */
	public static String getReceivingFacility(String v2Message) {
	
		String recFacility = "";
	
		if (v2Message == null || v2Message.isEmpty()) {
			return recFacility;
		}
		String[] hl7Fields = v2Message.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
		if (hl7Fields.length > 5) {
			recFacility = hl7Fields[5];
		}
		return recFacility;
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
		
		v2Message = StringUtils.startsWith(v2Message, V2MessageUtil.SegmentType.MSH.toString()) ? v2Message : v2Message.substring(8);
		String mshSegment = getMSHSegment(v2Message);
	
		if (StringUtils.isNotBlank(mshSegment)) {
		String[] hl7MessageAtt = v2Message.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
		if (hl7MessageAtt.length > 8) {
			msgType = hl7MessageAtt[8];			
		} 
		// When response is generated, acknowledgment identifier is added at MSH(8)
		if (msgType.equals(Util.ACK)) {
			msgType = "";
		}
		// there is a special case for R50 message which the value of MSH.8 is
		// "R50^Z05".
		if (!msgType.isEmpty() && msgType.contains(Util.CARET)) {
			int index = msgType.indexOf(Util.CARET);
			msgType = msgType.substring(0, index);
		}
		}
		return msgType;
	}
	
	/**
	 * returns the user info based on the HL7 message.
	 * @param v2Message
	 * @return
	 */
	public static String getUserInfo(String v2Message) {

		String user = "";

		if (StringUtils.isEmpty(v2Message)) {
			return user;
		}

		v2Message = StringUtils.startsWith(v2Message, V2MessageUtil.SegmentType.MSH.toString()) ? v2Message
				: v2Message.substring(7);
		String mshSegment = getMSHSegment(v2Message);

		if (StringUtils.isNotBlank(mshSegment)) {
			String[] hl7MessageAtt = v2Message.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
			if (hl7MessageAtt.length > 7) {
				user = hl7MessageAtt[7];
			}
		}

		return user;
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
	

	public static String getMsgControlId(String v2Msg) {
		String methodName = LoggingUtil.getMethodName();
		String controlId = null;
		if (StringUtils.isBlank(v2Msg)) {
			logger.warn("{} - MessageControlId is blank", methodName);
			return controlId;
        }
        String[] v2DataLines = v2Msg.split("\n");
		String[] v2Segments = v2DataLines[0].split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER,-1);
		controlId = v2Segments[9];
        
         return controlId;        
	}
	
	public static String getProcessingDomain(String v2Msg) {
		String methodName = LoggingUtil.getMethodName();
		String domain = null;
		if (StringUtils.isBlank(v2Msg)) {
			logger.warn("{} - Processing domain is blank", methodName);
			return domain;
        }
        String[] v2DataLines = v2Msg.split("\n");
		String[] v2Segments = v2DataLines[0].split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER,-1);
		domain = v2Segments[10];
        
         return domain;        
	}
	
	/**
	 * @param zhdSegment
	 * ZHD|20220920115331|^^00000010|HNAIADMINISTRATION||||2.4
	 * @return organization 
	 */
	public static String getOrg(String zhdSegment) {		
		if(StringUtils.isNotEmpty(zhdSegment)) {
			String[] zhdDataSegment = zhdSegment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
			if(zhdDataSegment.length >2 && StringUtils.isNotEmpty(zhdDataSegment[2])) {
				String formattedOrg =  zhdDataSegment[2].replace("^", "");
				return formattedOrg.trim();
			}
		}
		return "";
	}
	
	/**
	 * @param pidSegment
	 * PID||PHN^^^BC^PH
	 * @return phn 
	 */
	public static String getPHN(String pidSegment) {		
		if(StringUtils.isNotEmpty(pidSegment)) {
			String[] pidDataSegment = pidSegment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
			if(pidDataSegment.length > 2 && StringUtils.isNotEmpty(pidDataSegment[2])) {
				return pidDataSegment[2].substring(0, 10);
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
	public static String getDataSegment(String v2Message, String segmentType) {
		String[] v2DataLinesPharmanet = getMessageSegments(v2Message);
	
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
	
		String[] v2DataLines = getMessageSegments(v2Message);
	
		for (String segment : v2DataLines) {
	
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
        } else if (v2Message.contains(Util.CARRIAGE_RETURN)) {
            segments = v2Message.split(Util.CARRIAGE_RETURN);
        } else {
			logger.warn("Can't split v2 message due to unknown EOL characters");
		}
		return segments;
	}
	
	/**
	 * Return the first segment of the specified type from the v2 message.
	 * 
	 * @param segments
	 * @param segmentType
	 * @return
	 */
	public static String getSegment(String[] segments, SegmentType segmentType) {
		List<String> selectedSegments = getSegments(segments, segmentType);
		return selectedSegments.isEmpty() ? null :  selectedSegments.get(0);
	}

	/**
	 * Return a list of segments of the specified type from a v2 message. e.g. all the QPD segments
	 * @param segments
	 * @param segmentType
	 * @return
	 */
	public static List<String> getSegments(String[] segments, SegmentType segmentType) {
		List<String> requiredSegments = new ArrayList<>();
		
		if (segments != null) {
			for (String segment : segments) {						
				if (segment.startsWith(segmentType.name())) {
					requiredSegments.add(segment);
				}
			}
		}
		
		if (requiredSegments.isEmpty()) {
			logger.warn("No segments of type {} found.", segmentType);
		}
		
		return requiredSegments;
	}

	public static String[] getSegmentFields(String segment) {
		if (StringUtils.isEmpty(segment)) {
			 return new String[0];
		}
		return segment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER);
	}

	public static String[] getFieldSections(String[] fields, int position) {
		String[] patientIdentifierSections = null;
		if (fields.length > position) {
			String patientIdentifierField = fields[position];	//e.g. 0891250000^^^BC^PH
			patientIdentifierSections = getFieldSections(patientIdentifierField);
		}
		return patientIdentifierSections; 
	}

	public static String[] getFieldSections(String field) {
		return field.split(Util.DOUBLE_BACKSLASH + Util.CARET);
	}

	public static String[] getIdentifierSectionsPID(String[] fields) {
		return getFieldSections(fields, 2); 
	}

	public static String[] getIdentifierSectionsQPD(String[] fields) {
		return getFieldSections(fields, 6); 
	}

	/**
	 * 
	 * @param fields
	 * @return
	 */
	public static String getIdentifierSectionZCC(String[] fields) {
		if (!StringUtils.equals(fields[0], SegmentType.ZCC.name())) {
			return null;
		}
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
		
