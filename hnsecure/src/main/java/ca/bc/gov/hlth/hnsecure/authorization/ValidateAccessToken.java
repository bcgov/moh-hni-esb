package ca.bc.gov.hlth.hnsecure.authorization;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CustomError_Msg_InvalidAuthKey;

public class ValidateAccessToken implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ValidateAccessToken.class);

    private AuthorizationProperties authorizationProperties;
    private String certsEndpoint;
    private static String AUTH_HEADER_KEY = "Authorization";

    /**
     * Default Constructor
     */
    public ValidateAccessToken(AuthorizationProperties authorizationProperties, String certsEndpoint) {
        this.authorizationProperties = authorizationProperties;
        this.certsEndpoint = certsEndpoint;
    }

    @Override
    public void process(Exchange exchange)
            throws Exception {
    	String methodName = "process";
    	String authorizationKey = (String) exchange.getIn().getHeader(AUTH_HEADER_KEY);
    	if(authorizationKey==null) {
    		logger.info("{} - No authorization key passed in request header.", methodName);
            throw new CustomHNSException(CustomError_Msg_InvalidAuthKey.getErrorMessage());
    	}
        AccessToken accessToken = AccessToken.parse(authorizationKey);
        logger.info("{} - Access token: {}", methodName,accessToken);

        // Create a JWT processor for the access tokens
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier(new JOSEObjectType("JWT")));

        // The public RSA keys to validate the signatures
        // The RemoteJWKSet caches the retrieved keys to speed up subsequent look-ups
        // TODO this should be moved into the constructor to make use of the JWK caching
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(
                new URL(certsEndpoint),
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
                        new JWTClaimsSet.Builder()
                                .issuer(authorizationProperties.getIssuer())
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
