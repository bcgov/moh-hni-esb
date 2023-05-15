package ca.bc.gov.hlth.hnsecure.properties;

/**
 * Enum to map all the properties from application.properties 
 *
 */
public enum ApplicationProperty {
	
	CAMEL_MAIN_NAME("camel.main.name"),
	HOSTNAME("hostname"), 
	PORT("port"),
	ENDPOINT("endpoint"),
	AUDIENCE("audience"),
	SCOPES("scopes"),
	ISSUER("issuer"),
	VALID_RECIEVING_FACILITY("valid.receiving.facility"),
	PROCESSING_DOMAIN("processing.domain"),
	VERSION("version"),
	CERTS_ENDPOINT("certs.endpoint"),
	IS_FILEDDROPS_ENABLED("is.filedrops.enabled"),
	FILE_DROPS_LOCATION("file.drops.location"),
	//File Drop rotation properties
	FILE_DROPS_ROTATION_CRON("file.drops.rotation.cron"),
	FILE_DROPS_ROTATION_DELETE_AFTER("file.drops.rotation.delete.after"),
	
	IS_AUDITS_ENABLED("audits.enabled"),
	IS_MQ_ENABLED("mq.enabled"),
	
	//Pharmanet properties
	PHARMANET_URI("pharmanet.uri"),
	PHARMANET_CERT_PASSWORD("pharmanet.cert.password"),
	PHARMANET_USER("pharmanet.user"),
	PHARMANET_PASSWORD("pharmanet.password"),
	PHARMANET_CERT("pharmanet.cert"),

	//Database properties
	DATABASE_HOST("database.host"),
	DATABASE_PORT("database.port"),
	DATABASE_NAME("database.name"),
	DATABASE_USERNAME("database.username"),
	DATABASE_PASSWORD("database.password"),
	DATABASE_SCHEMA("database.schema"),

	//JMB Properties
	JMB_REQUEST_QUEUE("jmb.request.queue"),
	JMB_REPLY_QUEUE("jmb.reply.queue"),
	JMB_HTTP_URI("jmb.http.uri"),
	
	RAPID_USER("rapid.user"),
	RAPID_PASSWORD("rapid.password"),
	RAPID_HTTP_URI("rapid.http.uri"),
	RAPID_CERT("rapid.cert"),
	RAPID_CERT_PASSWORD("rapid.cert.password"),

	//HIBC MQ properties
	HIBC_REQUEST_QUEUE("hibc.request.queue"),
	HIBC_REPLY_QUEUE("hibc.reply.queue"),
	
	//HIBC HTTP properties
	HIBC_HTTP_URI("hibc.http.uri"),
	HIBC_CERT("hibc.cert"),
	HIBC_CERT_PASSWORD("hibc.cert.password"),
	//Path and basic auth for R15 and E45 endpoint
	HIBC_ELIGIBILITY_PATH("hibc.eligibility.path"),
	HIBC_ELIGIBILITY_USER("hibc.eligibility.user"),
	HIBC_ELIGIBILITY_PASSWORD("hibc.eligibility.password"),
	//Path and basic auth for R50 endpoint
	HIBC_ENROLLMENT_PATH("hibc.enrollment.path"),
	HIBC_ENROLLMENT_USER("hibc.enrollment.user"),
	HIBC_ENROLLMENT_PASSWORD("hibc.enrollment.password"),

	//MQ Properties
	MQ_HOST("mq.host"),
	MQ_CHANNEL("mq.channel"),
	MQ_PORT("mq.port"),
	MQ_QUEUEMANAGER("mq.queuemanager"),
	
	//RTrans URI
	RTRANS_URI("rtrans.uri");
	
	// Key should be same as the key in application.properties
	private String key;
	
	private ApplicationProperty(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

}
