package ca.bc.gov.hlth.hnsecure.messagevalidation;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CustomError_Msg_InvalidAuthKey;
import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CustomError_Msg_InvalidRequest;
import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.HL7Error_Msg_NoInputHL7;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.filedrops.FileDropGenerater;

/**
 * Custom ExceptionHandler to handle hn secure exceptions.
 *
 */
public class ExceptionHandler implements Processor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileDropGenerater.class);

    /**
     * Handle the exception thrown from the route. Based on the exception content it will add an
     * appropriate HTTP Status Code.
     * 
     * @param exchange the exchange that is being processed
     */    
	@Override
	public void process(Exchange exchange) {

		Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
		
		if (exception instanceof CustomHNSException) {
			if (CustomError_Msg_InvalidAuthKey.getErrorMessage().equals(exception.getMessage())) {
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, SC_FORBIDDEN);
			} else if (CustomError_Msg_InvalidRequest.getErrorMessage().equals(exception.getMessage())){
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, SC_BAD_REQUEST);
			} else if (HL7Error_Msg_NoInputHL7.getErrorMessage().equals(exception.getMessage())){
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, SC_UNPROCESSABLE_ENTITY);
			} else {
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, SC_BAD_REQUEST);
			}
		} else if (exception instanceof HttpHostConnectException) {
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, SC_INTERNAL_SERVER_ERROR);
			LOGGER.info("{} - Failed to connect remote server.",LoggingUtil.getMethodName());
		} else {
			// Should not reach here as the specific exception should be handled above, add default error in case the specific handling not added
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, SC_INTERNAL_SERVER_ERROR);
		}
		
		//Set the body to null as none is expected.
        exchange.getIn().setBody(null);
	}
}
