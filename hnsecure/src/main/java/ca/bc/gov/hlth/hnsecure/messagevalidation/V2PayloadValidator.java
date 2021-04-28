package ca.bc.gov.hlth.hnsecure.messagevalidation;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.authorization.AuthorizationProperties;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.message.ErrorResponse;
import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.message.MessageUtil;
import ca.bc.gov.hlth.hnsecure.message.PharmanetErrorResponse;
import ca.bc.gov.hlth.hnsecure.message.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class V2PayloadValidator {

	private static final Logger logger = LoggerFactory.getLogger(V2PayloadValidator.class);

	private static Set<String> validReceivingFacility;
	private static String processingDomain;
	private static final String expectedEncodingChar = "^~\\&";
	private static final String segmentIdentifier = "MSH";
	private static String version;

	private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

	public V2PayloadValidator(AuthorizationProperties authorizationProperties) {
		validReceivingFacility = authorizationProperties.getValidReceivingFacility();
		processingDomain = authorizationProperties.getProcessingDomain();
		version = authorizationProperties.getVersion();
	}

	/**
	 *This method does generic validation and Pharmanet specific validation
	 * @param v2Message the hl7v2 message to validate
	 * @throws ValidationFailedException if a validation step fails
	 */
	@Handler
	// TODO This method is too long. We need to break it for understanding and
	// maintainence.
	public static void validate(Exchange exchange, String v2Message) throws ValidationFailedException {

		HL7Message messageObj = new HL7Message();

		String accessToken = (String) exchange.getIn().getHeader("Authorization");

		boolean isPharmanetMode = false;

		// Validate v2Message format
		validateMessageFormat(exchange, v2Message, messageObj);

		if ((!StringUtil.isEmpty(messageObj.getMessageType())
				&& (messageObj.getMessageType()).equals(Util.MESSAGE_TYPE_PNP))) {
			isPharmanetMode = true;
		}

		validateSendingFacility(exchange, messageObj, accessToken, isPharmanetMode);

		validateReceivingApp(exchange, messageObj);
		
		validateReceivingFacility(exchange, messageObj);

		populateOptionalField(messageObj);
		
		validatePhanrmanetMessageFormat(exchange, v2Message, messageObj, isPharmanetMode);

		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);

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
	protected static void validatePhanrmanetMessageFormat(Exchange exchange, String v2Message, HL7Message messageObj,
			boolean isPharmanetMode) throws ValidationFailedException {
		if (isPharmanetMode) {
			if (!Util.isSegmentPresent(v2Message, Util.ZCB_SEGMENT)) {
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
	protected static void validateReceivingFacility(Exchange exchange, HL7Message messageObj)
			throws ValidationFailedException {
		if (!messageObj.getReceivingApplication().equalsIgnoreCase(Util.RECEIVING_APP_PNP)) {

			if (validReceivingFacility.stream().noneMatch(messageObj.getReceivingFacility()::equalsIgnoreCase)) {
				generateError(messageObj, ErrorMessage.HL7Error_Msg_EncryptionError, exchange);

			} else if (messageObj.getReceivingApplication().equalsIgnoreCase(Util.RECEIVING_APP_PNP)
					&& (!messageObj.getMessageType().equalsIgnoreCase(Util.MESSAGE_TYPE_PNP))) {
				generatePharmanetError(messageObj, ErrorMessage.HL7Error_Msg_EncryptionError, exchange);
			}
		}
	}

	/**
	 * Validates receiving application
	 * @param exchange
	 * @param messageObj
	 * @throws ValidationFailedException
	 */
	protected static void validateReceivingApp(Exchange exchange, HL7Message messageObj)
			throws ValidationFailedException {
		if ((StringUtil.isEmpty(messageObj.getReceivingApplication())
				|| StringUtil.isEmpty(messageObj.getReceivingFacility()))) {

			generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidHL7Format, exchange);
		}

		// Check the validity
		if (!validateReceivingApplication(messageObj.getReceivingApplication())) {
			generateError(messageObj, ErrorMessage.HL7Error_Msg_UnknownReceivingApplication, exchange);
		}
	}

	/**
	 * @param exchange
	 * @param messageObj
	 * @param accessToken
	 * @param isPharmanetMode
	 * @throws ValidationFailedException
	 */
	protected static void validateSendingFacility(Exchange exchange, HL7Message messageObj, String accessToken,
			boolean isPharmanetMode) throws ValidationFailedException {
		// Validate Sending facility
		if (!StringUtil.isEmpty(messageObj.getSendingFacility())) {

			String facilityNameFromAccessToken = getSendingFacility(accessToken);

			if (!isPharmanetMode && StringUtil.isEmpty(messageObj.getSendingFacility())
					|| !messageObj.getSendingFacility().equals(facilityNameFromAccessToken)) {
				generateError(messageObj, ErrorMessage.HL7Error_Msg_FacilityIDMismatch, exchange);
			} else if (isPharmanetMode && StringUtil.isEmpty(messageObj.getSendingFacility())
					|| !messageObj.getSendingFacility().equals(facilityNameFromAccessToken)) {
				generatePharmanetError(messageObj, ErrorMessage.HL7Error_Msg_FacilityIDMismatch, exchange);

			}
		}
	}

	/**
	 * This method checks the format of incoming message
	 * @param exchange
	 * @param v2Message
	 * @param messageObj
	 * @throws ValidationFailedException
	 */
	protected static void validateMessageFormat(Exchange exchange, String v2Message, HL7Message messageObj)
			throws ValidationFailedException {
		if (!StringUtil.isEmpty(v2Message)) {
			String[] v2DataLines = v2Message.split("\n");
			String[] v2Segments = v2DataLines[0].split("\\|");

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
		if (StringUtil.isEmpty(messageObj.getEncodingCharacter())
				|| messageObj.getEncodingCharacter().toCharArray().length != 4) {
			generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidHL7Format, exchange);
		} else if (!sameChars(messageObj.getEncodingCharacter(), expectedEncodingChar)) {
			generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidMSHSegment, exchange);
		}
	}

	/**
	 * Populate optional field from properties file if present
	 * @param messageObj
	 */
	private static void populateOptionalField(HL7Message messageObj) {

		if (StringUtil.isEmpty(messageObj.getDateTime())) {
			messageObj.setDateTime(Util.getGenericDateTime());
		}

		if (StringUtil.isEmpty(messageObj.getVersionId())) {
			messageObj.setVersionId(version);
		}

		if (StringUtil.isEmpty(messageObj.getProcessingId())) {
			messageObj.setProcessingId(processingDomain);
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
		messageObject.setProcessingId(processingDomain);
		messageObject.setReceivingApplication(Util.RECEIVING_APP_HNSECURE);

		ErrorResponse errorResponse = new ErrorResponse();
		// TODO could probably make the constructResponse Static but need to refactor
		// the interface
		String v2Response = errorResponse.constructResponse(messageObject, errorMessage);
		logger.info(v2Response);
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
		messageObject.setProcessingId(processingDomain);
		messageObject.setReceivingApplication(Util.RECEIVING_APP_HNSECURE);

		PharmanetErrorResponse errorResponse = new PharmanetErrorResponse();

		String v2Response = errorResponse.constructResponse(messageObject, errorMessage);
		logger.info(v2Response);
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
		if (!StringUtil.isEmpty(auth)) {
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
