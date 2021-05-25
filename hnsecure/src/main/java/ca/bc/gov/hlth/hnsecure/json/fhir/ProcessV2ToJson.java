package ca.bc.gov.hlth.hnsecure.json.fhir;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.HL7Error_Msg_NoInputHL7;

import org.apache.camel.Handler;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.json.fhir.FHIRJsonUtil;
import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;

public class ProcessV2ToJson {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessV2ToJson.class);
	
	@Handler
	public String convertToFHIRJson(String base64V2Message) throws CustomHNSException {
		logger.debug("{}: Trying to create a JSON Message {}", LoggingUtil.getMethodName(), base64V2Message);

		// It should be impossible for the body to be empty here (the handshake server should catch that) but handle it just in case
		if (StringUtil.isBlank(base64V2Message)) {
			throw new CustomHNSException(HL7Error_Msg_NoInputHL7.getErrorMessage());
		}
		return FHIRJsonUtil.createFHIRJsonObj(base64V2Message).toString();
	}
}
