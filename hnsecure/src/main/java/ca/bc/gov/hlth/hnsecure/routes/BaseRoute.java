package ca.bc.gov.hlth.hnsecure.routes;

import java.net.UnknownHostException;

import javax.jms.JMSException;

import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.conn.HttpHostConnectException;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.exception.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.messagevalidation.ExceptionHandler;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;

public abstract class BaseRoute extends RouteBuilder {
	protected static final String JMS_DESTINATION_NAME_FORMAT = "queue:///%s?targetClient=1&&mdWriteEnabled=true";
	
	protected static final String MQ_URL_FORMAT = "jmsComponent:queue:%s?exchangePattern=InOut&replyTo=queue:///%s&replyToType=Exclusive&allowAdditionalHeaders=JMS_IBM_MQMD_MsgId";
	
	protected static final ApplicationProperties properties = ApplicationProperties.getInstance(); 

	@SuppressWarnings("unchecked")
	protected void handleExceptions() {
    	onException(CustomHNSException.class, HttpHostConnectException.class, UnknownHostException.class, JMSException.class, ExchangeTimedOutException.class)
	    	.process(new ExceptionHandler())
	    	.handled(true)
	    	.to("direct:handleResponse");
	    
		onException(ValidationFailedException.class)
			.id("ValidationException")
			.log("Validation exception response: ${body}")
			.handled(true)
			.to("direct:handleResponse");
	}

}
