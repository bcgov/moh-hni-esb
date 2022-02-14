package com.cgi.mockendpoints.util;

import org.apache.commons.lang3.StringUtils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class JsonUtil {

	public static String FHIR_JSONMESSAGE_TYPE = "contentType";
	public static String FHIR_JSONMESSAGE_DATA = "data";
	public static String FHIR_JSONMESSAGE_ATTACHMENT = "attachment";
	public static String FHIR_JSONMESSAGE_RESOURCETYPE = "resourceType";
	public static String FHIR_JSONMESSAGE_STATUS = "status";
	public static String FHIR_JSONMESSAGE_CONTENT = "content";
	
	/**
	 * This method is used to create a Json Array for the Mock V2 HL7 message.
	 * A null should be returned if the HL7 message is empty.
	 * 
	 * @param hl7message the message to transform
	 * @return JSONArray
	 */
	public static JSONArray createFHIRJsonArray(final String hl7message) {
		// if the content of HL7 message is null/empty, return null;
		if (StringUtils.isBlank(hl7message)) {
			return null;
		}
		JSONArray contentArray = new JSONArray();
		JSONObject contentObj = new JSONObject();
		JSONObject attachmentObj = new JSONObject();

		attachmentObj.put(FHIR_JSONMESSAGE_TYPE, "x-application/hl7-v2+er7");
		attachmentObj.put(FHIR_JSONMESSAGE_DATA, hl7message);
		contentObj.put(FHIR_JSONMESSAGE_ATTACHMENT, attachmentObj);
		contentArray.add(contentObj);

		// return the contentArray object
		return contentArray;
	}
}
