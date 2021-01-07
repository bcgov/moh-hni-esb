package ca.bc.gov.hlth.hnsecure.authorization;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

public class ValidateAccessToken implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ValidateAccessToken.class);

    AuthorizationProperties authorizationProperties;

    /**
     * Default Constructor
     */
    public ValidateAccessToken(AuthorizationProperties authorizationProperties) {
        this.authorizationProperties = authorizationProperties;
    }

    @Override
    public void process(Exchange exchange)
            throws Exception {

        AccessToken accessToken = AccessToken.parse(exchange.getIn().getHeader("Authorization").toString());
        logger.info(String.format("Access token: %s", accessToken));

        // Create a JWT processor for the access tokens
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier(new JOSEObjectType("JWT")));

        // The public RSA keys to validate the signatures
        // The RemoteJWKSet caches the retrieved keys to speed up subsequent look-ups
        // TODO this should be moved into the constructor to make use of the JWK caching
        // TODO url should be a config property
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(
                new URL("https://common-logon-dev.hlth.gov.bc.ca/auth/realms/v2_pos/protocol/openid-connect/certs"),
                // Overrides the DefaultResourceRetriever to up the timeouts to 5 seconds
                new DefaultResourceRetriever(5000, 5000, 51200)
        );

        // Configure the JWT processor with a key selector to feed matching public
        // RSA keys sourced from the JWK set URL
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        // Set the required JWT claims - these must all be available in the token payload
        jwtProcessor.setJWTClaimsSetVerifier(
                new CustomJWTClaimsVerifier(
                        // Accepted Audience -> aud
                        authorizationProperties.getAudiences(),
                        // Accepted Authorized Parties -> azp
                        authorizationProperties.getAuthorizedParties(),
                        // Accepted Scopes -> scope
                        authorizationProperties.getScopes(),
                        // Exact Match Claims -> iss
                        // TODO issuer should be a config property
                        new JWTClaimsSet.Builder()
                                .issuer("https://common-logon-dev.hlth.gov.bc.ca/auth/realms/v2_pos")
                                .build(),
                        // Required Claims -> azp, scope, iat, exp, jti
                        new HashSet<>(Arrays.asList("azp", "scope", "iat", "exp", "jti")),
                        // Prohibited Claims
                        null
                )
        );

        // Process the token
        JWTClaimsSet claimsSet = jwtProcessor.process(accessToken.toString(), null);

        // Print out the token claims set
        logger.info("TOKEN PAYLOAD: " + claimsSet.toJSONObject());
    }
}
