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
		String zzzSegment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.ZZZ);
		String[] zzzSegmentFields = zzzSegment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER, -1);
		int zzzSegmentsLength = zzzSegmentFields.length;

		// Apply formal formatting only for TRP request profile
		if (zzzSegmentsLength >= 1 && zzzSegmentFields[1].equalsIgnoreCase(Util.PROFILE_TYPE_TRP)) {
			if (zzzSegmentsLength >= 6 && zzzSegmentsLength < 11) {
				int noOfMissingSeparators = 11 - zzzSegmentsLength;
				// Append missing separators
				String mSeparators = buildMissingSeparators(noOfMissingSeparators);
				String formattedZZZSegment = zzzSegment + mSeparators;
				String replacedMsg = v2Message.replace(zzzSegment, formattedZZZSegment);
				v2Message = replacedMsg;
			}

			// MSH
			String mshSegment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.MSH);

			String[] mshSegmentFields = mshSegment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER, -1);
			int mshSegmnetsLength = mshSegmentFields.length;
			if (mshSegmentFields.length >= 12 && mshSegmentFields.length < 14) {
				int noOfMissingSeparators = 14 - mshSegmnetsLength;
				// Append missing separators
				String mSeparators = buildMissingSeparators(noOfMissingSeparators);
				String formattedMSHSegment = mshSegment + mSeparators;
				String replacedMsg = v2Message.replace(mshSegment, formattedMSHSegment);
				v2Message = replacedMsg;
			}

			// ZCC
			String zccSegment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.ZCC);

			String[] zccSegmentFields = zccSegment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER, -1);
			if (zccSegmentFields.length == 11) {
				String formattedZCCSegment = zccSegment.concat(Util.HL7_DELIMITER);
				String replacedMsg = v2Message.replace(zccSegment, formattedZCCSegment);
				v2Message = replacedMsg;
			}

			logger.debug("{}: Formatted TRP request {}", LoggingUtil.getMethodName(), v2Message);

		}
		return v2Message;

	}

	private String buildMissingSeparators(int noOfSeparators) {
		String separators = "";
		for (int i = 0; i < noOfSeparators; i++) {
			separators = separators.concat(Util.HL7_DELIMITER);
		}
		return separators.trim();
	}

}
