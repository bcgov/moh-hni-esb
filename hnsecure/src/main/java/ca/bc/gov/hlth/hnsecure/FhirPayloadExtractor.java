package ca.bc.gov.hlth.hnsecure;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnclientv2.json.FHIRJsonMessage;
import ca.bc.gov.hlth.hnclientv2.json.FHIRJsonUtil;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class FhirPayloadExtractor {

    private static Logger logger = LoggerFactory.getLogger(FhirPayloadExtractor.class);
    private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

    @Handler
    public static String extractFhirPayload(String fhirMessage) throws Exception {

        JSONObject fhirMessageJSON = (JSONObject) jsonParser.parse(fhirMessage);
        
        FHIRJsonMessage encodedExtractedMessage = FHIRJsonUtil.parseJson2FHIRMsg(fhirMessageJSON); // get the data property

        //TODO we may need to check somewhere in the message to verify the base 64 encoding
        String extractedMessage = decodeBase64(encodedExtractedMessage.getV2MessageData());

        return extractedMessage;
    }

    private static JSONObject parse(String s) throws Exception {
        Object parsedString = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(s);
        if (parsedString instanceof JSONObject) {
            return (JSONObject) parsedString;
        } else {
            throw new Exception("Parsing did not return a JSON object");
        }
    }

    private static String decodeBase64(String stringToDecode) throws UnsupportedEncodingException {
        byte[] bytesToDecode = stringToDecode.getBytes(StandardCharsets.UTF_8);
        byte[] decodedBytes = Base64.getDecoder().decode(bytesToDecode);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        return decodedString;
    }
}
