/**
 *
 */
package ca.bc.gov.hlth.hnsecure.json;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.HL7Error_Msg_NoInputHL7;

import java.util.Base64;

import org.apache.camel.Handler;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;

/**
 * @author dbarrett
 *
 */

public class Base64Encoder {

    private static Logger logger = LoggerFactory.getLogger(Base64Encoder.class);

    @Handler
    public String convertToBase64String(String v2Message) throws CustomHNSException {
		logger.debug("convertToBase64String: {}", v2Message);

		// It should be impossible for the body to be empty here (the handshake server should catch that) but handle it just in case
		if (StringUtil.isBlank(v2Message)) {
			throw new CustomHNSException(HL7Error_Msg_NoInputHL7.getErrorMessage());
        } else {
            return new String(Base64.getEncoder().encode(v2Message.getBytes()));
        }

    }

}
	

