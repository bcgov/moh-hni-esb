package ca.bc.gov.hlth.hnsecure.authorization;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CustomJWTClaimsVerifier<C extends SecurityContext> extends DefaultJWTClaimsVerifier<C> {

    /**
     * The accepted authorized party values, {@code null} if not specified. A {@code null} value present in the set
     * allows JWTs with no authorized parties.
     */
    private final Set<String> acceptedAuthorizedPartyValues;

    /**
     * The accepted scope values, {@code null} if not specified. A {@code null} value present in the set allows JWTs
     * with no scopes.
     */
    private final Set<String> acceptedScopeValues;

    /**
     * Creates new default JWT claims verifier.
     *
     * @param acceptedAudience The accepted JWT audience values, {@code null} if not specified. A {@code null} value in
     * the set allows JWTs with no audience.
     * @param acceptedAuthorizedParties The accepted JWT authorized party values, {@code null} if not specified. A
     * {@code null} value in the set allows JWTs with no authorized parties.
     * @param acceptedScopes The accepted JWT scope values, {@code null} if not specified. A {@code null} value in the
     * set allows JWTs with no scopes.
     * @param exactMatchClaims The JWT claims that must match exactly, {@code null} if none.
     * @param requiredClaims The names of the JWT claims that must be present, empty set or {@code null} if none.
     * @param prohibitedClaims The names of the JWT claims that must not be present, empty set or {@code null} if none.
     */
    public CustomJWTClaimsVerifier(
            final Set<String> acceptedAudience,
            final Set<String> acceptedAuthorizedParties,
            final Set<String> acceptedScopes,
            final JWTClaimsSet exactMatchClaims,
            final Set<String> requiredClaims,
            final Set<String> prohibitedClaims) {

        super(acceptedAudience, exactMatchClaims, requiredClaims, prohibitedClaims);

        this.acceptedAuthorizedPartyValues
                = acceptedAuthorizedParties != null ? Collections.unmodifiableSet(acceptedAuthorizedParties) : null;

        this.acceptedScopeValues = acceptedScopes != null ? Collections.unmodifiableSet(acceptedScopes) : null;
    }

    @Override
    public void verify(final JWTClaimsSet claimsSet) throws BadJWTException {
        verify(claimsSet, null);
    }

    @Override
    public void verify(final JWTClaimsSet claimsSet, final C context) throws BadJWTException {
        super.verify(claimsSet, context);
        verifyAzpClaim(claimsSet);
        verifyScopeClaim(claimsSet);
    }

    /**
     * Verify an azp claim against an accepted list of azp values
     *
     * @param claimsSet
     */
    private void verifyAzpClaim(final JWTClaimsSet claimsSet) throws BadJWTException {
        String azp = "azp";
        String azpValues = (String) claimsSet.getClaim(azp);
        verifyClaim(azp, azpValues, acceptedAuthorizedPartyValues);
    }

    /**
     * Verify a scope claim against an accepted list of scope values
     *
     * @param claimsSet
     */
    private void verifyScopeClaim(final JWTClaimsSet claimsSet) throws BadJWTException {
        String scope = "scope";
        String scopeValues = (String) claimsSet.getClaim(scope);
        verifyClaim(scope, scopeValues, acceptedScopeValues);
    }

    /**
     * Verify a claim against an accepted list of claim values
     *
     * @param claimName
     * @param claimValues
     * @param acceptedClaimValues
     */
    private void verifyClaim(String claimName, String claimValues, Set<String> acceptedClaimValues)
            throws BadJWTException {

        // Check claim
        if (acceptedClaimValues != null) {
            List<String> claimList = Arrays.asList(claimValues.split(" "));
            if (claimList != null && !claimList.isEmpty()) {
                boolean claimMatch = false;
                for (String claim : claimList) {
                    if (acceptedClaimValues.contains(claim)) {
                        claimMatch = true;
                        break;
                    }
                }
                if (!claimMatch) {
                    throw new BadJWTException("JWT " + claimName + " rejected: " + claimList);
                }
            } else if (!acceptedClaimValues.contains(null)) {
                throw new BadJWTException("JWT missing required " + claimName);
            }
        }
    }
}
