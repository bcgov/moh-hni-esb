package ca.bc.gov.hlth.hnsecure.messagevalidation;

import java.net.UnknownHostException;

import javax.jms.JMSException;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.Processor;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.http.conn.HttpHostConnectException;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.UncategorizedJmsException;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.EventMessageProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.EventMessageErrorLevel;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.message.ErrorResponse;
import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.message.ResponseSegment;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;

/**
 * Custom ExceptionHandler to handle hn secure exceptions.
 *
 */
public class ExceptionHandler implements Processor {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);
	
	private static final Boolean IS_AUDITS_ENABLED = Boolean.valueOf(ApplicationProperties.getInstance().getValue(ApplicationProperty.IS_AUDITS_ENABLED));

    /**
     * Handle the exception thrown from the route. Based on the exception content it will add an
     * appropriate HTTP Status Code.
     * 
     * @param exchange the exchange that is being processed
     */    
	@Override
	public void process(Exchange exchange) {

		Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
		String methodName = LoggingUtil.getMethodName();
		
		if (exception instanceof CustomHNSException) {
			ErrorMessage errorMessage = ((CustomHNSException)exception).getErrorMessage(); 
			switch (errorMessage) {
			case HL7_ERROR_NO_INPUT:
			case CUSTOM_ERROR_INVALID_REQUEST:
				handleException(exchange, errorMessage, HttpStatus.BAD_REQUEST_400, TransactionEventType.INVALID);
				break;
			case CUSTOM_ERROR_INVALID_AUTH_KEY:
			case CUSTOM_ERROR_MISSING_AUTH_KEY:
				handleException(exchange, errorMessage, HttpStatus.UNAUTHORIZED_401, TransactionEventType.UNAUTHENTICATED);
				break;
			case CUSTOM_ERROR_MQ_NOT_ENABLED:
				handleException(exchange, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR_500, TransactionEventType.ERROR);
				break;
			default:
				handleException(exchange, ErrorMessage.HL7_ERROR_UNKNOWN, HttpStatus.BAD_REQUEST_400, TransactionEventType.ERROR);
				break;
			}
		} else if (exception instanceof HttpHostConnectException || exception instanceof HttpOperationFailedException || exception instanceof UnknownHostException) {
			logger.info("{} -  Failed to connect remote server. {}", methodName, exception.getMessage());
			handleException(exchange, ErrorMessage.CUSTOM_ERROR_DOWN_STREAM_CONNECTION_FAILED,HttpStatus.INTERNAL_SERVER_ERROR_500, TransactionEventType.ERROR);			
		} else if (exception instanceof JMSException) {
			JMSException jmse = (JMSException)exception;
			logger.info("{} - JMS Error Code: {} JMS Explanation: {}", methodName, jmse.getErrorCode(), jmse.getLinkedException().getMessage());
			handleException(exchange, ErrorMessage.HL7_ERROR_MQ_TIMEOUT, HttpStatus.INTERNAL_SERVER_ERROR_500, TransactionEventType.ERROR);			
		} else if (exception instanceof UncategorizedJmsException) {
			UncategorizedJmsException jmse = (UncategorizedJmsException)exception;			
			logger.info("{} - JMS Error Code: {} JMS Explanation: {}", methodName, jmse.getErrorCode(), jmse.getMessage());
			handleException(exchange, ErrorMessage.HL7_ERROR_MQ_FAILED_TO_CONNECT_QM, HttpStatus.INTERNAL_SERVER_ERROR_500, TransactionEventType.ERROR);
		} else if (exception instanceof ExchangeTimedOutException) {
			logger.info("{} - MQSeries failure. {}", methodName, exception.getMessage());
			handleException(exchange, ErrorMessage.HL7_ERROR_MQ_SERIES_FAILURE,HttpStatus.INTERNAL_SERVER_ERROR_500, TransactionEventType.ERROR);			
		} else {
			// Should not reach here as the specific exception should be handled above, add default error in case the specific handling not added
			handleException(exchange, ErrorMessage.HL7_ERROR_UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR_500, TransactionEventType.ERROR);
		}
	}
	
	private void handleException(Exchange exchange, ErrorMessage errorMessage, Integer httpStatusCode, TransactionEventType eventType) {
		generateErrorResponse(exchange, errorMessage, httpStatusCode);
		// Write to Audit tables if enabled
		if (Boolean.TRUE.equals(IS_AUDITS_ENABLED)) {
			writeEventMessageAudit(exchange, errorMessage, httpStatusCode, eventType);	
		}		
	}
	
	private void generateErrorResponse(Exchange exchange, ErrorMessage errorMessage, Integer httpStatusCode) {		
		// Initialize the incoming hl7Message used to construct the response
		HL7Message hl7Message = new HL7Message();
		String v2 = (String)exchange.getIn().getBody();
		String[] v2Segments = V2MessageUtil.getMshSegmentFields(v2);
		// Since this can be invoked under multiple scenarios it is possible
		// for the segments to be null
		if (v2Segments != null) {
			ResponseSegment.initSegment(v2Segments, hl7Message);
		}

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
			eventMessageProcessor.process(exchange, eventType, EventMessageErrorLevel.ERROR, errorMessage.getErrorSequence(), errorMessage.getMessage());	
		}
	}

}
