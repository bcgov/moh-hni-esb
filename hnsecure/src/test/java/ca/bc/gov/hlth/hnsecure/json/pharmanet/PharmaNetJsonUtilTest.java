package ca.bc.gov.hlth.hnsecure.json.pharmanet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Base64;
import java.util.UUID;

import org.junit.Test;

import net.minidev.json.JSONObject;

public class PharmaNetJsonUtilTest {

	public static final String V2_PHARMANET_MSG = "MSH|^~\\&|POS|moh_hnclient_dev|PNP|PP|||ZPN|9286|P|2.1||\r\n"
            + "ZZZ|TDR||9286|P1|2F3P2||||\r\n"
            + "ZCA||03|00|KC|13|\r\n"
            + "ZCB|BC00007007|201222|9286\r\n"
            + "ZPC|2240579||||||Y|ZPC1^^^766720\r\n"
            + "\r\n";    
	
	@Test
	public void testCreateJsonObjectPharmanetObject() {		
		String transactionUUID = UUID.randomUUID().toString();
		String encodedV2 = new String(Base64.getEncoder().encode(V2_PHARMANET_MSG.getBytes()));
		JSONObject jsonObj = PharmaNetJsonUtil.createJsonObjectPharmanet(transactionUUID, encodedV2);
		assertEquals("{\"transactionUUID\":\"" + transactionUUID + "\",\"hl7Message\":\"TVNIfF5+XCZ8UE9TfG1vaF9obmNsaWVudF9kZXZ8UE5QfFBQfHx8WlBOfDkyODZ8UHwyLjF8fA0KWlpafFREUnx8OTI4NnxQMXwyRjNQMnx8fHwNClpDQXx8MDN8MDB8S0N8MTN8DQpaQ0J8QkMwMDAwNzAwN3wyMDEyMjJ8OTI4Ng0KWlBDfDIyNDA1Nzl8fHx8fHxZfFpQQzFeXl43NjY3MjANCg0K\"}", 
				jsonObj.toJSONString());
	}
	
	@Test
	public void testCreateJsonObjectPharmanetNullV2Message() {		
		JSONObject jsonObj = PharmaNetJsonUtil.createJsonObjectPharmanet(null, null);
		assertTrue(jsonObj.isEmpty());
	}

	@Test
	public void testParseJsonToPharmanetMsg() {
		String transactionUUID = UUID.randomUUID().toString();
		String encodedV2 = new String(Base64.getEncoder().encode(V2_PHARMANET_MSG.getBytes()));
		JSONObject jsonObj = PharmaNetJsonUtil.createJsonObjectPharmanet(transactionUUID, encodedV2);
		PharmaNetJsonMessage pharmaNetJsonMessage = PharmaNetJsonUtil.parseJsonToPharmanetMsg(jsonObj);
		assertEquals(transactionUUID, pharmaNetJsonMessage.getTransactionUUID());
		assertEquals(encodedV2, pharmaNetJsonMessage.getHl7Message());
	}

	@Test
	public void testParseJsonToPharmanetMsgNullJsonObject() {
		PharmaNetJsonMessage pharmaNetJsonMessage = PharmaNetJsonUtil.parseJsonToPharmanetMsg(null);
		assertNull(pharmaNetJsonMessage);
	}
}
