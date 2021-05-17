package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.json.pharmanet.PharmaNetJsonMessage;
import ca.bc.gov.hlth.hnsecure.json.pharmanet.PharmaNetJsonUtil;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * Camel Processor to process the extraction of the v2 message from the JSON returned by PharmaNet 
 * 
 * @author dave.p.barrett
 *
 */
public class PharmaNetPayloadExtractor implements Processor {

    private static Logger logger = LoggerFactory.getLogger(PharmaNetPayloadExtractor.class);
    
    private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

    /**
     * Processes the message exchange by extracting the v2 message from the JSON returned by PharmaNet 
     * 
     * @param exchange the message exchange
     * @throws Exception if an internal processing error has occurred.
     */
	@Override
	public void process(Exchange exchange) throws Exception {
		final String methodName = Util.getMethodName();
		
		Object body = exchange.getIn().getBody(String.class);
		
        JSONObject pharmaNetJSON = (JSONObject) jsonParser.parse(body.toString());
        
        PharmaNetJsonMessage encodedExtractedMessage = PharmaNetJsonUtil.parseJsonToPharmanetMsg(pharmaNetJSON); 

        String extractedMessage = Util.decodeBase64(encodedExtractedMessage.getHl7Message());
        logger.info("{} - The decoded PharmaNet HL7 message response is: {}", methodName, extractedMessage);
        exchange.getIn().setBody(extractedMessage);
	}    
}
