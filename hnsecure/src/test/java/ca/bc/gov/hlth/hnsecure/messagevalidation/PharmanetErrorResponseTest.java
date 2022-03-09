package ca.bc.gov.hlth.hnsecure.messagevalidation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.message.PharmanetErrorResponse;

public class PharmanetErrorResponseTest {
	private final PharmanetErrorResponse  errorResponse = new PharmanetErrorResponse();

	   @Test
	    public void test_buildZCA() {	   
	    String expectedZCA = "ZCA|||50|||\n";
	    String zca = errorResponse.buildZCA();
	    assertEquals(zca,expectedZCA); 
   
	    }
	   
	   
	   @Test
	    public void test_buildZCB() {	  
	    String expectedZCB = "ZCB|||\n";
	    String zcb = errorResponse.buildZCB();
	    assertEquals(zcb,expectedZCB); 
  
	    }
	   
	   
	   @Test
	    public void test_buildZZZ() {
	    String expectedZZZ = "ZZZ||1|||||VLDT014E  The Supplied HL7 Message was improperly formatted||";
	    String zzz = errorResponse.buildZZZ(ErrorMessage.HL7_ERROR_INVALID_FORMAT);
	    assertEquals(zzz,expectedZZZ); 
 
	    }
}
