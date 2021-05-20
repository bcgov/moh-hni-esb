package ca.bc.gov.hlth.hnsecure.validation;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CustomError_Msg_InvalidAuthKey;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.AUDIENCE;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.AUTHORIZED_PARTIES;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.CERTS_ENDPOINT;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.ISSUER;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.SCOPES;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
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

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.authorization.CustomJWTClaimsVerifier;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;


/**
 * This class validate the token passed in the request using JSON Web Token processor
 * JWT initialization is done in the constructor. 
 * Our custom JWT claims verifier is used to validate access token against the Keycloak endpoint (provided in application properties). 
 * @author pankaj.kathuria
 *
 */
public class TokenValidator extends AbstractValidator {
	private static final Logger logger = LoggerFactory.getLogger(TokenValidator.class);
	private static final String OBJECT_TYPE_JWT = "JWT";
	private ApplicationProperties properties = ApplicationProperties.getInstance();
	
	private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

	
	private Validator validator;
	
	
	public TokenValidator(Validator validator) throws MalformedURLException {
		super();
		this.validator = validator;
		jwtProcessor = initJwtProcessor();
	}

	@Override
	public boolean validate(Exchange exchange) throws Exception {
		logger.info("TokenValidator validation started");

		String methodName = LoggingUtil.getMethodName();
		
		// If more validataion is required for exchange message, we should create a new bean
		String authorizationKey = (String) exchange.getIn().getHeader(AUTHORIZATION);
		if(StringUtils.isBlank(authorizationKey)) {
			logger.info("{} - TransactionId: {}, No authorization key passed in request header.", methodName, exchange.getIn().getMessageId());
			throw new CustomHNSException(CustomError_Msg_InvalidAuthKey.getErrorMessage());
		}
		AccessToken accessToken = AccessToken.parse(authorizationKey);
		logger.info("{} - TransactionId: {}, Access token: {}", methodName,exchange.getIn().getMessageId(),accessToken);

		// Process the token
		JWTClaimsSet claimsSet = jwtProcessor.process(accessToken.toString(), null);

		// Print out the token claims set
		logger.info("{} - TransactionId: {}, TOKEN PAYLOAD: {}", methodName, exchange.getIn().getMessageId(), claimsSet.toJSONObject());
	
		logger.info("TokenValidator Validation completed");
		// After token call the  other validations. Type of validation depends on wrapped class when initializing
		validator.validate(exchange);
	
		return true;
	}
	
	/**
	 * This method configures the JSON Web token processor. 
	 * This token processor will validate the access token passed in the request 
	 * @throws MalformedURLException 
	 */
	protected ConfigurableJWTProcessor<SecurityContext> initJwtProcessor() throws MalformedURLException {
		String methodName = LoggingUtil.getMethodName();
		logger.info("{} - Loading JWT processor started.",methodName);
		// Create a JWT processor for the access tokens
		jwtProcessor = new DefaultJWTProcessor<>();
		jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType(OBJECT_TYPE_JWT)));
		
		String certEndpoints = properties.getValue(CERTS_ENDPOINT);

		// The public RSA keys to validate the signatures
		// The RemoteJWKSet caches the retrieved keys to speed up subsequent look-ups
		JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
		JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(
				new URL(certEndpoints),
				// Overrides the DefaultResourceRetriever to up the timeouts to 5 seconds
				new DefaultResourceRetriever(5000, 5000, 51200)
				);
		
		// Configure the JWT processor with a key selector to feed matching public
		// RSA keys sourced from the JWK set URL
		JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);
		jwtProcessor.setJWSKeySelector(keySelector);
		

        Set<String> audiences =  Util.getPropertyAsSet(properties.getValue(AUDIENCE));
    	Set<String> authorizedParties = Util.getPropertyAsSet(properties.getValue(AUTHORIZED_PARTIES));
    	Set <String> scopes = Util.getPropertyAsSet(properties.getValue(SCOPES));

		// Set the required JWT claims - these must all be available in the token payload
		jwtProcessor.setJWTClaimsSetVerifier(
				new CustomJWTClaimsVerifier<>(
						// Accepted Audience -> aud
						audiences,
						// Accepted Authorized Parties -> azp
						authorizedParties,
						// Accepted Scopes -> scope
						scopes,
						// Exact Match Claims -> iss
						new JWTClaimsSet.Builder()
						.issuer(properties.getValue(ISSUER))
						.build(),
						// Required Claims -> azp, scope, iat, exp, jti
						new HashSet<>(Arrays.asList("azp", "scope", "iat", "exp", "jti")),
						// Prohibited Claims
						null
						)
				);
		logger.info("{} - Loading JWT processor completed.",methodName);
		return jwtProcessor;
	}



}
