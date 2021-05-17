package ca.bc.gov.hlth.hnsecure.validation;


import static org.junit.Assert.fail;

import java.net.MalformedURLException;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.test.TestPropertiesLoader;

public class TokenValidatorTest extends TestPropertiesLoader {


    private String jwtInvalidScopes;
    private String jwtInvalidIssuer;

    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());

    @Test
    public void initialiazionSuccesful() {
        try {
			new TokenValidator(new ValidatorImpl());
		} catch (MalformedURLException e) {
			fail("Not expecting exception");
		}
    }

    /**
     * If Authorization header is not set, expect custom exception
     */
    @Test
    public void blankAuthorizationHeader() {
        exchange.getIn().setHeader("Authorization", null);
        try {
        	Validator validator = new TokenValidator(new ValidatorImpl());
        	validator.validate(exchange);
        	fail("Expecting CustomHNSException exception");
        }catch(CustomHNSException e) {
        	// Do nothing as expecting this exception
        } catch (Exception e) {
        	fail("Expecting CustomHNSException exception");
		}
        
    }

    @Test
    public void invalidScopesTest() {
        exchange.getIn().setHeader("Authorization", jwtInvalidScopes);
    }

    @Test
    public void invalidIssuerTest() {
        exchange.getIn().setHeader("Authorization", jwtInvalidIssuer);
    }
    
    @Test
    public void invalidTokenTest() {
        exchange.getIn().setHeader("Authorization", "jwtInvalidToken");
    }
    
    @Test
    public void validTokenTest() {
        exchange.getIn().setHeader("Authorization", "jwtValidToken");
    }
    

}
