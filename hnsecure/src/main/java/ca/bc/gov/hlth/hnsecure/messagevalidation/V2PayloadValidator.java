package ca.bc.gov.hlth.hnsecure.messagevalidation;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PROCESSING_DOMAIN;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.VALID_RECIEVING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.VERSION;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.exception.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.message.ErrorResponse;
import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.message.MessageUtil;
import ca.bc.gov.hlth.hnsecure.message.PharmanetErrorResponse;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class V2PayloadValidator {

	private static final Logger logger = LoggerFactory.getLogger(V2PayloadValidator.class);
	private static final String expectedEncodingChar = "^~\\&";
	private static final String segmentIdentifier = "MSH";
	private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
	private ApplicationProperties properties = ApplicationProperties.getInstance();
	
	/**
	 *This method does generic validation and Pharmanet specific validation
	 * @param v2Message the hl7v2 message to validate
	 * @throws ValidationFailedException if a validation step fails
	 */
	@Handler
	public void validate(Exchange exchange, String v2Message) throws ValidationFailedException {

		HL7Message messageObj = new HL7Message();
		String accessToken = (String) exchange.getIn().getHeader(AUTHORIZATION);
		// Validate v2Message format
		validateMessageFormat(exchange, v2Message, messageObj);
		boolean isPharmanetMode = isPharmanet(messageObj);
		validateSendingFacility(exchange, messageObj, accessToken, isPharmanetMode);
		validateReceivingApp(exchange, messageObj);
		validateReceivingFacility(exchange, messageObj);
		// TODO  ADDRESSED Do we need to populate fields in validator? Should this be done after validation is complete?
		// This call was required to populate fields for error response message. Updated method name to "populateFieldsForErrorResponse" and moved call to method that needs these fields  
		validatePharmanetMessageFormat(exchange, v2Message, messageObj, isPharmanetMode);
		// Moved this call to PopulateHeader class to keep it all together
		//exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);

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
		if (isPharmanetMode) {
			if (!Util.isSegmentPresent(v2Message, Util.ZCB_SEGMENT)) {
				populateFieldsForErrorResponse(messageObj);
				generatePharmanetError(messageObj, ErrorMessage.HL7Error_Msg_TransactionFromatError, exchange);
			}
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
		if (!messageObj.getReceivingApplication().equalsIgnoreCase(Util.RECEIVING_APP_PNP)) {
			Set<String> validReceivingFacility = Util.getPropertyAsSet(properties.getValue(VALID_RECIEVING_FACILITY));
			if (validReceivingFacility.stream().noneMatch(messageObj.getReceivingFacility()::equalsIgnoreCase)) {
				generateError(messageObj, ErrorMessage.HL7Error_Msg_EncryptionError, exchange);

			}
		}else if (messageObj.getReceivingApplication().equalsIgnoreCase(Util.RECEIVING_APP_PNP)
					&& (!messageObj.getMessageType().equalsIgnoreCase(Util.MESSAGE_TYPE_PNP))) {
				generatePharmanetError(messageObj, ErrorMessage.HL7Error_Msg_EncryptionError, exchange);
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
		if ((StringUtils.isEmpty(messageObj.getReceivingApplication())
				|| StringUtils.isEmpty(messageObj.getReceivingFacility()))) {

			generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidHL7Format, exchange);
		}

		// Check the validity
		if (!MessageUtil.mTypeCollection.containsValue(messageObj.getReceivingApplication())) {
			generateError(messageObj, ErrorMessage.HL7Error_Msg_UnknownReceivingApplication, exchange);
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
			if(isPharmanetMode) {
				generatePharmanetError(messageObj, ErrorMessage.HL7Error_Msg_FacilityIDMismatch, exchange);
			}else {
				generateError(messageObj, ErrorMessage.HL7Error_Msg_FacilityIDMismatch, exchange);
			}
		}
		else {
			//do nothing
		}
	}

	/**
	 * This method checks the format of incoming message
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
			if (Arrays.stream(v2Segments).allMatch(Objects::nonNull) && v2Segments.length >= 12) {
				ErrorResponse.initSegment(v2Segments, messageObj);
			} else {
				generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidHL7Format, exchange);
			}
		} else {
			generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidHL7Format, exchange);
		}

		// Validate segment identifier
		if (!messageObj.getSegmentIdentifier().equals(segmentIdentifier)) {
			generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidHL7Format, exchange);
		}

		// Validate encoding characters
		if (StringUtils.isEmpty(messageObj.getEncodingCharacter())
				|| messageObj.getEncodingCharacter().toCharArray().length != 4) {
			generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidHL7Format, exchange);
		} else if (!sameChars(messageObj.getEncodingCharacter(), expectedEncodingChar)) {
			generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidMSHSegment, exchange);
		}
	}
	
	/**
	 * This method checks if it is a Pharmanet message
	 * @param messageObj
	 * @return
	 */
	protected static boolean isPharmanet(HL7Message messageObj) {
		if ((!StringUtils.isEmpty(messageObj.getMessageType())
				&& (messageObj.getMessageType()).equals(Util.MESSAGE_TYPE_PNP))) {
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
		String methodName = "generateError";
		messageObject.setReceivingApplication(Util.RECEIVING_APP_HNSECURE);
		ErrorResponse errorResponse = new ErrorResponse();		
		String v2Response = errorResponse.constructResponse(messageObject, errorMessage);
		logger.info("{} - TransactionId: {}, FacilityId: {}, Error message is: {}",methodName, exchange.getIn().getMessageId(),messageObject.getSendingFacility(), errorMessage);
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		exchange.getIn().setBody(v2Response);
		throw new ValidationFailedException(errorMessage.getErrorMessage());
	}

	/**
	 * @param messageObject
	 * @param errorMessage
	 * @param exchange
	 * @throws ValidationFailedException
	 */
	private static void generatePharmanetError(HL7Message messageObject, ErrorMessage errorMessage, Exchange exchange)
			throws ValidationFailedException {
		String methodName = "generatePharmanetError";
		messageObject.setReceivingApplication(Util.RECEIVING_APP_HNSECURE);
		PharmanetErrorResponse errorResponse = new PharmanetErrorResponse();
		String v2Response = errorResponse.constructResponse(messageObject, errorMessage);
		logger.info("{} - TransactionId: {}, FacilityId: {}, Error message is: {}",methodName, exchange.getIn().getMessageId(),messageObject.getSendingFacility(), errorMessage);
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		exchange.getIn().setBody(v2Response);
		throw new ValidationFailedException(errorMessage.getErrorMessage());
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
				JSONObject jsonObject = (JSONObject) jsonParser.parse(decodeAuth);
				clientId = (String) jsonObject.get("azp");
			} catch (net.minidev.json.parser.ParseException e) {
				logger.error(e.getMessage());
			}
		}
		return clientId;
	}

	private static Boolean validateReceivingApplication(String receivingApp) {
		return MessageUtil.mTypeCollection.containsValue(receivingApp);
		
	}
	
	private static boolean sameChars(String firstStr, String secondStr) {
		char[] first = firstStr.toCharArray();
		char[] second = secondStr.toCharArray();
		Arrays.sort(first);
		Arrays.sort(second);
		return Arrays.equals(first, second);
	}

}
