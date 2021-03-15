package ca.bc.gov.hlth.hnsecure.messagevalidation;

import ca.bc.gov.hlth.hnsecure.authorization.AuthorizationProperties;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class V2PayloadValidatorTest {

    private AuthorizationProperties authProps = new AuthorizationProperties(
            "account",
            "moh_hnclient_dev",
            "system/*.write",
            "r03",
            "",
            "BC0002041"
            ,"D");

    private static String msgInvalidMSH="|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";

    private static String msgR03="MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";

    private static String msgR15= "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D|2.4||\r\n" +
            "ZHD|20201015092224|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000073721^^^BC^PH\r\n" +
            "IN1||||||||||||20190228\r\n";
    
    private static String msgInvalidFormat= "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D\r\n" +
            "ZHD|20201015092224|^^00000010|HNAIADMINISTRATION|||2.4\r\n" +
            "PID||0000073721^^^BC^PH\r\n" +
            "IN1||||||||||||20190228\r\n";
    
    private static String msgMissingEncodingChar="MSH|~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
            "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
            "PID||0000053655^^^BC^PH\r\n";
    private final V2PayloadValidator v2PayloadValidator = new V2PayloadValidator(authProps);
    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());

    @Test
    @Ignore
    public void messageDoesntBeginWithMSHTest() {
        v2PayloadValidator.validate(exchange, msgInvalidMSH);
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 403);
        assertEquals(exchange.getIn().getBody(), "{ \"error\": \"Message does not start with MSH and is an invalid v2 message.\" }");
    }
    
    @Test
    public void test_HL7Error_Msg_InvalidHL7Format() {
    	String expectedResponse = "MSA|AR|null|VLDT6  The Supplied HL7 Message was improperly formatted|";
    	
        v2PayloadValidator.validate(exchange, msgInvalidFormat);
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 403);
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(response,expectedResponse);       
    }
    
    @Test
    public void test_HL7Error_Msg_InvalidHL7Format_MissingEncodingChar() {
    	String expectedResponse = "MSA|AR|null|VLDT6  The Supplied HL7 Message was improperly formatted|";
    	
        v2PayloadValidator.validate(exchange,msgMissingEncodingChar );
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 403);
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(response,expectedResponse);       
    }
    
    @Test
    public void test_HL7Error_Msg_NoInputHL7() {
    	String expectedResponse = "MSA|AR|null|VLDT1  No HL7 Message was supplied as input|";
    	
        v2PayloadValidator.validate(exchange, null);
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 403);
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(response,expectedResponse);       
    }
    
    @Test
    public void test_HL7Error_Msg_MSHSegmentMissing() {
        String msgInput="|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n" +
                "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" +
                "PID||0000053655^^^BC^PH\r\n";
    	String expectedResponse = "MSA|AR|null|VLDT2  The MSH Segment from the HL7 Message is missing.|";
    	
        v2PayloadValidator.validate(exchange, msgInput);
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 403);
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(response,expectedResponse);       
    }

    @Test
    @Ignore
    public void messageIsNotValidV2MessageType() {
        v2PayloadValidator.validate(exchange, msgR15);
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 403);
        assertEquals(exchange.getIn().getBody(), "{ \"error\": \"Unsupported v2 transaction type.\" }");
    }

    @Test
    @Ignore
    public void validMessage() {
        v2PayloadValidator.validate(exchange, msgR03);
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
    }
}
