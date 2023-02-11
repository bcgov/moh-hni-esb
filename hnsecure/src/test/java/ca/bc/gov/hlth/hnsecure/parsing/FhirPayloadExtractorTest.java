package ca.bc.gov.hlth.hnsecure.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;
import ca.bc.gov.hlth.hnsecure.test.TestPropertiesLoader;
import net.minidev.json.parser.ParseException;

public class FhirPayloadExtractorTest extends TestPropertiesLoader {

    private static final String EXPECTED_DECODED_V2 = "MSH|^~\\&|HR|BC00000098|RAIGET-DOC-SUM|BC0003000|19991004103039|lharris|E45|19980915000015|D|2.3HDR|||TRAININGAdmin\r\n"
    		+ "SFT|1.0||testorg^^orgid^^^MOH|1.0|barebones||\r\n"
    		+ "QPD|E45^^HNET0003|1|^^00000001^^^CANBC^XX^MOH|^^00000001^^^CANBC^XX^MOH|^^00000754^^^CANBC^XX^MOH|9020198746^^^CANBC^JHN^MOH||19420112||||||19980601||PVC^^HNET9909||\r\n"
    		+ "RCP|I|";

    @Test
    public void testExtractFhirPayload_success() throws UnsupportedEncodingException, ParseException, CustomHNSException {
        FhirPayloadExtractor.extractFhirPayload(exchange, SamplesToSend.e45JsonMsg);
        assertEquals(EXPECTED_DECODED_V2, exchange.getIn().getBody());
        assertEquals("RAIGET-DOC-SUM", exchange.getProperty(Util.PROPERTY_RECEIVING_APP));
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
        	FhirPayloadExtractor.extractFhirPayload(exchange, SamplesToSend.invalidFhirJsonMsg);
        });
   
        assertEquals(ErrorMessage.CUSTOM_ERROR_INVALID_REQUEST, exception.getErrorMessage());
    }
    
}
