package ca.bc.gov.hlth.hnsecure.authorization;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;


public class AuthorizationProperties {
//TODO: This class will be removed with Application properties
//TODO: Methhod will be moved to Util class
    private final Set<String> audiences;
    private final Set<String> authorizedParties;
    private final Set<String> scopes;
    private final Set<String> validV2MessageTypes;
    private final String issuer;
    private final String processingDomain;
    private final String version;
    private final Set<String> validReceivingFacility;

    public AuthorizationProperties(String audiences, String authorizedParties, String scopes, String validV2MessageTypes,
                                   String issuer, String validReceivingFacility,String processingDomain, String version) {
        this.audiences = null;
		//this.audiences = getPropertyAsSet(audiences);
        this.authorizedParties = getPropertyAsSet(authorizedParties);
        this.scopes = getPropertyAsSet(scopes);
        this.validV2MessageTypes = getPropertyAsSet(validV2MessageTypes);
        this.issuer = issuer;
        this.validReceivingFacility = getPropertyAsSet(validReceivingFacility);
        this.processingDomain = processingDomain;
        this.version = version;
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
        //return audiences;
    	String audience = ApplicationProperties.getInstance().getValue(ApplicationProperty.AUDIENCE);
    	return getPropertyAsSet(audience);
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

    public String getIssuer() { return issuer; }

	public Set<String> getValidReceivingFacility() {
		return validReceivingFacility;
	}

	public String getProcessingDomain() {
		return processingDomain;
	}

	public String getVersion() {
		return version;
	}
}
