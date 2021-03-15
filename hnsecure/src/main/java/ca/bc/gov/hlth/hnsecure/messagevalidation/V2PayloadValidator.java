package ca.bc.gov.hlth.hnsecure.messagevalidation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jose.util.ArrayUtils;

import ca.bc.gov.hlth.hnsecure.authorization.AuthorizationProperties;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.message.ErrorResponse;
import ca.bc.gov.hlth.hnsecure.message.HL7Message;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class V2PayloadValidator {

	private static final Logger logger = LoggerFactory.getLogger(V2PayloadValidator.class);

	private static Set<String> validV2MessageTypes;
	private static Set<String> validReceivingFascility;
	private static String processingDomain;
	private static String expectedEncodingChar = "^~\\&";
	private static boolean isValidSeg;
	private static String segmentIdentifier = "MSH";
	private static List<String> validDomainType = Collections
			.unmodifiableList(Arrays.asList(new String[] { "P", "E", "T", "D" }));
	private static ErrorMessage errorMessage;
	private static ErrorResponse errorResponse;

	private static final JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

	public V2PayloadValidator(AuthorizationProperties authorizationProperties) {
		validV2MessageTypes = authorizationProperties.getValidV2MessageTypes();
		validReceivingFascility = authorizationProperties.getVaildReceivingFascility();
		processingDomain = authorizationProperties.getProcessingDomain();
	}

	/**
	 * Validates the Hl7V2 transaction type (MSH.8) format and required fields
	 * 
	 * @param v2Message the hl7v2 message to validate
	 */
	@Handler
	public static void validate(Exchange exchange, String v2Message) {

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
		if (isValidSeg && StringUtil.isEmpty(messageObj.getSendingFascility())) {

			messageObj.setSendingFascility(getSendingFascility(auth));
		}

		if (isValidSeg && !StringUtil.isEmpty(messageObj.getSendingFascility())) {

			String localFascilityName = getSendingFascility(auth);

			if (!messageObj.getSendingFascility().equals(localFascilityName)) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_FacilityIDMismatch;
			}
		}

		// Validate Receiving facility
		if (isValidSeg && StringUtil.isEmpty(messageObj.getReceivingFascility())) {
			isValidSeg = false;
			errorMessage = ErrorMessage.HL7Error_Msg_MissingReceivingFacility;

		}

		if (isValidSeg && !StringUtil.isEmpty(messageObj.getReceivingFascility())) {
			if (!validReceivingFascility.stream().anyMatch(messageObj.getReceivingFascility()::equalsIgnoreCase)) {
				isValidSeg = false;
				errorMessage = ErrorMessage.HL7Error_Msg_FacilityIDMismatch;

			}
		}

		// Validate Domain
		if (isValidSeg && StringUtil.isEmpty(messageObj.getProcessingId())) {
			// TODO::Populate Processing id from client config
			messageObj.setProcessingId(processingDomain);
		}

		if (isValidSeg && !validDomainType.contains(messageObj.getProcessingId())) {
			isValidSeg = false;
			errorMessage = ErrorMessage.HL7Error_Msg_InvalidDomain;
		}

		if (!isValidSeg) {

			errorResponse = new ErrorResponse();
			String v2Response = errorResponse.consructResponse(messageObj, null, errorMessage);
			logger.info(v2Response);
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 403);
			exchange.getIn().setBody(v2Response);
			return;
		}

		String transactionType = v2Message.split("\\|")[8];
		if (!validV2MessageTypes.stream().anyMatch(transactionType::equalsIgnoreCase)) {
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 403);
			exchange.getIn().setBody("{ \"error\": \"Unsupported v2 transaction type.\" }");
			return;
		}

		/*
		 * int mshIndex = v2Message.indexOf("MSH|"); if (mshIndex != 0) {
		 * exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 403);
		 * exchange.getIn()
		 * .setBody("{ \"error\": \"Message does not start with MSH and is an invalid v2 message.\" }"
		 * ); return; }
		 */

		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);

	}

	private static String getSendingFascility(String auth) {
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

}
