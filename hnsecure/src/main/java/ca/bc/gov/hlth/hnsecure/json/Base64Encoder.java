/**
 *
 */
package ca.bc.gov.hlth.hnsecure.json;

import java.util.Base64;

import org.apache.camel.Handler;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dbarrett
 *
 */

public class Base64Encoder {

    private static Logger logger = LoggerFactory.getLogger(Base64Encoder.class);

    @Handler
    public String convertToBase64String(String v2Message) {
		logger.debug("convertToBase64String: {}", v2Message);

		// It should be impossible for the body to be empty here (the handshake server should catch that) but handle it just in case
		if (StringUtil.isBlank(v2Message)) {
			// TODO (dbarrett) change to throw an exception that causes an HL7Error_Msg_NoInputHL7 response if it is empty. ca.bc.gov.hlth.hnclientv2.error.NoInputHL7Exception can be used when it is moved to common code project
            throw new IllegalArgumentException("v2Message can't be null or empty");
        } else {
            return new String(Base64.getEncoder().encode(v2Message.getBytes()));
        }

    }

}
	

