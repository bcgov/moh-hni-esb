package ca.bc.gov.hlth.hnsecure.validation;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PROCESSING_DOMAIN;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.VALID_RECIEVING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.VERSION;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.EventMessageProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.EventMessageErrorLevel;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.exception.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.message.ErrorResponse;
import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.message.MessageUtil;
import ca.bc.gov.hlth.hnsecure.message.PharmanetErrorResponse;
import ca.bc.gov.hlth.hnsecure.message.ResponseSegment;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * This validator validates the payload i.e. HL7 V2 message It performs different types of validations on the message. 
 * For instance vlaidation message format, valdating contents of the message etc If the validation fails, it generates ValidationFailedException
 * This class is using decorator pattern. So validate method also makes a call to validate method of wrapped class passed in constructor.    
 *
 */
public class PayLoadValidator extends AbstractValidator {
	private static final Logger logger = LoggerFactory.getLogger(PayLoadValidator.class);
	private static final String SEGMENT_IDENTIFIER = "MSH";

	private static final ApplicationProperties properties = ApplicationProperties.getInstance();
	
	private static final Boolean IS_AUDIT_ENABLED = Boolean.valueOf(properties.getValue(ApplicationProperty.IS_AUDITS_ENABLED));
	
	private Validator validator;
	
	public PayLoadValidator(Validator validator) {
		super();
		this.validator = validator;
	}

	@Override
	public boolean validate(Exchange exchange) throws ValidationFailedException, CustomHNSException {
		String methodName = LoggingUtil.getMethodName();
		logger.info("{} - TransactionId: {}, PayLoadValidator Validation started", methodName, exchange.getExchangeId());
		HL7Message messageObj = new HL7Message();
		String accessToken = (String) exchange.getIn().getHeader(AUTHORIZATION); 
		// Validate v2Message format
		String v2Message = (String) exchange.getIn().getBody();
		String transactionId = exchange.getExchangeId();
		validateMessageFormat(exchange, v2Message, messageObj);
		boolean isPharmanetMode = isPharmanet(v2Message,messageObj, transactionId);
		validateSendingFacility(exchange, messageObj, accessToken, isPharmanetMode);
		validateReceivingApp(exchange, messageObj);
		validateReceivingFacility(exchange, messageObj);
		validatePharmanetMessageFormat(exchange, v2Message, messageObj, isPharmanetMode);
		// To ensure validation in wrapper class is called.
		validator.validate(exchange);
		logger.info("{} - TransactionId: {}, PayLoadValidator Validation completed", methodName, exchange.getExchangeId());
		return true;
	}

	
	/**
	 * Validates the format of Pharmanet message
	 * checks if zcb segment is present
	 * @param exchange
	 * @param v2Message
	 * @param messageObj
	 * @param isPharmanetMode
	 * @throws ValidationFailedException
	 */
	protected  void validatePharmanetMessageFormat(Exchange exchange, String v2Message, HL7Message messageObj,
			boolean isPharmanetMode) throws ValidationFailedException {	
		if (isPharmanetMode && !V2MessageUtil.isSegmentPresent(v2Message, Util.ZCB_SEGMENT)) {
			populateFieldsForErrorResponse(messageObj);
			logger.error("{} - TransactionId: {}, couldn't find ZCB segment", LoggingUtil.getMethodName(), exchange.getExchangeId());
			generatePharmanetError(messageObj, ErrorMessage.HL7_ERROR_TRANSACTION_FORMAT_ERROR, exchange);
		}		
	}

	/**
	 * Validates the receiving facility for non-pharmanet messages.
	 * Message type must be 'ZPN' for Pharmanet messages
	 * @param exchange
	 * @param messageObj
	 * @throws ValidationFailedException
	 */
	protected  void validateReceivingFacility(Exchange exchange, HL7Message messageObj)
			throws ValidationFailedException {
		if (StringUtils.isEmpty(messageObj.getReceivingFacility())) {
			generateError(messageObj, ErrorMessage.HL7_ERROR_MISSING_RECEIVING_FACILITY, exchange);
		} else if (!messageObj.getReceivingApplication().equalsIgnoreCase(Util.RECEIVING_APP_PNP)) {
			Set<String> validReceivingFacility = Util.getPropertyAsSet(properties.getValue(VALID_RECIEVING_FACILITY));
			if (validReceivingFacility.stream().noneMatch(messageObj.getReceivingFacility()::equalsIgnoreCase)) {
				logger.error("{} - TransactionId: {}, Facility Not Found '{}, {}'", LoggingUtil.getMethodName(), exchange.getExchangeId(), messageObj.getReceivingFacility(), messageObj.getProcessingId());
				generateError(messageObj, ErrorMessage.HL7_ERROR_ENCRYPTION_ERROR, exchange);
			}
		} else if (messageObj.getReceivingApplication().equalsIgnoreCase(Util.RECEIVING_APP_PNP)
					&& (!messageObj.getMessageType().equalsIgnoreCase(Util.MESSAGE_TYPE_PNP))) {
			logger.error("{} - TransactionId: {}, Receiving application is PNP for non-ZPN Message Type: {}", LoggingUtil.getMethodName(), exchange.getExchangeId(), messageObj.getMessageType());
			generatePharmanetError(messageObj, ErrorMessage.HL7_ERROR_ENCRYPTION_ERROR, exchange);
		}
	}
	

