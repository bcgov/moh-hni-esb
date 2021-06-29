package ca.bc.gov.hlth.hnsecure.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.test.TestPropertiesLoader;
import net.minidev.json.parser.ParseException;

public class FhirPayloadExtractorTest extends TestPropertiesLoader {
	
	private static final String fhirJsonMsg = "{\"content\":[{\"attachment\":{\"data\":\"MDAwMDAzNTJNU0h8Xn5cJnxITldFQnxWSUhBfFJBSUdULVBSU04tRE1HUnxCQzAwMDAxMDEzfDIwMTcwMTI1MTIyMTI1fHRyYWluOTZ8UjAzfDIwMTcwMTI1MTIyMTI1fER8Mi40fHwKWkhEfDIwMTcwMTI1MTIyMTI1fF5eMDAwMDAwMTB8SE5BSUFETUlOSVNUUkFUSU9OfHx8fDIuNApQSUR8fDEyMzQ1Njc4OTBeXl5CQ15QSA==\",\"contentType\":\"x-application\\/hl7-v2+er7\"}}],\"resourceType\":\"DocumentReference\",\"status\":\"current\"}";
	
	private static final String invalidFhirJsonMsg = "{\"content\":[{\"attachment\":{\"data\":\"MDAwMDAzNTJNU0h8Xn5cJnxITldFQnxWSUhBfFJBSUdULVBSU04tRE1HUnxCQzAwMDAxMDEzfDIwMTcwMTI1MTIyMTI1fHRyYWluOTZ8UjAzfDIwMTcwMTI1MTIyMTI1fER8Mi40fHwKWkhEfDIwMTcwMTI1MTIyMTI1fF5eMDAwMDAwMTB8SE5BSUFETUlOSVNUUkFUSU9OfHx8fDIuNApQSUR8fDEyMzQ1Njc4OTBeXl5CQ15QSA=B=\",\"contentType\":\"x-application\\/hl7-v2+er7\"}}],\"resourceType\":\"DocumentReference\",\"status\":\"current\"}";
	
    private static final String v2Msg = "00000352MSH|^~\\&|HNWEB|VIHA|RAIGT-PRSN-DMGR|BC00001013|20170125122125|train96|R03|20170125122125|D|2.4||\n"
            + "ZHD|20170125122125|^^00000010|HNAIADMINISTRATION||||2.4\n"
            + "PID||1234567890^^^BC^PH";

    @Test
    public void testExtractFhirPayload_success() throws UnsupportedEncodingException, ParseException, CustomHNSException {
        String v2 = FhirPayloadExtractor.extractFhirPayload(exchange, fhirJsonMsg);
        assertEquals(v2Msg, v2);
    }
	
    @Test
    public void testExtractFhirPayload_nullPayload() {
        assertThrows(NullPointerException.class, () -> {
        	FhirPayloadExtractor.extractFhirPayload(exchange, null);
        });
    }
    
    @Test
    public void testExtractFhirPayload_invalidPayload() {
        CustomHNSException exception = assertThrows(CustomHNSException.class, () -> {
        	FhirPayloadExtractor.extractFhirPayload(exchange, invalidFhirJsonMsg);
        });

        assertEquals(ErrorMessage.CustomError_Msg_InvalidRequest, exception.getErrorMessage());
    }
    
}
