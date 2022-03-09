package ca.bc.gov.hlth.hncommon.json.fhir;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public final class FHIRJsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(FHIRJsonUtil.class);

	public static final String FHIR_JSON_MESSAGE_TYPE = "contentType";
	public static final String FHIR_JSON_MESSAGE_DATA = "data";
	public static final String FHIR_JSON_MESSAGE_ATTACHMENT = "attachment";
	public static final String FHIR_JSON_MESSAGE_RESOURCETYPE = "resourceType";
	public static final String FHIR_JSON_MESSAGE_STATUS = "status";
	public static final String FHIR_JSON_MESSAGE_CONTENT = "content";

	/**
	 * This method is used to create a Json Object for the BC health V2 HL7 message.
	 * A null should be returned if the HL7 message is empty.
	 * 
	 * @param hl7message the message to transform
	 * @return JSONObject
	 */
	public static JSONObject createFHIRJsonObj(final String hl7message) {
		final String methodName = LoggingUtil.getMethodName();

		// if the content of HL7 message is null/empty, return null;
		if (StringUtils.isBlank(hl7message)) {
			return null;
		}

		// init a JSON object
		JSONObject v2JsonObj = new JSONObject();
		JSONArray contentArray = new JSONArray();
		JSONObject contentObj = new JSONObject();
		JSONObject attachmentObj = new JSONObject();

		// There is a FHIR message specification of the BC Health. This piece of code is
		// based on this specification. Please refer to the
		// https://github.com/bcgov/bcmoh-iam-integration-guide/wiki/FHIR-message-specification-to-wrap-HL7v2-messages
		// for details.
		attachmentObj.put(FHIR_JSON_MESSAGE_TYPE, "x-application/hl7-v2+er7");
		attachmentObj.put(FHIR_JSON_MESSAGE_DATA, hl7message);
		contentObj.put(FHIR_JSON_MESSAGE_ATTACHMENT, attachmentObj);
		contentArray.add(contentObj);
		v2JsonObj.put(FHIR_JSON_MESSAGE_RESOURCETYPE, "DocumentReference");
		v2JsonObj.put(FHIR_JSON_MESSAGE_STATUS, "current");
		v2JsonObj.put(FHIR_JSON_MESSAGE_CONTENT, contentArray);
		logger.debug("{} - The JSON Message is: {}", methodName, v2JsonObj.toJSONString());
		// return the JSON object
		return v2JsonObj;
	}

	/**
	 * This method is for parse a JSON message into a FHIR Message object based on
	 * the specification
	 * 
	 * @param jsonObj - the json message to parse
	 * @return FHIRJsonMessage
	 */
	public static FHIRJsonMessage parseJson2FHIRMsg(final JSONObject jsonObj) {

		FHIRJsonMessage fhirJsonMsg = new FHIRJsonMessage();

		if (jsonObj == null) {
			return null;
		}

		for (Entry<String, Object> hs : jsonObj.entrySet()) {
			String key = hs.getKey();
			Object value = hs.getValue();

			if (value instanceof String) {
				switch (key) {
				case FHIR_JSON_MESSAGE_RESOURCETYPE:
					fhirJsonMsg.setResourceType(value.toString());
					break;
				case FHIR_JSON_MESSAGE_STATUS:
					fhirJsonMsg.setStatus(value.toString());
					break;
				default:
					logger.error("This is not an valid FHIR message!");
					return null;
				}

			}
			if (key.equals(FHIR_JSON_MESSAGE_CONTENT) && value instanceof JSONArray) {
				JSONObject contentJson = (JSONObject) ((JSONArray) value).get(0);
				JSONObject attachmentJson = (JSONObject) contentJson.get(FHIR_JSON_MESSAGE_ATTACHMENT);

				for (Entry<String, Object> attach : attachmentJson.entrySet()) {
					String attachKey = attach.getKey();
					Object attachValue = attach.getValue();
					switch (attachKey) {
					case FHIR_JSON_MESSAGE_TYPE:
						fhirJsonMsg.setContentType(attachValue.toString());
						break;
					case FHIR_JSON_MESSAGE_DATA:
						fhirJsonMsg.setV2MessageData(attachValue.toString());
						break;
					default:
						logger.error("This is not an valid FHIR message!");
						return null;
					}
				
				}

			}
		}
		return fhirJsonMsg;
	}
}
