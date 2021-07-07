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
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.message.ErrorResponse;
import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.parsing.Util;

/**
 * Custom ExceptionHandler to handle hn secure exceptions.
 *
 */
public class ExceptionHandler implements Processor {

	private static final Logger logger = LoggerFactory.getLogger(FileDropGenerater.class);

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
			ErrorMessage errorMessage = ((CustomHNSException)exception).getErrorMessage(); 
			switch (errorMessage) {
			case HL7Error_Msg_NoInputHL7:
			case CustomError_Msg_InvalidRequest:
				generateErrorResponse(exchange, errorMessage, HttpStatus.SC_BAD_REQUEST);
				break;
			case CustomError_Msg_InvalidAuthKey:
			case CustomError_Msg_MissingAuthKey:
				generateErrorResponse(exchange, errorMessage, HttpStatus.SC_UNAUTHORIZED);
				break;
			default:
				generateErrorResponse(exchange, ErrorMessage.HL7Error_Msg_Unknown, HttpStatus.SC_BAD_REQUEST);
				break;
			}
		} else if (exception instanceof HttpHostConnectException) {
			logger.info("{} - Failed to connect remote server. {}", LoggingUtil.getMethodName(), exception.getMessage());
			generateErrorResponse(exchange, ErrorMessage.CustomError_Msg_DownstreamConnectionFailed, HttpStatus.SC_INTERNAL_SERVER_ERROR);			
		} else {
			// Should not reach here as the specific exception should be handled above, add default error in case the specific handling not added
			generateErrorResponse(exchange, ErrorMessage.HL7Error_Msg_Unknown, HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	private void generateErrorResponse(Exchange exchange, ErrorMessage errorMessage, int httpStatusCode) {
		HL7Message hl7Message = new HL7Message();
		hl7Message.setReceivingApplication(Util.RECEIVING_APP_HNSECURE);
		ErrorResponse errorResponse = new ErrorResponse();
		String v2Response = errorResponse.constructResponse(hl7Message, errorMessage);
		logger.info("{} - TransactionId: {}, FacilityId: {}, Error message is: {}", LoggingUtil.getMethodName(), exchange.getExchangeId(), hl7Message.getSendingFacility(), errorMessage);
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpStatusCode);
		exchange.getIn().setBody(v2Response);
	}
}
