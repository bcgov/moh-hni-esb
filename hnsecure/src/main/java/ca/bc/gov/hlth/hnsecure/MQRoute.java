package ca.bc.gov.hlth.hnsecure;

public interface MQRoute {

	public static final String JMS_DESTINATION_NAME_FORMAT = "queue:///%s?targetClient=1&&mdWriteEnabled=true";
	
	public static final String MQ_URL_FORMAT = "jmsComponent:queue:%s?exchangePattern=InOut&replyTo=queue:///%s&replyToType=Exclusive&allowAdditionalHeaders=JMS_IBM_MQMD_MsgId";

}
