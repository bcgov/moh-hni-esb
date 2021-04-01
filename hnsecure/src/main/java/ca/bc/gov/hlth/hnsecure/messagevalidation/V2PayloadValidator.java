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

	private static Set<String> validReceivingFacility;
	private static String processingDomain;
	private static final String expectedEncodingChar = "^~\\&";
	private static boolean isValidSeg;
	private static final String segmentIdentifier = "MSH";
	private static final List<String> validDomainType = List.of(new String[] { "P", "E", "T", "D" });
	private static ErrorMessage errorMessage;
	private static ErrorResponse errorResponse;

	private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

	public V2PayloadValidator(AuthorizationProperties authorizationProperties) {
		validReceivingFacility = authorizationProperties.getValidReceivingFacility();
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

			if (Arrays.stream(v2Segments).allMatch(Objects::nonNull) && v2Segments.length >= 12) {
				errorResponse.initSegment(v2Segments, messageObj);
			} else {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_InvalidHL7Format;

			}
		} else {
			isValidSeg = false;
			errorMessage = ErrorMessage.HL7Error_Msg_InvalidHL7Format;

		}

		// Validate segment identifier
		if (isValidSeg && !messageObj.getSegmentIdentifier().equals(segmentIdentifier)) {
			isValidSeg = false;
			errorMessage = ErrorMessage.HL7Error_Msg_InvalidHL7Format;
		}

		// Validate encoding character
		if (isValidSeg) {
			if (StringUtil.isEmpty(messageObj.getEncodingCharacter())) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_InvalidHL7Format;
			} else if (isValidSeg && (messageObj.getEncodingCharacter().toCharArray()).length !=4 ) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_InvalidHL7Format;
			} else if (!sameChars(messageObj.getEncodingCharacter(), expectedEncodingChar)) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_InvalidMSHSegment;
			}
		}

		// Validate Sending facility
		if (isValidSeg && StringUtil.isEmpty(messageObj.getSendingFacility()))

		{

			messageObj.setSendingFacility(getSendingFacility(auth));
		}

		if (isValidSeg && !StringUtil.isEmpty(messageObj.getSendingFacility())) {

			String localFacilityName = getSendingFacility(auth);

			if (!messageObj.getSendingFacility().equals(localFacilityName)) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_FacilityIDMismatch;
			}
		}

		// Validate receiving application and receiving facility
		if (isValidSeg && (StringUtil.isEmpty(messageObj.getReceivingApplication())
				|| StringUtil.isEmpty(messageObj.getReceivingFacility()))) {
			isValidSeg = false;
			errorMessage = ErrorMessage.HL7Error_Msg_InvalidHL7Format;

		}

		if (isValidSeg && !StringUtil.isEmpty(messageObj.getReceivingFacility())) {
			if (validReceivingFacility.stream().noneMatch(messageObj.getReceivingFacility()::equalsIgnoreCase)) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_FacilityIDMismatch;

			}
		}

		if (isValidSeg && !StringUtil.isEmpty(messageObj.getReceivingApplication())) {

			String receivingApplication = getReceivingApplication(messageObj.getMessageType());

			if (!messageObj.getReceivingApplication().equals(receivingApplication)) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_UnknownReceivingApplication;
			}
		}

		// Populate Domain
		if (isValidSeg) {
			messageObj.setProcessingId(processingDomain);
		}

		if (!isValidSeg) {
			errorResponse = new ErrorResponse();
			messageObj.setReceivingApplication("HNSecure");
			String v2Response = errorResponse.constructResponse(messageObj, errorMessage);
			logger.info(v2Response);
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			exchange.getIn().setBody(v2Response);
			throw new ValidationFailedException(errorMessage.getErrorMessage());
		}

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

	private static boolean sameChars(String firstStr, String secondStr) {
		char[] first = firstStr.toCharArray();
		char[] second = secondStr.toCharArray();
		Arrays.sort(first);
		Arrays.sort(second);
		return Arrays.equals(first, second);
	}

}
