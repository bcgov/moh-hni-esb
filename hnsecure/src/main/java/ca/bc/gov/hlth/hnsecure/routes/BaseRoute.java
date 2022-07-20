package ca.bc.gov.hlth.hnsecure.routes;

import java.net.UnknownHostException;

import javax.jms.JMSException;

import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.http.conn.HttpHostConnectException;

import com.ibm.mq.headers.MQRFH2;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.exception.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.messagevalidation.ExceptionHandler;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;


public abstract class BaseRoute extends RouteBuilder {
	/**
	 * JMS Component for MQ end point with the option :
	 * 1.allowAdditionalHeaders: Allows additional headers which may have values that are invalid according to JMS specification
	 * 2.requestTimeout: The timeout for waiting for a reply when using the InOut Exchange Pattern (in milliseconds).
	 * 3.timeToLive: When sending messages, specifies the time-to-live of the message (in milliseconds).
	 * 4.targetClient: Removes {@link MQRFH2} header.
	 * 5.mdWriteEnabled: permits editing MQMD field on destination.
	 * Refer: https://camel.apache.org/components/3.11.x/jms-component.html
	 */
	protected static final String JMS_DESTINATION_NAME_FORMAT = "queue:///%s?targetClient=1&&mdWriteEnabled=true";
	
	protected static final String MQ_URL_FORMAT = "jmsComponent:queue:%s?exchangePattern=InOut&replyTo=queue:///%s&replyToType=Shared&allowAdditionalHeaders=JMS_IBM_MQMD_MsgId&requestTimeout=7000&explicitQosEnabled=true&timeToLive=7000";
	protected static final ApplicationProperties properties = ApplicationProperties.getInstance(); 

	@SuppressWarnings("unchecked")
	protected void handleExceptions() {
    	onException(CustomHNSException.class, HttpHostConnectException.class, HttpOperationFailedException.class, UnknownHostException.class, JMSException.class, ExchangeTimedOutException.class)
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
