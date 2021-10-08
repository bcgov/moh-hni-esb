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

	//JMB Properties
	JMB_REQUEST_QUEUE("jmb.request.queue"),
	JMB_REPLY_QUEUE("jmb.reply.queue"),
	JMB_HTTP_URI("jmb.http.uri"),

	//Hibc properties
	HIBC_REQUEST_QUEUE("hibc.request.queue"),
	HIBC_REPLY_QUEUE("hibc.reply.queue"),
	HIBC_HTTP_URI("hibc.http.uri"),

	//MQ Properties
	MQ_HOST("mq.host"),
	MQ_CHANNEL("mq.channel"),
	MQ_PORT("mq.port"),
	MQ_QUEUEMANAGER("mq.queuemanager"); 
	
	// Key should be same as the key in application.properties
	private String key;
	
	private ApplicationProperty(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

}
