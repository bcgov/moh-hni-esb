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
 * Purpose of this bean is to append 8 zeros to match the RTrans endpoint message specification
 * The RTrans endpoint expects 8 byte at the start of the v2Message.
 *
 */
public class FormatRTransMessage {

    private static Logger logger = LoggerFactory.getLogger(FormatRTransMessage.class);
    private static final String EIGHT_ZEROS = "00000000";

    @Handler
    public String format(String v2Message) throws CustomHNSException {
		logger.debug("{}: Appending zero's to V2 Message for RTrans endpoint {}", LoggingUtil.getMethodName(), v2Message);

		// It should be impossible for the body to be empty here (the handshake server should catch that) but handle it just in case
		if (StringUtils.isBlank(v2Message)) {
			throw new CustomHNSException(HL7_ERROR_NO_INPUT);
        }
            return EIGHT_ZEROS + v2Message;
        

    }

}
	

