package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CUSTOM_ERROR_INVALID_REQUEST;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_APP;

import java.util.Map;

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

    private static final Logger logger = LoggerFactory.getLogger(FhirPayloadExtractor.class);
    
    private FhirPayloadExtractor() {
	}

    @Handler
    public static void extractFhirPayload(Exchange exchange,String fhirMessage) throws ParseException, CustomHNSException {
    	
    	String methodName = LoggingUtil.getMethodName();
    	JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject fhirMessageJSON = (JSONObject) jsonParser.parse(fhirMessage);
        
        FHIRJsonMessage encodedExtractedMessage = FHIRJsonUtil.parseJson2FHIRMsg(fhirMessageJSON); // get the data property

        // Only way to verify if message is base64 encoded is to decode and check for no exception
        // In case string is not Base 64, decoder throws IllegalArgumentException. Handled that exception.
        String extractedMessage;
        try {
        	extractedMessage = Util.decodeBase64(encodedExtractedMessage.getV2MessageData());
        } catch(IllegalArgumentException e) {
        	logger.error("{} - TransactionId: {}, Exception while decoding message {}", methodName, exchange.getExchangeId(), e.getMessage());
        	throw new CustomHNSException(CUSTOM_ERROR_INVALID_REQUEST);
        }
        Map<String, Object> exchangeProperties = exchange.getProperties();		
		exchangeProperties.put(PROPERTY_RECEIVING_APP, V2MessageUtil.getReceivingApp(extractedMessage));
		
        logger.info("{} - TransactionId: {},{}, {}", methodName, exchange.getExchangeId(), "Message extracted successfully for the receiving app", exchangeProperties.get(PROPERTY_RECEIVING_APP));
		logger.debug("{} - TransactionId: {}, The decoded HL7 message is: {}", methodName, exchange.getExchangeId(), extractedMessage);
        	
		exchange.getIn().setBody(extractedMessage);
    }    
}
