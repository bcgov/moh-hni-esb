package ca.bc.gov.hlth.hnsecure.json.pharmanet;

import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minidev.json.JSONObject;

/**
 * Utilities to convert to and from JSON Object
 * 
 * @author dave.p.barrett
 *
 */
public class PharmaNetJsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(PharmaNetJsonUtil.class);
	
	public static String TRANSACTION_UUID = "transactionUUID";
	public static String HL7_MESSAGE = "hl7Message";

	/**
	 * Create a JSON message for PharmaNet from the HL7 message: The format is:
	 * 	{
		    "transactionUUID": "c7a45f97-4b5d-d8de-59a8-6305cb194a7b", 
		    "hl7Message":     "TVNIfF5+XCZ8VFJYVE9PTHxQQ0FSRVNVUHxQTlB8UFB8fHxaUE58OTI4NnxQfDIuMXx8DVpaWnxURFJ8fDkyODZ8UDF8MkYzUDJ8fHx8DVpDQXx8MDN8MDB8S0N8MTN8DVpDQnxCQzAwMDA3MDA3fDIwMTIyMnw5Mjg2DVpQQ3wyMjQwNTc5fHx8fHx8WXxaUEMxXl5eNzY2NzIwDQ0="
		}
	 * hl7Message value is a Base64 formatted hl7 message. The content-type is 'application/json'.
	 * 
	 * @param transactionUUID the value of the Transaction UUID associated with this message transfer
	 * @param hl7Message the HL7 message to be set as the hl7Message value
	 * 
	 * @return a {@link JSONObject} containing the provided Transaction UUID and HL7 message or null if the HL7 Message was empty
	 */
	public static JSONObject createJsonObjectPharmanet(final String transactionUUID, final String hl7Message) {

		if (StringUtil.isBlank(hl7Message)) {
			return null;
		}

		JSONObject jsonObj = new JSONObject();
		jsonObj.put(TRANSACTION_UUID, transactionUUID);
		jsonObj.put(HL7_MESSAGE, hl7Message);
		logger.debug("The JSON Message is:"+jsonObj.toJSONString());

		return jsonObj;		
	}	
	
	/**
	 * Extracts the fields from a JSON object and populates a PharmaNetJsonMessage
	 * 
	 * @param json the JSON Object to extract the data from
	 * 
	 * @return the PharmaNetJsonMessage populated with the JSON object data
	 */
	public static PharmaNetJsonMessage parseJsonToPharmanetMsg(JSONObject json) {
		
		if (json == null) {
			return null;
		}
		
		PharmaNetJsonMessage pharmaNetJsonMessage = new PharmaNetJsonMessage();
		pharmaNetJsonMessage.setTransactionUUID(json.getAsString(TRANSACTION_UUID));
		pharmaNetJsonMessage.setHl7Message(json.getAsString(HL7_MESSAGE));
		
		return pharmaNetJsonMessage;
	}	
	
}
