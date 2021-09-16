package ca.bc.gov.hlth.hnsecure.parsing;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.oauth2.sdk.util.StringUtils;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;

public class ProtocolEvaluator implements Processor {

	private static final Logger logger = LoggerFactory.getLogger(ProtocolEvaluator.class);
	
	private static final String PROTOCOL_PREFIX = ".protocol";
	
	private static final ApplicationProperties properties = ApplicationProperties.getInstance();
	
	@Override
	public void process(Exchange exchange) throws Exception {
		final String methodName = LoggingUtil.getMethodName();
		
		String messageType = (String)exchange.getProperty(Util.PROPERTY_MESSAGE_TYPE);
		
		// Dynamically look up the protocol from the properties
		String protocol = properties.getValue(messageType + PROTOCOL_PREFIX);
		
		exchange.setProperty(Util.PROPERTY_MESSAGE_PROTOCOL, protocol);

        logger.info("{} - Message Type: {} protocol {}", methodName, messageType, StringUtils.isNotBlank(protocol) ? protocol : "<not found>");
	}  

}
