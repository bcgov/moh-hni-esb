package ca.bc.gov.hlth.hnsecure.filedrops;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.parsing.Util;

/**
 * This implementation logs each HL7v2 request message in a file in its
 * raw/text form. file name
 * format:{messageid}-{messagetype}-{facilityid}-{messagedate}-request.txt
 * This bean will be called in direct route as wiretap
 * @author anumeha.srivastava
 *
 */
public class RequestFileDropGenerater extends FileDropGenerater {

	private static final Logger logger = LoggerFactory.getLogger(RequestFileDropGenerater.class);
	private static final String REQUEST_FILE = "request.txt";
	

	@Handler
	public void createFileDrops(Exchange exchange) {
		String fileName = buildFileNameParameters(exchange);
		String requestFileName = fileName + REQUEST_FILE;
		writeFiledrop(exchange.getIn().getBody().toString(), requestFileName);
		logger.info("{} - TransactionId: {}, Successfully created file drops for request: {}",LoggingUtil.getMethodName(), exchange.getProperty(Exchange.CORRELATION_ID, String.class), requestFileName);
	}



}
