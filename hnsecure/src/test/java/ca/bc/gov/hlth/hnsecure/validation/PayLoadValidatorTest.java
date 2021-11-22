package ca.bc.gov.hlth.hnsecure.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.apache.camel.Exchange;
import org.apache.http.HttpStatus;
import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.exception.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;
import ca.bc.gov.hlth.hnsecure.test.TestPropertiesLoader;

public class PayLoadValidatorTest extends TestPropertiesLoader {

    private PayLoadValidator v2PayloadValidator = new PayLoadValidator(new ValidatorImpl());

    @Test
    public void testHL7ErrorMsgInvalidHL7Format() {
    	String expectedResponse = "MSA|AR||VLDT014E  The Supplied HL7 Message was improperly formatted|||";
    	exchange.getIn().setBody(SamplesToSend.msgInvalidFormat);
        assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });

        assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(expectedResponse, response);       
    }
    
    @Test
    public void testHL7ErrorMsgMissingReceivingFacility() {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
    	String expectedResponse = "MSA|AR|20191108083244|VLDT009E  The Receiving Facility ID was not specified in the HL7 Message.|||";
    	exchange.getIn().setBody(SamplesToSend.missingReceivingFacility);
        assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });

        assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(expectedResponse, response);       
    }
    
    @Test
    public void testHL7ErrorMsgUnknownReceivingApplication() {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
    	String expectedResponse = "MSA|AR|20191108083244|HNPS005E  Unknown receiving application: RAIGT-PRSN-DMGRX|||";
    	exchange.getIn().setBody(SamplesToSend.unknownReceivingApplication);
        assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });

        assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(expectedResponse, response);       
    }
    
    @Test
    public void testHL7ErrorMsgInvalidHL7FormatMissingEncodingChar() throws ValidationFailedException {
    	String expectedResponse = "MSA|AR|20191108083244|VLDT014E  The Supplied HL7 Message was improperly formatted|||";
    	exchange.getIn().setBody(SamplesToSend.msgMissingEncodingChar);
        assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange );
        });
        assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(expectedResponse, response);     
    }
    
    @Test
    public void testHL7ErrorMsgNoInputHL7() throws ValidationFailedException {
    	String expectedResponse = "MSA|AR||VLDT004E  No HL7 Message was supplied as input|||";
    	exchange.getIn().setBody(null);
        assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });
        assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(expectedResponse, response);
    }
    
    @Test
    public void testHL7ErrorMsgMSHSegmentMissing() {
    	String expectedResponse = "MSA|AR|20191108083244|VLDT007E  The MSH Segment from the HL7 Message is missing.|||";
    	exchange.getIn().setBody(SamplesToSend.msgMissingMSH);
    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });
    	assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(expectedResponse, response);       
    }
    
    @Test
    public void testHL7ErrorMsgInvalidMSHSegment() {
    	String expectedResponse = "MSA|AR|20191108083244|HNPS002E  Invalid MSH segment format|||";
    	exchange.getIn().setBody(SamplesToSend.msgInvalidMSH);
    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });
    	assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(expectedResponse, response);       
    }

    @Test
    public void testHL7ErrorMsgFacilityIdMismatch() {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
        String msgInput="MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\n" +
                "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\n" +
                "PID||0000053655^^^BC^PH\n";
    	String expectedResponse = "MSA|AR|20191108083244|VLDT008E  The Client Facility and HL7 Sending Facility IDs do not match: moh_hnclient_dev|||";
    	exchange.getIn().setBody(msgInput);
    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });
    	assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(expectedResponse, response);
    }
    
    @Test
    public void testHL7ErrorMsgEncryptionError() {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
    	
        String msgInput="MSH|^~\\&|HNWeb|moh_hnclient_dev|RAIGT-PRSN-DMGR|BC0002041|20191108083244|train96|R03|20191108083244|D|2.4||\n" +
                "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\n" +
                "PID||0000053655^^^BC^PH\n";
    	String expectedResponse = "MSA|AR|20191108083244|TXFR029E  Encryption protocols failed with remote facility.|||";
    	exchange.getIn().setBody(msgInput);
    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });
    	assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(expectedResponse, response);
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
    	exchange.getIn().setBody(msgInput);
    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });
        assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[3];
        assertEquals(expectedResponse, response); 
    }
    
    @Test
    public void testHL7ErrorMsgTransactionFromatError() {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
    	
        String msgInput="MSH|^&~\\|DESKTOP|moh_hnclient_dev|PNP|PP|2012/01/06 15:47:24|SS0AR|ZPN|000008|D|2.1||\n"+
        		"ZZZ|TRP|R|000008|P1|XXASD||||\n"+
        		"ZCA|000001|03|00|AR|04|\n"+
        		"ZCC||||||||||0009735391361|\n";
        
    	String expectedResponse = "ZZZ||1|||||PNPA004E  Transaction format error detected||";
    	exchange.getIn().setBody(msgInput);
    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });
    	assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String response = ((String) exchange.getIn().getBody()).split("\n")[3];
        assertEquals(expectedResponse, response);
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
    	exchange.getIn().setBody(msgInput);		
    	assertThrows(ValidationFailedException.class, () -> {
            v2PayloadValidator.validate(exchange);
        });
    	assertEquals(HttpStatus.SC_BAD_REQUEST, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
        String zca = ((String) exchange.getIn().getBody()).split("\n")[1];
        assertEquals(expectedZCA, zca);
        
        String zcb = ((String) exchange.getIn().getBody()).split("\n")[2];
        assertEquals(expectedZCB, zcb);
        
        String zzz = ((String) exchange.getIn().getBody()).split("\n")[3];
        assertEquals(expectedZZZ, zzz);
        
    }

    // Validate no exception is thrown
    @Test  
    public void testValidMessage() {
    	exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
    	exchange.getIn().setBody(SamplesToSend.msgR03);
    	try {
    		v2PayloadValidator.validate(exchange);
    	}catch(Exception e) {
    		fail("Not expecting exception ");
    	}
    }
}
