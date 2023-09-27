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

		// It should be impossible for the body to be empty here (the handshake server
		// should catch that) but handle it just in case
		if (StringUtils.isBlank(v2Message)) {
			throw new CustomHNSException(HL7_ERROR_NO_INPUT);
		}

		String[] segments = V2MessageUtil.getMessageSegments(v2Message);
		String zzzSegment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.ZZZ);
		String[] zzzSegmentFields = zzzSegment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER, -1);

		// Apply formal formatting only for TRP transaction id
		if (zzzSegmentFields.length >= 1 && zzzSegmentFields[1].equalsIgnoreCase("TRP")) {

			// Append missing optional fields
			if (zzzSegmentFields.length >= 6 && zzzSegmentFields.length < 11) {
				while (zzzSegmentFields.length < 11) {
					String formattedSegment = zzzSegment.concat(Util.HL7_DELIMITER);
					String replacedmsg = v2Message.replace(zzzSegment, formattedSegment);
					v2Message = replacedmsg;
					String[] formattedZZZSegments = V2MessageUtil.getMessageSegments(v2Message);
					String formattedZZZSegment = V2MessageUtil.getSegment(formattedZZZSegments,
							V2MessageUtil.SegmentType.ZZZ);
					String[] updatedSegmentFields = formattedZZZSegment
							.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER, -1);
					zzzSegmentFields = updatedSegmentFields;
				}
			}

			// MSH
			String segment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.MSH);

			String[] mshSegmentFields = segment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER, -1);
			if (mshSegmentFields.length >= 12 && mshSegmentFields.length < 14) {
				while (mshSegmentFields.length < 14) {
					String formattedSegment = segment.concat(Util.HL7_DELIMITER);
					String replacedmsg = v2Message.replace(segment, formattedSegment);
					v2Message = replacedmsg;
					String[] formattedMSHSegments = V2MessageUtil.getMessageSegments(v2Message);
					String formattedMSHSegment = V2MessageUtil.getSegment(formattedMSHSegments,
							V2MessageUtil.SegmentType.MSH);
					String[] updatedSegmentFields = formattedMSHSegment
							.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER, -1);
					mshSegmentFields = updatedSegmentFields;
				}

			}

			// ZCC
			String zccSegment = V2MessageUtil.getSegment(segments, V2MessageUtil.SegmentType.ZCC);

			String[] zccSegmentFields = zccSegment.split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER, -1);
			if (zccSegmentFields.length == 11) {
				String formattedSegment = zccSegment.concat(Util.HL7_DELIMITER);
				String replacedmsg = v2Message.replace(zccSegment, formattedSegment);
				v2Message = replacedmsg;
			}

			logger.debug("{}: Formatted TRP request {}", LoggingUtil.getMethodName(), v2Message);
		}

		return v2Message;

	}

}
