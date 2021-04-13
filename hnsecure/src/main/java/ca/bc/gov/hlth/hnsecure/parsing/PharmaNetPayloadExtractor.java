package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnscommon.json.PharmaNetJsonMessage;
import ca.bc.gov.hlth.hnscommon.json.PharmaNetJsonUtil;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class PharmaNetPayloadExtractor implements Processor {

    private static Logger logger = LoggerFactory.getLogger(PharmaNetPayloadExtractor.class);
    
    private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

	@Override
	public void process(Exchange exchange) throws Exception {
		Object body = exchange.getIn().getBody(String.class);
		
        JSONObject pharmaNetJSON = (JSONObject) jsonParser.parse(body.toString());
        
        PharmaNetJsonMessage encodedExtractedMessage = PharmaNetJsonUtil.parseJsonToPharmanetMsg(pharmaNetJSON); 

        String extractedMessage = Util.decodeBase64(encodedExtractedMessage.getHl7Message());
        logger.debug("The decoded HL7 message is:"+extractedMessage);
        exchange.getIn().setBody(extractedMessage);
	}    
}
