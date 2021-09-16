
package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Handler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;

/**
 * Purpose of this bean is to remove byte count prefix before rtrans response
 * The RTrans endpoint returns message with 8 byte prefix before MSH
 * 
 * @author asrivastava
 *
 */
public class FormatRTransResponse {

	private static final Logger logger = LoggerFactory.getLogger(FormatRTransResponse.class);

	@Handler
	public String format(String v2Message) {

		logger.debug("{}: Removing byte prefix from V2 Message for RTrans POS {}", LoggingUtil.getMethodName(),
				v2Message);
		 return StringUtils.startsWith(v2Message, V2MessageUtil.SegmentType.MSH.toString()) ? v2Message : v2Message.substring(8);
	}

}
