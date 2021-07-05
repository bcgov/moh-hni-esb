package ca.bc.gov.hlth.hnsecure.filedrops;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;

/**
 * This implementation logs each HL7v2 responsest message in a file in its
 * raw/text form. file name
 * format:{messageid}-{messagetype}-{facilityid}-{messagedate}-response.txt This
 * bean will be called onCompletion of main route. When we use onCompletion,
 * Camel spin-off a copy of the Exchange and routes it in a separate thread,
 * kinda like a Wire Tap.
 * 
 * @author anumeha.srivastava
 *
 */
public class ResponseFileDropGenerater extends FileDropGenerater {

	private static final Logger logger = LoggerFactory.getLogger(ResponseFileDropGenerater.class);
	public static final String RESPONSE_FILE = "response.txt";

	@Handler
	public void createFileDrops(Exchange exchange) {
		String fileName = buildFileNameParameters(exchange,exchange.getExchangeId());
		String responseFileName = fileName + RESPONSE_FILE;		
		writeFiledrop(exchange.getIn().getBody().toString(), responseFileName);
		logger.info("{} - TransactionId: {}, Successfully created file drops for response: {}",
				LoggingUtil.getMethodName(), exchange.getExchangeId(), responseFileName);
	}

}
