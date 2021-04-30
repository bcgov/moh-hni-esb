package ca.bc.gov.hlth.hnsecure.messagevalidation;

import ca.bc.gov.hlth.hnsecure.authorization.AuthorizationProperties;
import ca.bc.gov.hlth.hnsecure.message.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class V2PayloadValidatorTest {

    private AuthorizationProperties authProps = new AuthorizationProperties(
            "account",
            "moh_hnclient_dev",
            "system/*.write",
            "r03",
            "",
            "BC00002041"
            ,"D"
            ,"2.1");

    private final V2PayloadValidator v2PayloadValidator = new V2PayloadValidator(authProps);
 
    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());
    
    
    @Test
    public void testHL7ErrorMsgInvalidHL7Format() {
    	String expectedResponse = "MSA|AR||VLDT014E  The Supplied HL7 Message was improperly formatted|";

        assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange, SamplesToSend.msgInvalidFormat);
        });

        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(response,expectedResponse);       
    }
    
    @Test
    public void testHL7ErrorMsgInvalidHL7FormatMissingEncodingChar() throws ValidationFailedException {
    	String expectedResponse = "MSA|AR|20191108083244|VLDT014E  The Supplied HL7 Message was improperly formatted|";

        assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange,SamplesToSend.msgMissingEncodingChar );
        });
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(response,expectedResponse);       
    }
    
    @Test
    public void testHL7ErrorMsgNoInputHL7() throws ValidationFailedException {
    	String expectedResponse = "MSA|AR||VLDT014E  The Supplied HL7 Message was improperly formatted|";

        assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange, null);
        });
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(response,expectedResponse);       
    }
    
    @Test
    public void testHL7ErrorMsgMSHSegmentMissing() {
    	String expectedResponse = "MSA|AR|20191108083244|VLDT014E  The Supplied HL7 Message was improperly formatted|";

    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange, SamplesToSend.msgInvalidMSH);
        });
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(response,expectedResponse);       
    }
    


    @Test
    public void testHL7ErrorMsgFacilityIdMismatch() {
    
        String msgInput="MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\n" +
                "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\n" +
                "PID||0000053655^^^BC^PH\n";
    	String expectedResponse = "MSA|AR|20191108083244|VLDT008E  The Client Facility and HL7 Sending Facility IDs do not match.|";

    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange, msgInput);
        });
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(response,expectedResponse);       
    }
    
    @Test
    public void testHL7ErrorMsgEncryptionError() {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
    
        String msgInput="MSH|^~\\&|HNWeb|moh_hnclient_dev|RAIGT-PRSN-DMGR|BC0002041|20191108083244|train96|R03|20191108083244|D|2.4||\n" +
                "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\n" +
                "PID||0000053655^^^BC^PH\n";
    	String expectedResponse = "MSA|AR|20191108083244|TXFR029E  Encryption protocols failed with remote facility.|";

    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange, msgInput);
        });
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(response,expectedResponse);       
    }
    
    @Test
    public void testHL7ErrorMsgEncryptionErrorPharmanet() {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
    
        String msgInput="MSH|^&~\\|DESKTOP|moh_hnclient_dev|PNP|PP|2012/01/06 15:47:24|SS0AR|ZP|000008|D|2.1||\r\n"+
        		"ZZZ|TRP|R|000008|P1|XXASD||||\r\n"+
        		"ZCA|000001|03|00|AR|04|\r\n"+
        		"ZCB|BC000001AB|210405|000008\n"+
        		"ZCC||||||||||0009735391361|\r\n";
    	String expectedResponse = "ZZZ||1|||||TXFR029E  Encryption protocols failed with remote facility.||";

    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange, msgInput);
        });
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
        String response = ((String) exchange.getIn().getBody()).split("\n")[3];
        assertEquals(response,expectedResponse);       
    }
    
    
    @Test
    public void testHL7ErrorMsgTransactionFromatError() {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
    	
        String msgInput="MSH|^&~\\|DESKTOP|moh_hnclient_dev|PNP|PP|2012/01/06 15:47:24|SS0AR|ZPN|000008|D|2.1||\n"+
        		"ZZZ|TRP|R|000008|P1|XXASD||||\n"+
        		"ZCA|000001|03|00|AR|04|\n"+
        		"ZCC||||||||||0009735391361|\n";
        
    	String expectedResponse = "ZZZ||1|||||PNPA004E  Transaction format error detected||";

    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange, msgInput);
        });
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
        String response = ((String) exchange.getIn().getBody()).split("\n")[3];
        assertEquals(response,expectedResponse);        
       
    }
    
    
    @Test
    public void testPNPMessageFormat() {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
        String msgInput="MSH|^&~\\|DESKTOP|moh_hnclient_dev|PNP|PP|2012/01/06 15:47:24|SS0AR|ZPN|000008|D|2.1||\r\n"+
        		"ZZZ|TRP|R|000008|P1|XXASD||||\r\n"+
        		"ZCA|000001|03|00|AR|04|\r\n"+
        		"ZCC||||||||||0009735391361|\r\n";
        
    	String expectedZCA ="ZCA|||50|||" ;
    	String expectedZCB ="ZCB|||" ; 
    	String expectedZZZ ="ZZZ||1|||||PNPA004E  Transaction format error detected||" ; 
    			
    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange, msgInput);
        });
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
        String zca = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(zca,expectedZCA);
        
        String zcb = ((String) exchange.getIn().getBody()).split("\n")[2];
        assertEquals(zcb,expectedZCB);
        
        String zzz = ((String) exchange.getIn().getBody()).split("\n")[3];
        assertEquals(zzz,expectedZZZ);
        
    }


    @Test  
    public void testValidMessage() throws ValidationFailedException {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
        v2PayloadValidator.validate(exchange, SamplesToSend.msgR03);
        assertEquals(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
    }
}