	/**
	 * Validates receiving application
	 * @param exchange
	 * @param messageObj
	 * @throws ValidationFailedException
	 */
	protected  void validateReceivingApp(Exchange exchange, HL7Message messageObj)
			throws ValidationFailedException {
		if (StringUtils.isEmpty(messageObj.getReceivingApplication())) {
			generateError(messageObj, ErrorMessage.HL7_ERROR_INVALID_FORMAT, exchange);
		}
		// Check the validity
		else if (!MessageUtil.getMtypecollection().containsValue(messageObj.getReceivingApplication())) {
			generateError(messageObj, ErrorMessage.HL7_ERROR_UNKNOWN_RECEIVING_APPLICATION, exchange);
		}
	}

	/**
	 * This method checks if the sending facility provided in message is same as the facility in access token
	 * If sending facility is not provided in the message, retrieve the facility from access token
	 * Validation fails if the sending facility is not same as the facility in access token
	 * @param exchange
	 * @param messageObj
	 * @param accessToken
	 * @param isPharmanetMode
	 * @throws ValidationFailedException
	 */
	protected  void validateSendingFacility(Exchange exchange, HL7Message messageObj, String accessToken,
			boolean isPharmanetMode) throws ValidationFailedException {
		// Validate Sending facility	
		String facilityNameFromAccessToken = getSendingFacility(accessToken);
		if (StringUtils.isEmpty(messageObj.getSendingFacility())) {
			messageObj.setSendingFacility(facilityNameFromAccessToken);
		} 
		else if(!messageObj.getSendingFacility().equalsIgnoreCase(facilityNameFromAccessToken)) {
			messageObj.setClientFacilityId(facilityNameFromAccessToken);
			if(isPharmanetMode) {
				generatePharmanetError(messageObj, ErrorMessage.HL7_ERROR_FACILITY_ID_MISMATCH, exchange);
			}else {
				generateError(messageObj, ErrorMessage.HL7_ERROR_FACILITY_ID_MISMATCH, exchange);
			}
		}
		else {
			//do nothing
		}
	}

	/**
	 * This method checks the format of incoming message
	 * Sets the messageType for filedrop
	 * @param exchange
	 * @param v2Message
	 * @param messageObj
	 * @throws ValidationFailedException
	 */
	protected  void validateMessageFormat(Exchange exchange, String v2Message, HL7Message messageObj)
			throws ValidationFailedException {
		if (!StringUtils.isEmpty(v2Message)) {
			String[] v2DataLines = v2Message.split("\n");
			String[] v2Segments = v2DataLines[0].split(Util.DOUBLE_BACKSLASH + Util.HL7_DELIMITER,-1);
			
			//This is just a workaround. The MessageType must be set regardless of any validation error
			//as its being used to build filedrop name.Ideally it should be set only in PopulateReqHeader but its not called 
			// for validation failure case.
			if (Arrays.stream(v2Segments).allMatch(Objects::nonNull) && v2Segments.length > 8) {				
				String msgType = V2MessageUtil.getMsgType(v2Message);
				exchange.getProperties().put(PROPERTY_MESSAGE_TYPE, msgType);
			}
			if (Arrays.stream(v2Segments).allMatch(Objects::nonNull) && v2Segments.length >= 12) {
				ResponseSegment.initSegment(v2Segments, messageObj);				
			} else {
				generateError(messageObj, ErrorMessage.HL7_ERROR_INVALID_FORMAT, exchange);
			}
		} else {
			generateError(messageObj, ErrorMessage.HL7_ERROR_NO_INPUT, exchange);
		}

		// Validate segment identifier
		if (!messageObj.getSegmentIdentifier().equals(SEGMENT_IDENTIFIER)) {
			generateError(messageObj, ErrorMessage.HL7_ERROR_MSH_SEGMENT_MISSING, exchange);
		}

		// Validate encoding characters
		if (StringUtils.isEmpty(messageObj.getEncodingCharacter())
				|| messageObj.getEncodingCharacter().toCharArray().length != 4) {
			generateError(messageObj, ErrorMessage.HL7_ERROR_INVALID_FORMAT, exchange);
		} else if (!sameChars(messageObj.getEncodingCharacter(), Util.ENCODING_CHARACTERS)) {
			generateError(messageObj, ErrorMessage.HL7_ERROR_INVALID_MSH_SEGMENT, exchange);
		}
	}
	
