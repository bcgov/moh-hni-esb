package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.message.ErrorMessage.CUSTOM_ERROR_MQ_NOT_ENABLED;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.*;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_CERT_PASSWORD;
import static org.apache.camel.component.http.HttpMethods.POST;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.json.Base64Encoder;
import ca.bc.gov.hlth.hnsecure.json.fhir.ProcessV2ToJson;
import ca.bc.gov.hlth.hnsecure.parsing.FhirPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateJMSMessageHeader;
import ca.bc.gov.hlth.hnsecure.parsing.ProtocolEvaluator;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.http.conn.ssl.NoopHostnameVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HIBCRoute extends BaseRoute {

	private static final String DIRECT_AUDIT = "direct:audit";
	private static final String DIRECT_HIBC_MQ = "direct:hibcMQ";
	private static final String DIRECT_HIBC_HTTP = "direct:hibcHTTP";

	private static final String KEY_STORE_TYPE_PKCS12 = "PKCS12";

	private static final String SSL_CONTEXT_HIBC = "ssl_context_hibc";

	private static final String SSL2 = "ssl2";

	private static final String BASIC = "Basic ";

	private static final String CAMEL_HTTP_METHOD = "CamelHttpMethod";

	@Override
	public void configure() throws Exception {

		setupSSLContextHibcRegistry(getContext());

		String hibcHttpUrl = String.format(
				properties.getValue(HIBC_HTTP_URI) + "?bridgeEndpoint=true&sslContextParameters=#%s&authMethod=Basic&authUsername=%s&authPassword=%s",
				SSL_CONTEXT_HIBC, properties.getValue(HIBC_USER), properties.getValue(HIBC_PASSWORD));
		String basicToken = buildBasicToken(properties.getValue(HIBC_USER), properties.getValue(HIBC_PASSWORD));

		boolean isMQEnabled = Boolean.parseBoolean(properties.getValue(IS_MQ_ENABLED));		
		String hibcRequestQueue = properties.getValue(HIBC_REQUEST_QUEUE);
		String hibcReplyQueue = properties.getValue(HIBC_REPLY_QUEUE);
		String hibcMqUrl = String.format(MQ_URL_FORMAT, hibcRequestQueue, hibcReplyQueue);

		handleExceptions();

		from("direct:hibc").routeId("hibc-route")
			.process(new ProtocolEvaluator()).id("ProtocolEvaluator")
			.choice()
				.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_HTTP))
					.to(DIRECT_HIBC_HTTP)
				.when(exchangeProperty(Util.PROPERTY_MESSAGE_PROTOCOL).isEqualTo(Util.PROTOCOL_MQ))
					.to(DIRECT_HIBC_MQ)
				.otherwise()
					.log("Protocol for HIBC message type ${exchangeProperty.messageType} not found or not valid. Defaulting to MQ")
					.to(DIRECT_HIBC_MQ)
			.end();

		// XXX This is currently just a simple HTTP route which will need additional configuration (auth, request/response conversion)
		// once the endpoint is available
		from(DIRECT_HIBC_HTTP).routeId("hibc-http-route")
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	     	.wireTap(DIRECT_AUDIT).end()
	     	.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
	        .setHeader(CAMEL_HTTP_METHOD, POST)
			.setHeader(AUTHORIZATION, simple(basicToken))
	     	.to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
	     	.to(hibcHttpUrl).id("ToHibcHttpUrl")
			.to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
	     	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	     	.wireTap(DIRECT_AUDIT).end();

		if (isMQEnabled) {
			from(DIRECT_HIBC_MQ).routeId("hibc-mq-route")
		        .log(String.format("Processing HIBC messages. Request Queue : %s, ReplyQ: %s", hibcRequestQueue, hibcReplyQueue))
		        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
		        .bean(new PopulateJMSMessageHeader()).id("PopulateJMSMessageHeaderHIBC")
				.log("HIBC request message ::: ${body}")
				.setHeader("CamelJmsDestinationName", constant(String.format(JMS_DESTINATION_NAME_FORMAT, hibcRequestQueue)))	           		        	
		    	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
		    	.wireTap(DIRECT_AUDIT).end()
				.to(hibcMqUrl).id("ToHibcMqUrl")
				.removeHeaders("JMS*")	
		        .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
		        .wireTap(DIRECT_AUDIT).end()
		        .log("Received response message from HIBC queue ::: ${body}");
		} else {
			from(DIRECT_HIBC_MQ).routeId("hibc-mq-route")
	    	.log("MQ routes are disabled.")
	    	.throwException(new CustomHNSException(CUSTOM_ERROR_MQ_NOT_ENABLED));
		}
	}

	private void setupSSLContextHibcRegistry(CamelContext camelContext) {
		KeyStoreParameters ksp = new KeyStoreParameters();
		ksp.setResource(properties.getValue(HIBC_CERT));
		ksp.setPassword(properties.getValue(HIBC_CERT_PASSWORD));
		ksp.setType(KEY_STORE_TYPE_PKCS12);

		KeyManagersParameters kmp = new KeyManagersParameters();
		kmp.setKeyStore(ksp);
		kmp.setKeyPassword(properties.getValue(HIBC_CERT_PASSWORD));

		SSLContextParameters sslContextParameters = new SSLContextParameters();
		sslContextParameters.setKeyManagers(kmp);

		HttpComponent httpComponent = camelContext.getComponent("https", HttpComponent.class);
		//This is important to make your cert skip CN/Hostname checks
		httpComponent.setX509HostnameVerifier(new NoopHostnameVerifier());

		Registry registry = camelContext.getRegistry();
		registry.bind(SSL_CONTEXT_HIBC, sslContextParameters);
		registry.bind(SSL2, new SSLContextParameters()); // If there is only one bound SSL context then Camel will default to always use it in every URL. So as a workaround to stop this a default new empty context is added here.
	}

	private String buildBasicToken(String username, String password) {
		String usernamePassword = username + ":" + password;
		String token = new String(Base64.getEncoder().encode(usernamePassword.getBytes(StandardCharsets.UTF_8)));

		return BASIC + token;
	}
	
}