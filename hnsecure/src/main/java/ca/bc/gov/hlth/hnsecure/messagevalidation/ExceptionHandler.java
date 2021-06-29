package ca.bc.gov.hlth.hnsecure.messagevalidation;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpStatus;
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
			switch (((CustomHNSException)exception).getErrorMessage()) {
			case HL7Error_Msg_NoInputHL7:
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_UNPROCESSABLE_ENTITY);
				break;
			case CustomError_Msg_InvalidRequest:
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_BAD_REQUEST);
				break;
			case CustomError_Msg_InvalidAuthKey:
			case CustomError_Msg_MissingAuthKey:
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_UNAUTHORIZED);
				break;
			default:
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_BAD_REQUEST);
				break;
			}
		} else if (exception instanceof HttpHostConnectException) {
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
			LOGGER.info("{} - Failed to connect remote server. {}", LoggingUtil.getMethodName(), exception.getMessage());
		} else {
			// Should not reach here as the specific exception should be handled above, add default error in case the specific handling not added
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
		
		//Set the body to null as none is expected.
        exchange.getIn().setBody(null);
	}
}
