package ca.bc.gov.hlth.hnsecure;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FhirPayloadExtractor {

    private static Logger logger = LoggerFactory.getLogger(FhirPayloadExtractor.class);
    private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

    @Handler
    public static String extractFhirPayload(String fhirMessage) throws Exception {

        //TODO - Investigate other JSON libraries. This JSON library came as a Nimbus dependency but it's a little cumbersome to get a nested property
        JSONObject fhirMessageJSON = (JSONObject) jsonParser.parse(fhirMessage);

        JSONArray entryProperties = (JSONArray) jsonParser.parse(fhirMessageJSON.getAsString("entry")); // Get the "entry" property json array

        JSONObject entryPropertyContainingPayload = (JSONObject) entryProperties.get(1); // Get the second json object from the array

        JSONObject resourceProperty = (JSONObject) jsonParser.parse(entryPropertyContainingPayload.getAsString("resource")); // get the "resource" property

        String encodedExtractedMessage = resourceProperty.getAsString("data"); // get the data property

        //TODO we may need to check somewhere in the message to verify the base 64 encoding
        String extractedMessage = decodeBase64(encodedExtractedMessage);

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
