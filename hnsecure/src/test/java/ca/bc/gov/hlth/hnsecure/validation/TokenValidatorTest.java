package ca.bc.gov.hlth.hnsecure.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.net.MalformedURLException;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.message.ErrorMessage;
import ca.bc.gov.hlth.hnsecure.test.TestPropertiesLoader;

public class TokenValidatorTest extends TestPropertiesLoader {

    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());

    @Test
    public void testConstructor() throws MalformedURLException {
    	Validator validator = new TokenValidator(new ValidatorImpl());
    	assertNotNull(validator);
    }

    /**
     * If Authorization header is not set, expect custom exception
     */
    @Test
    public void testValidate_blankAuthorizationHeader() {
        exchange.getIn().setHeader("Authorization", null);
        CustomHNSException exception = 
        	assertThrows(CustomHNSException.class, () -> {
            	Validator validator = new TokenValidator(new ValidatorImpl());
            	validator.validate(exchange);        		
        	});
        assertEquals(ErrorMessage.CUSTOM_ERROR_MISSING_AUTH_KEY, exception.getErrorMessage());
    }
    
    /**
     * If Authorization header is an invalid token, expect custom exception
     */
    @Test
    public void testValidate_invalidAuthorizationHeader() {
        exchange.getIn().setHeader("Authorization", "asdfasdfasdf");
        CustomHNSException exception = 
        	assertThrows(CustomHNSException.class, () -> {
            	Validator validator = new TokenValidator(new ValidatorImpl());
            	validator.validate(exchange);        		
        	});
        assertEquals(ErrorMessage.CUSTOM_ERROR_INVALID_AUTH_KEY, exception.getErrorMessage());
    }


}
