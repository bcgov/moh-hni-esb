package ca.bc.gov.hlth.hnsecure.filedrops;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;

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
		String methodName = LoggingUtil.getMethodName();
		logger.debug("{} - Started... ", methodName);
		
		Object body = exchange.getIn().getBody();
		if (body != null && StringUtils.isNotBlank(body.toString())) {
			logger.info("{} - No v2 message was found in the request so it cannot be logged to file.", methodName);
			return;
		}
		
		/*
		 * when we use wiretap then the tapped exchange has its own exchange id
		 * But the wire tap will store the exchange id from its parent as a
		 * "correlated exchange id".
		 */
		String corId = exchange.getProperty(Exchange.CORRELATION_ID, String.class);
		String fileName = buildFileNameParameters(exchange,corId);
		String requestFileName = fileName + REQUEST_FILE;
		writeFiledrop(body.toString(), requestFileName);
		logger.info("{} - TransactionId: {}, Successfully created file drops for request: {}",methodName, exchange.getProperty(Exchange.CORRELATION_ID, String.class), requestFileName);

		logger.debug("{} - Finished.", methodName);
	}

}
