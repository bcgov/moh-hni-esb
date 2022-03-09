package ca.bc.gov.hlth.hnsecure.json;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.HL7_ERROR_NO_INPUT;

import java.util.Base64;

import org.apache.camel.Handler;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;

/**
 * Contains methods related to Base64 Encoding
 *
 */
public class Base64Encoder {

    private static Logger logger = LoggerFactory.getLogger(Base64Encoder.class);

    @Handler
    public String convertToBase64String(String v2Message) throws CustomHNSException {
		logger.debug("{}: Converting V2 Message to Base64 {}", LoggingUtil.getMethodName(), v2Message);

		// It should be impossible for the body to be empty here (the handshake server should catch that) but handle it just in case
		if (StringUtil.isBlank(v2Message)) {
			throw new CustomHNSException(HL7_ERROR_NO_INPUT);
        } else {
            return new String(Base64.getEncoder().encode(v2Message.getBytes()));
        }

    }

}
	

