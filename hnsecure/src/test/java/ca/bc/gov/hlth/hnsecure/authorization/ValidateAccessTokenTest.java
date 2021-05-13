package ca.bc.gov.hlth.hnsecure.authorization;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;

public class ValidateAccessTokenTest {


    private String jwtInvalidAud;
    private String jwtInvalidAuthParties;
    private String jwtInvalidScopes;
    private String jwtInvalidIssuer;

    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());

    @Test
    public void invalidAudienceTest() {
        exchange.getIn().setHeader("Authorization", jwtInvalidAud);
    }

    @Test
    public void invalidAuthPartiesTest() {
        exchange.getIn().setHeader("Authorization", jwtInvalidAuthParties);
    }

    @Test
    public void invalidScopesTest() {
        exchange.getIn().setHeader("Authorization", jwtInvalidScopes);
    }

    @Test
    public void invalidIssuerTest() {
        exchange.getIn().setHeader("Authorization", jwtInvalidIssuer);
    }

}
