/**
 *
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;

/**
 * Purpose of this bean is to remove byte count prefix before rtrans response
 * The RTrans endpoint returns message with 8byte prefix before MSH
 * 
 * @author asrivastava
 *
 */
public class FormatRTransResponse {

	private static Logger logger = LoggerFactory.getLogger(FormatRTransResponse.class);

	@Handler
	public String format(String v2Message) throws CustomHNSException {

		logger.debug("{}: Removing byte prefix from V2 Message for RTrans POS {}", LoggingUtil.getMethodName(),
				v2Message);
		return v2Message.substring(8);

	}

}
