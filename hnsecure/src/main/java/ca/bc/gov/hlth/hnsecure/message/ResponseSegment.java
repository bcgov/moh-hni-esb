package ca.bc.gov.hlth.hnsecure.message;

import java.util.Optional;

import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;

public class ResponseSegment {

	private static final String UNKNOWN_APP = "UNKNOWNAPP";

	private static final String UNKNOWN_CLIENT = "UNKNOWNCLIENT";
	
	private static final ApplicationProperties properties = ApplicationProperties.getInstance();

	/**
	 * @param v2Segment
	 */
	public static void initSegment(String[] v2Segment, HL7Message messageObj) {
		if (v2Segment.length >= 12) {
			messageObj.setSegmentIdentifier(v2Segment[0]);
			messageObj.setEncodingCharacter(v2Segment[1]);
			messageObj.setSendingApplication(v2Segment[2]);
			messageObj.setSendingFacility(v2Segment[3]);
			messageObj.setReceivingApplication(v2Segment[4]);
			messageObj.setReceivingFacility(v2Segment[5]);
			messageObj.setDateTime(v2Segment[6]);
			messageObj.setSecurity(v2Segment[7]);
			messageObj.setMessageType(v2Segment[8]);
			messageObj.setMessageControlId(v2Segment[9]);
			messageObj.setProcessingId(v2Segment[10]);
			messageObj.setVersionId(v2Segment[11]);
		}
	}

	public String constructMSH(HL7Message messageObj) {
		StringBuilder sb = new StringBuilder();
		messageObj.setSegmentIdentifier("MSH");

		sb.append(Optional.ofNullable(messageObj.getSegmentIdentifier()).orElse(""));
		sb.append(messageObj.getFieldSeparator());

		sb.append(Util.ENCODING_CHARACTERS);
		sb.append(messageObj.getFieldSeparator());

		sb.append(Util.RECEIVING_APP_HNSECURE);
		sb.append(messageObj.getFieldSeparator());

		sb.append(Optional.ofNullable(messageObj.getReceivingFacility()).orElse(""));
		sb.append(messageObj.getFieldSeparator());

		sb.append(Optional.ofNullable(messageObj.getSendingApplication()).orElse(UNKNOWN_APP));
		sb.append(messageObj.getFieldSeparator());

		sb.append(Optional.ofNullable(messageObj.getSendingFacility()).orElse(UNKNOWN_CLIENT));
		sb.append(messageObj.getFieldSeparator());

		if (messageObj.getMessageType() == null || !messageObj.getMessageType().equals(Util.MESSAGE_TYPE_PNP)) {
			sb.append(Util.getGenericDateTime());
		} else {
			sb.append(Util.getPharmanetDateTime());
		}

		sb.append(messageObj.getFieldSeparator());

		sb.append(Optional.ofNullable(messageObj.getSecurity()).orElse(""));
		sb.append(messageObj.getFieldSeparator());

		// When constructing responses directly in HNS ESB (as opposed to something returned from a downstream system)
		// the Message Type will always be ACK
		sb.append(Util.ACK);
		sb.append(messageObj.getFieldSeparator());

		sb.append(Optional.ofNullable(messageObj.getMessageControlId()).orElse(""));
		sb.append(messageObj.getFieldSeparator());

		sb.append(Optional.ofNullable(messageObj.getProcessingId()).orElse(properties.getValue(ApplicationProperty.PROCESSING_DOMAIN)));
		sb.append(messageObj.getFieldSeparator());

		sb.append(Optional.ofNullable(messageObj.getVersionId()).orElse(properties.getValue(ApplicationProperty.VERSION)));

		sb.append(Util.LINE_BREAK);

		return sb.toString();

	}

}
