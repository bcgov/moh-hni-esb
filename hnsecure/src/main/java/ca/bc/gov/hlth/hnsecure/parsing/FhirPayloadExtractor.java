package ca.bc.gov.hlth.hnsecure.parsing;

import java.io.UnsupportedEncodingException;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.bc.gov.hlth.hnsecure.json.FHIRJsonMessage;
import ca.bc.gov.hlth.hnsecure.json.FHIRJsonUtil;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class FhirPayloadExtractor {

    private static Logger logger = LoggerFactory.getLogger(FhirPayloadExtractor.class);
    private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

    @Handler
    public static String extractFhirPayload(Exchange exchange,String fhirMessage) throws ParseException, UnsupportedEncodingException {
    	
    	String methodName = "extractFhirPayload";
        JSONObject fhirMessageJSON = (JSONObject) jsonParser.parse(fhirMessage);
        
        FHIRJsonMessage encodedExtractedMessage = FHIRJsonUtil.parseJson2FHIRMsg(fhirMessageJSON); // get the data property

        // TODO we may need to check somewhere in the message to verify the base 64 encoding
        String extractedMessage = Util.decodeBase64(encodedExtractedMessage.getV2MessageData());
        logger.debug("{} - TransactionId: {},The decode HL7 message is:{} {}", methodName, exchange.getIn().getMessageId(),System.lineSeparator(),extractedMessage);
        
        return extractedMessage;
    }    
}
