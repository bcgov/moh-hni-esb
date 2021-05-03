package ca.bc.gov.hlth.hnsecure.authorization;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;

public class ValidateAccessTokenTest {

    private AuthorizationProperties authProps = new AuthorizationProperties(
            "account",
            "moh_hnclient_dev",
            "system/*.write",
            "r03, r07, r09",
            "https://common-logon-dev.hlth.gov.bc.ca/auth/realms/v2_pos",
            "",
            "D"
            ,"2.1");
    private String certsEndpoint = null;

    private String jwtInvalidAud;
    private String jwtInvalidAuthParties;
    private String jwtInvalidScopes;
    private String jwtInvalidIssuer;

    private final ValidateAccessToken validateAccessToken = new ValidateAccessToken();
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
