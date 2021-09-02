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
	SCOPES("scopes"),
	VALID_V2_MSG_TYPES("valid-v2-message-types"),
	ISSUER("issuer"),
	VALID_RECIEVING_FACILITY("valid-receiving-facility"),
	PROCESSING_DOMAIN("processing-domain"),
	VERSION("version"),
	CERTS_ENDPOINT("certs-endpoint"),
	PHARMANET_ENDPOINT("pharmanet-endpoint"),
	IS_FILEDDROPS_ENABLED("is-filedrops-enabled"),
	FILE_DROPS_LOCATION("file-drops-location"),
	MQ_HOST("mq.host"),
	MQ_PORT("mq.port"),
	MQ_QUEUEMANAGER("mq.queuemanager"),
	MQ_CHANNEL("mq.channel"),
	IS_AUDITS_ENABLED("audits.enabled"),
	JMB_REQUEST_QUEUE("jmb.request.queue"),
	JMB_REPLY_QUEUE("jmb.reply.queue"),
	HIBC_REQUEST_QUEUE("hibc.request.queue"),
	HIBC_REPLY_QUEUE("hibc.reply.queue");
	
	// Key should be same as the key in application.properties
	private String key;
	
	private ApplicationProperty(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

}
