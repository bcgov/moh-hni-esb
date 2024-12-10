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
 * Formats an E45 message. Currently this means adding || to the end of the MSH segment if it is not there already. e.g.
 * 
 * MSH|^~\&|SApp|SFac|RApp|RFac|20241209161149|testUser|E45|72b09479-e171-484e-a6e1-a983a7f4f10a|D|2.3
 * 
 * to
 * 
 * MSH|^~\&|SApp|SFac|RApp|RFac|20241209161149|testUser|E45|72b09479-e171-484e-a6e1-a983a7f4f10a|D|2.3||
 * 
 */
public class FormatE45RequestMessage {

	private static Logger logger = LoggerFactory.getLogger(FormatE45RequestMessage.class);

	@Handler
	public String format(String v2Message) throws CustomHNSException {

		logger.info("{}: Converting E45 request to expected format {}", LoggingUtil.getMethodName(), v2Message);

		if (StringUtils.isBlank(v2Message)) {
			throw new CustomHNSException(HL7_ERROR_NO_INPUT);
		}

		String[] segments = V2MessageUtil.getMessageSegments(v2Message);
		String mshSegment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.MSH);

		// MSH
		String formattedMessage = formatMessage(v2Message, mshSegment, 12, 14);
		logger.info("{}: Formatted E45 request {}", LoggingUtil.getMethodName(), formattedMessage);
		return formattedMessage;
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
		return separators;
	}

}
