package ca.bc.gov.hlth.hnsecure.messagevalidation;

import java.util.Arrays;
import java.util.List;
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

	private static Set<String> validV2MessageTypes;
	private static Set<String> validReceivingFascility;
	private static String processingDomain;
	private static final String expectedEncodingChar = "^~\\&";
	private static boolean isValidSeg;
	private static final String segmentIdentifier = "MSH";
	private static final List<String> validDomainType = List.of(new String[]{"P", "E", "T", "D"});
	private static ErrorMessage errorMessage;
	private static ErrorResponse errorResponse;

	private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

	public V2PayloadValidator(AuthorizationProperties authorizationProperties) {
		validV2MessageTypes = authorizationProperties.getValidV2MessageTypes();
		validReceivingFascility = authorizationProperties.getValidReceivingFacility();
		processingDomain = authorizationProperties.getProcessingDomain();
	}

	/**
	 * Validates the Hl7V2 transaction type (MSH.8) format and required fields
	 * 
	 * @param v2Message the hl7v2 message to validate
	 * @throws ValidationFailedException 
	 */
	@Handler
	public static void validate(Exchange exchange, String v2Message) throws ValidationFailedException {

		isValidSeg = true;
		HL7Message messageObj = new HL7Message();
		errorResponse = new ErrorResponse();

		String auth = (String) exchange.getIn().getHeader("Authorization");

		// Validate v2Message format
		if (!StringUtil.isEmpty(v2Message)) {
			String[] v2DataLines = v2Message.split("\r\n");
			String[] v2Segments = v2DataLines[0].split("\\|");

			if (Arrays.stream(v2Segments).allMatch(Objects::nonNull) && v2Segments.length >=12) {
				errorResponse.initSegment(v2Segments, messageObj);
			} else {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_InvalidHL7Format;

			}
		} else {
			isValidSeg = false;
			errorMessage = ErrorMessage.HL7Error_Msg_NoInputHL7;

		}

		// Validate segment identifier
		if (isValidSeg && !messageObj.getSegmentIdentifier().equals(segmentIdentifier)) {
			isValidSeg = false;
			errorMessage = ErrorMessage.HL7Error_Msg_MSHSegmentMissing;
		}

		// Validate encoding character
		if (isValidSeg && !messageObj.getEncodingCharacter().equals(expectedEncodingChar)) {
			isValidSeg = false;
			errorMessage = ErrorMessage.HL7Error_Msg_InvalidHL7Format;
		}

		// Validate Sending facility
		if (isValidSeg && StringUtil.isEmpty(messageObj.getSendingFacility())) {

			messageObj.setSendingFacility(getSendingFacility(auth));
		}

		if (isValidSeg && !StringUtil.isEmpty(messageObj.getSendingFacility())) {

			String localFascilityName = getSendingFacility(auth);

			if (!messageObj.getSendingFacility().equals(localFascilityName)) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_FacilityIDMismatch;
			}
		}
		
		//Validate receiving application
		if (isValidSeg && !StringUtil.isEmpty(messageObj.getReceivingApplication())) {

			String receivingApplication = getReceivingApplication(messageObj.getMessageType());

			if (!messageObj.getReceivingApplication().equals(receivingApplication)) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_UnknownReceivingApplication;
			}
		}

		// Validate Receiving facility
		if (isValidSeg && StringUtil.isEmpty(messageObj.getReceivingFacility())) {
			isValidSeg = false;
			errorMessage = ErrorMessage.HL7Error_Msg_MissingReceivingFacility;

		}

		if (isValidSeg && !StringUtil.isEmpty(messageObj.getReceivingFacility())) {
			if (validReceivingFascility.stream().noneMatch(messageObj.getReceivingFacility()::equalsIgnoreCase)) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_FacilityIDMismatch;

			}
		}

		// Validate Domain
		if (isValidSeg && StringUtil.isEmpty(messageObj.getProcessingId())) {
			messageObj.setProcessingId(processingDomain);
		}

		if (isValidSeg && !validDomainType.contains(messageObj.getProcessingId())) {
			isValidSeg = false;
			errorMessage = ErrorMessage.HL7Error_Msg_InvalidDomain;
		}

		if (!isValidSeg) {

			errorResponse = new ErrorResponse();
			messageObj.setReceivingApplication("HNSecure");
			String v2Response = errorResponse.constructResponse(messageObj, null, errorMessage);
			logger.info(v2Response);
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			exchange.getIn().setBody(v2Response);
			throw new ValidationFailedException(errorMessage.getErrorMessage());
		}

		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);

	}

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
	
	private static String getReceivingApplication(String messageType) {
		return MessageUtil.mTypeCollection.get(messageType);
		
		
	}

}
