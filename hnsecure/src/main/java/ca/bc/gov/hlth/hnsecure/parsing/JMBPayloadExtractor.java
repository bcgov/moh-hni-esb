package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
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
public class JMBPayloadExtractor implements Processor {

    private static Logger logger = LoggerFactory.getLogger(JMBPayloadExtractor.class);

  
	@Override
	public void process(Exchange exchange) throws Exception {
		final String methodName = LoggingUtil.getMethodName();
		
		Object response = exchange.getIn().getHeader("JMSReplyTo");	
        logger.info("{} - The jmb replymessage is  : {}", methodName, response);
        exchange.getIn().setBody(response);
	}    
}
