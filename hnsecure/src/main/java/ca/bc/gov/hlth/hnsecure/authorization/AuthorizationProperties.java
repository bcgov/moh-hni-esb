package ca.bc.gov.hlth.hnsecure.authorization;

import java.util.*;

public class AuthorizationProperties {

    private Set<String> audiences;
    private Set<String> authorizedParties;
    private Set<String> scopes;
    private Set<String> validV2MessageTypes;

    public AuthorizationProperties(String audiences, String authorizedParties, String scopes, String validV2MessageTypes) {
        this.audiences = getPropertyAsSet(audiences);
        this.authorizedParties = getPropertyAsSet(authorizedParties);
        this.scopes = getPropertyAsSet(scopes);
        this.validV2MessageTypes = getPropertyAsSet(validV2MessageTypes);
    }

    /**
     * Return a list of values from a comma delimited property
     *
     * @param commaDelimitedProperties a String of comma delimited values
     * @return List
     */
    private List<String> getPropertyAsList(String commaDelimitedProperties) {
        List<String> propertyList = Collections.emptyList();
        if (commaDelimitedProperties != null && !commaDelimitedProperties.isBlank()) {
            propertyList = Arrays.asList(commaDelimitedProperties.split("\\s*,\\s*"));
        }
        return propertyList;
    }

    /**
     * Return a set of values from a comma delimited property
     *
     * @param commaDelimitedProperties a String of comma delimited values
     * @return Set
     */
    private Set<String> getPropertyAsSet(String commaDelimitedProperties) {
        return new HashSet<>(getPropertyAsList(commaDelimitedProperties));
    }

    public Set<String> getAudiences() {
        return audiences;
    }

    public Set<String> getAuthorizedParties() {
        return authorizedParties;
    }

    public Set<String> getScopes() {
        return scopes;
    }
    public Set<String> getValidV2MessageTypes() {
        return validV2MessageTypes;
    }
}
