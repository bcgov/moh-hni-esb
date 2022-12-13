package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_IS_AUDITS_ENABLED;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_IS_FILE_DROPS_ENABLED;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;

/**
 * This processor is created to prevent hnsesb to generate request file drop and auditing  
 * for the receiving app HNETDTTN
 */
public class DateTimeRequestProcessor implements Processor {
	private static final Logger logger = LoggerFactory.getLogger(DateTimeRequestProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {	
		String methodName = LoggingUtil.getMethodName();
		String v2Message = (String) exchange.getIn().getBody();
		
		Map<String, Object> exchangeProperties = exchange.getProperties();
			
		if(V2MessageUtil.getReceivingApp(v2Message).contentEquals(Util.HNETDTTN)) {
			exchangeProperties.put(PROPERTY_IS_FILE_DROPS_ENABLED, Boolean.FALSE);
			exchangeProperties.put(PROPERTY_IS_AUDITS_ENABLED, Boolean.FALSE);
			
			logger.debug("{} - TransactionId: {}, {}", methodName, exchange.getExchangeId(), exchange.getIn().getBody().toString());
		}				
		
	}

}