	/**
	 * This method checks if it is a Pharmanet message
	 * @param messageObj
	 * @return
	 */
	protected static boolean isPharmanet(String v2Message, HL7Message messageObj, String transactionId) {
		if ((!StringUtils.isEmpty(messageObj.getMessageType())
				&& (messageObj.getMessageType()).equals(Util.MESSAGE_TYPE_PNP))) {
			String methodName = LoggingUtil.getMethodName();		
			String zcbSegment = V2MessageUtil.getDataSegment(v2Message,Util.ZCB_SEGMENT);
			String pharmacyID = V2MessageUtil.getPharmacyId(zcbSegment);
			String traceNumber = V2MessageUtil.getTraceNumber(zcbSegment);
			logger.debug("{} - TransactionId: {}, FacilityId: {}, PharmacyId: {}, TraceNumber: {}",
					methodName, transactionId, messageObj.getSendingFacility(), pharmacyID, traceNumber);
			return true;
		}
		return false;
	}

	/**
	 * Populate optional field from properties file if present
	 * @param messageObj
	 */
	private void populateFieldsForErrorResponse(HL7Message messageObj) {

		if (StringUtils.isEmpty(messageObj.getDateTime())) {
			messageObj.setDateTime(Util.getGenericDateTime());
		}

		if (StringUtils.isEmpty(messageObj.getVersionId())) {
			messageObj.setVersionId(properties.getValue(VERSION));
		}

		if (StringUtils.isEmpty(messageObj.getProcessingId())) {
			messageObj.setProcessingId(properties.getValue(PROCESSING_DOMAIN));
		}
	}

	/**
	 * @param messageObject
	 * @param errorMessage
	 * @param exchange
	 * @throws ValidationFailedException
	 */
	private static void generateError(HL7Message messageObject, ErrorMessage errorMessage, Exchange exchange)
			throws ValidationFailedException {
		int httpStatusCode = HttpStatus.SC_BAD_REQUEST;
		String errorText = errorMessage.formatErrorMessage(messageObject);
		
		ErrorResponse errorResponse = new ErrorResponse();		
		String v2Response = errorResponse.constructResponse(messageObject, errorMessage);
		logger.error("{} - TransactionId: {}, FacilityId: {}. Error message is: {} {}", LoggingUtil.getMethodName(), exchange.getExchangeId(), messageObject.getSendingFacility(), errorMessage.getErrorSequence(), errorText);
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpStatusCode);
		exchange.getIn().setBody(v2Response);				
		// Write to Audit tables in enabled
		if (Boolean.TRUE.equals(IS_AUDIT_ENABLED)) {			
			writeEventMessageAudit(exchange, errorMessage.getErrorSequence(), errorText);
		}
		throw new ValidationFailedException(errorMessage);
	}

	/**
	 * @param messageObject
	 * @param errorMessage
	 * @param exchange
	 * @throws ValidationFailedException
	 */
	private static void generatePharmanetError(HL7Message messageObject, ErrorMessage errorMessage, Exchange exchange)
			throws ValidationFailedException {
		int httpStatusCode = HttpStatus.SC_BAD_REQUEST;
		String errorText = errorMessage.formatErrorMessage(messageObject);
		
		PharmanetErrorResponse errorResponse = new PharmanetErrorResponse();
		String v2Response = errorResponse.constructResponse(messageObject, errorMessage);
		logger.error("{} - TransactionId: {}, FacilityId: {}. Error message is: {} {}", LoggingUtil.getMethodName(), exchange.getExchangeId(),messageObject.getSendingFacility(), errorMessage.getErrorSequence(), errorText);
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpStatusCode);
		exchange.getIn().setBody(v2Response);				
		// Write to Audit tables in enabled
		if (Boolean.TRUE.equals(IS_AUDIT_ENABLED)) {
			writeEventMessageAudit(exchange, errorMessage.getErrorSequence(), errorText);
		}
		throw new ValidationFailedException(errorMessage);
	}
	
	private static void writeEventMessageAudit(Exchange exchange, String errorSequence, String errorMessage) {
		EventMessageProcessor eventMessageProcessor = new EventMessageProcessor();
		eventMessageProcessor.process(exchange, TransactionEventType.INVALID, EventMessageErrorLevel.REJECT, errorSequence, errorMessage);	
	}
	

	/*
	 * The FacilityId is the legacy way to track connected clients and is now set as
	 * the ClientId of the client application In the access token this is the 'azp'
	 * field
	 */
	private static String getSendingFacility(String auth) {
		String clientId = "";
		if (!StringUtils.isEmpty(auth)) {
			String[] split = auth.split("\\.");
			String decodeAuth = Util.decodeBase64(split[1]);
			try {
				JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
				JSONObject jsonObject = (JSONObject) jsonParser.parse(decodeAuth);
				clientId = (String) jsonObject.get("azp");
			} catch (net.minidev.json.parser.ParseException e) {
				logger.error(e.getMessage());
			}
		}
		return clientId;
	}


	private static boolean sameChars(String firstStr, String secondStr) {
		char[] first = firstStr.toCharArray();
		char[] second = secondStr.toCharArray();
		Arrays.sort(first);
		Arrays.sort(second);
		return Arrays.equals(first, second);
	}
	
}
