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

	private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

	public V2PayloadValidator(AuthorizationProperties authorizationProperties) {
		validReceivingFacility = authorizationProperties.getValidReceivingFacility();
		processingDomain = authorizationProperties.getProcessingDomain();
	}

	/**
	 * Validates the Hl7V2 transaction type (MSH.8) format and required fields
	 * 
	 * @param v2Message the hl7v2 message to validate
	 * @throws ValidationFailedException if a validation step fails
	 */
	@Handler
	public static void validate(Exchange exchange, String v2Message) throws ValidationFailedException {

		HL7Message messageObj = new HL7Message();

		String accessToken = (String) exchange.getIn().getHeader("Authorization");

		// Validate v2Message format
		if (!StringUtil.isEmpty(v2Message)) {
			String[] v2DataLines = v2Message.split("\r\n");
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
		} 
		else if (!sameChars(messageObj.getEncodingCharacter(), expectedEncodingChar)) {
			generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidMSHSegment, exchange);
		}	

		// Validate Sending facility
		if (!StringUtil.isEmpty(messageObj.getSendingFacility())) {

			String facilityNameFromAccessToken = getSendingFacility(accessToken);

			if (StringUtil.isEmpty(messageObj.getSendingFacility())
					|| !messageObj.getSendingFacility().equalsIgnoreCase(facilityNameFromAccessToken)) {
				generateError(messageObj, ErrorMessage.HL7Error_Msg_FacilityIDMismatch, exchange);
			}
		}

		// Validate receiving application and receiving facility
		if ((StringUtil.isEmpty(messageObj.getReceivingApplication())
				|| StringUtil.isEmpty(messageObj.getReceivingFacility()))) {

			generateError(messageObj, ErrorMessage.HL7Error_Msg_InvalidHL7Format, exchange);
		}

		// Validate the receiving facility is listed in application properties
		if (!messageObj.getReceivingApplication().equalsIgnoreCase(Util.RECEIVING_APP_PNP)) {
			if (validReceivingFacility.stream().noneMatch(messageObj.getReceivingFacility()::equalsIgnoreCase)) {
				generateError(messageObj, ErrorMessage.HL7Error_Msg_EncryptionError, exchange);
			}
		}

		// Validate the receiving application exists	
		if (!validateReceivingApplication(messageObj.getReceivingApplication())) {
			generateError(messageObj, ErrorMessage.HL7Error_Msg_UnknownReceivingApplication, exchange);
		}

	}

	private static void generateError(HL7Message messageObject, ErrorMessage errorMessage, Exchange exchange)
			throws ValidationFailedException {
		messageObject.setProcessingId(processingDomain);
		messageObject.setReceivingApplication("HNSecure");

		ErrorResponse errorResponse = new ErrorResponse();
		// TODO could probably make the constructResponse Static but need to refactor
		// the interface
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
			String decodeBase1 = Util.decodeBase64(split[1]);

			try {
				JSONObject jsonObject = (JSONObject) jsonParser.parse(decodeBase1);
				clientId = (String) jsonObject.get("azp");
			} catch (net.minidev.json.parser.ParseException e) {
				e.printStackTrace();
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
