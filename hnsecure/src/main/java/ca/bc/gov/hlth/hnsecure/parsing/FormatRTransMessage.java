/**
 *
 */
package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.HL7Error_Msg_NoInputHL7;

import java.util.Base64;

import org.apache.camel.Handler;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;

/**
 * Purpose of this bean is to append 8 zeros to match the RTrans endpoint message specification
 * The RTrans endpoint expects 8 byte at the start of the v2Message.
 * TODO: Create a story/task to look into if the POS will add that for us, or if we should add it (then we can make changes in the future) 
 * @author asrivastava
 *
 */
public class FormatRTransMessage {

    private static Logger logger = LoggerFactory.getLogger(FormatRTransMessage.class);
    private static final String EIGHT_ZEROS = "00000000";

    @Handler
    public String format(String v2Message) throws CustomHNSException {
		logger.debug("{}: Appending zero's to V2 Message for RTrans endpoint {}", LoggingUtil.getMethodName(), v2Message);

		// It should be impossible for the body to be empty here (the handshake server should catch that) but handle it just in case
		if (StringUtil.isBlank(v2Message)) {
			throw new CustomHNSException(HL7Error_Msg_NoInputHL7);
        } else {
            return EIGHT_ZEROS + v2Message;
        }

    }

}
	

