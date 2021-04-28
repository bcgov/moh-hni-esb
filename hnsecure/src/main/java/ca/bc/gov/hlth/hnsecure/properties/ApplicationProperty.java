package ca.bc.gov.hlth.hnsecure.properties;

import lombok.Getter;

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
	HIBC_R50_ENDPOINT("hibc-r50-endpoint")
	;

	@Getter
	private String key;
	
	private ApplicationProperty(String key) {
		this.key = key;
	}

}
