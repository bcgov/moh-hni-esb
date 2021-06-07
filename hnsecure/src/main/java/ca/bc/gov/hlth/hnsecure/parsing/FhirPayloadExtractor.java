package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CustomError_Msg_InvalidRequest;

import java.io.UnsupportedEncodingException;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.json.fhir.FHIRJsonMessage;
import ca.bc.gov.hlth.hncommon.json.fhir.FHIRJsonUtil;
import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class FhirPayloadExtractor {

    private static Logger logger = LoggerFactory.getLogger(FhirPayloadExtractor.class);

    @Handler
    public static String extractFhirPayload(Exchange exchange,String fhirMessage) throws ParseException, UnsupportedEncodingException, CustomHNSException {
    	
    	String methodName = LoggingUtil.getMethodName();
    	JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject fhirMessageJSON = (JSONObject) jsonParser.parse(fhirMessage);
        
        FHIRJsonMessage encodedExtractedMessage = FHIRJsonUtil.parseJson2FHIRMsg(fhirMessageJSON); // get the data property

        // Only way to verify if message is base64 encoded is to decode and check for no exception
        // In case string is not Base 64, decoder throws IllegalArgumentException. Handled that exception.
        String extractedMessage;
        try{
        	extractedMessage = Util.decodeBase64(encodedExtractedMessage.getV2MessageData());
        } catch(IllegalArgumentException e) {
        	logger.error("Exception while decoding message ", e);
        	throw new CustomHNSException(CustomError_Msg_InvalidRequest.getErrorMessage());
        }
        logger.debug("{} - TransactionId: {},{}", methodName, exchange.getIn().getMessageId(), "Message extracted successfully");
		logger.debug("The decoded HL7 message is:"+extractedMessage);
        
        return extractedMessage;
    }    
}
