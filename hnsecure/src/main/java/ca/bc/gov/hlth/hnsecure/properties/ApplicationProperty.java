package ca.bc.gov.hlth.hnsecure.properties;

/**
 * Enum to map all the properties from application.properties 
 * @author pankaj.kathuria
 *
 */
public enum ApplicationProperty {
	
	CAMEL_MAIN_NAME("camel.main.name"),
	HOSTNAME("hostname"), 
	PORT("port"),
	ENDPOINT("endpoint"),
	AUDIENCE("audience"),
	AUTHORIZED_PARTIES("authorized-parties"),
	SCOPES("scopes"),
	VALID_V2_MSG_TYPES("valid-v2-message-types"),
	ISSUER("issuer"),
	VALID_RECIEVING_FACILITY("valid-receiving-facility"),
	PROCESSING_DOMAIN("processing-domain"),
	VERSION("version"),
	CERTS_ENDPOINT("certs-endpoint"),
	PHARMANET_ENDPOINT("pharmanet-endpoint"),
	HIBC_E45_ENDPOINT("hibc-e45-endpoint"),
	HIBC_R15_ENDPOINT("hibc-r15-endpoint"),
	HIBC_R50_ENDPOINT("hibc-r50-endpoint"),
	IS_FILEDDROPS_ENABLED("is-filedrops-enabled"),
	FILE_DROPS_LOCATION("file-drops-location"),
	IS_AUDITS_ENABLED("audits.enabled");
	
	// Key should be same as the key in application.properties
	private String key;
	
	private ApplicationProperty(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

}
