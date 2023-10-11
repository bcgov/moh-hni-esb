/**
 *
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.HL7_ERROR_NO_INPUT;

import org.apache.camel.Handler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;

/**
 * Purpose of this bean is to convert TRP request message to formal format. The
 * PNET endpoint expects optional field separators to be appended for MSH, ZZZ
 * and ZCC segment
 * https://dev.azure.com/cgi-vic-hlth/HNI%20Modernization/_workitems/edit/10462/
 *
 */
public class FormatTRPRequestMessage {

	private static Logger logger = LoggerFactory.getLogger(FormatTRPRequestMessage.class);

	@Handler
	public String format(String v2Message) throws CustomHNSException {

		logger.debug("{}: Converting TRP request to formal format {}", LoggingUtil.getMethodName(), v2Message);

		if (StringUtils.isBlank(v2Message)) {
			throw new CustomHNSException(HL7_ERROR_NO_INPUT);
		}

		String[] segments = V2MessageUtil.getMessageSegments(v2Message);
		String mshSegment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.MSH);
		String zzzSegment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.ZZZ);
		String zccSegment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.ZCC);

		if (StringUtils.isNotEmpty(zzzSegment)) {

			String[] zzzSegmentFields = zzzSegment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER, -1);

			int zzzSegmentsLength = zzzSegmentFields.length;

			// Apply formal formatting only for TRP request profile
			if (zzzSegmentsLength >= 1 && zzzSegmentFields[1].equalsIgnoreCase(Util.PROFILE_TYPE_TRP)) {

				// ZZZ
				String formattedMessage = formatMessage(v2Message, zzzSegment, 6, 11);

				// MSH
				formattedMessage = formatMessage(formattedMessage, mshSegment, 12, 14);

				// ZCC
				formattedMessage = formatMessage(formattedMessage, zccSegment, 11, 12);

				logger.debug("{}: Formatted TRP request {}", LoggingUtil.getMethodName(), formattedMessage);

				return formattedMessage; // return formatted TRP message

			} else {
				return v2Message;
			}
		} else {
			return v2Message;
		}

	}

	private String formatMessage(String v2Message, String segment, int minSegment, int maxSegment) {
		if (StringUtils.isNotEmpty(segment)) {
			String[] segmentFields = segment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER, -1);

			int segmnetsLength = segmentFields.length;
			if (segmentFields.length >= minSegment && segmentFields.length < maxSegment) {
				int noOfMissingSeparators = maxSegment - segmnetsLength;
				// Append missing separators
				String missingSeparators = buildMissingSeparators(noOfMissingSeparators);
				String formattedSegment = segment + missingSeparators;
				return v2Message.replace(segment, formattedSegment);
			} else {
				return v2Message;
			}
		} else {
			return v2Message;
		}
	}

	private String buildMissingSeparators(int noOfSeparators) {
		String separators = "";
		for (int i = 0; i < noOfSeparators; i++) {
			separators = separators.concat(Util.HL7_DELIMITER);
		}
		return separators.trim();
	}

}
