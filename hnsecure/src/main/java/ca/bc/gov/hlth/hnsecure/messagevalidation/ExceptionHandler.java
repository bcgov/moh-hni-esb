package ca.bc.gov.hlth.hnsecure.messagevalidation;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.conn.HttpHostConnectException;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.EventMessageProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.EventMessageErrorLevel;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.filedrops.FileDropGenerater;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.message.ErrorResponse;
import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;

/**
 * Custom ExceptionHandler to handle hn secure exceptions.
 *
 */
public class ExceptionHandler implements Processor {

	private static final Logger logger = LoggerFactory.getLogger(FileDropGenerater.class);
	
	private static final Boolean isAuditsEnabled = Boolean.valueOf(ApplicationProperties.getInstance().getValue(ApplicationProperty.IS_AUDITS_ENABLED));

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
				handleException(exchange, errorMessage, HttpStatus.BAD_REQUEST_400, TransactionEventType.INVALID);
				break;
			case CustomError_Msg_InvalidAuthKey:
			case CustomError_Msg_MissingAuthKey:
				handleException(exchange, errorMessage, HttpStatus.UNAUTHORIZED_401, TransactionEventType.UNAUTHENTICATED);
				break;
			default:
				handleException(exchange, ErrorMessage.HL7Error_Msg_Unknown, HttpStatus.BAD_REQUEST_400, TransactionEventType.ERROR);
				break;
			}
		} else if (exception instanceof HttpHostConnectException) {
			logger.info("{} - Failed to connect remote server. {}", LoggingUtil.getMethodName(), exception.getMessage());
			handleException(exchange, ErrorMessage.CustomError_Msg_DownstreamConnectionFailed,HttpStatus.INTERNAL_SERVER_ERROR_500, TransactionEventType.ERROR);			
		} else {
			// Should not reach here as the specific exception should be handled above, add default error in case the specific handling not added
			handleException(exchange, ErrorMessage.HL7Error_Msg_Unknown, HttpStatus.INTERNAL_SERVER_ERROR_500, TransactionEventType.ERROR);
		}
	}
	
	private void handleException(Exchange exchange, ErrorMessage errorMessage, Integer httpStatusCode, TransactionEventType eventType) {
		generateErrorResponse(exchange, errorMessage, httpStatusCode, eventType);
		// Write to Audit tables if enabled
		if (Boolean.TRUE.equals(isAuditsEnabled)) {
			writeEventMessageAudit(exchange, errorMessage, httpStatusCode, eventType);	
		}		
	}
	
	private void generateErrorResponse(Exchange exchange, ErrorMessage errorMessage, Integer httpStatusCode, TransactionEventType eventType) {
		HL7Message hl7Message = new HL7Message();
		hl7Message.setReceivingApplication(Util.RECEIVING_APP_HNSECURE);
		ErrorResponse errorResponse = new ErrorResponse();
		String v2Response = errorResponse.constructResponse(hl7Message, errorMessage);
		logger.info("{} - TransactionId: {}, FacilityId: {}, Error message is: {}", LoggingUtil.getMethodName(), exchange.getExchangeId(), hl7Message.getSendingFacility(), errorMessage);
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpStatusCode);
		exchange.getIn().setBody(v2Response);
	}
	
	private static void writeEventMessageAudit(Exchange exchange, ErrorMessage errorMessage, Integer httpStatusCode, TransactionEventType eventType) {
		EventMessageProcessor eventMessageProcessor = new EventMessageProcessor();
		if (TransactionEventType.UNAUTHENTICATED == eventType) {
			eventMessageProcessor.process(exchange, eventType, EventMessageErrorLevel.WARNING, httpStatusCode.toString(), HttpStatus.getMessage(httpStatusCode));
		} else {
			eventMessageProcessor.process(exchange, eventType, EventMessageErrorLevel.ERROR, errorMessage.getErrorSequence(), errorMessage.getErrorMessage());	
		}
			
	}
	
	

}
