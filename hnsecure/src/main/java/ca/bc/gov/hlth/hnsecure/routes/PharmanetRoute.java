package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_CERT;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_CERT_PASSWORD;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_PASSWORD;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_URI;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_USER;
import static org.apache.camel.component.http.HttpMethods.POST;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.jsse.SSLContextParameters;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.json.Base64Encoder;
import ca.bc.gov.hlth.hnsecure.json.pharmanet.ProcessV2ToPharmaNetJson;

import ca.bc.gov.hlth.hnsecure.parsing.PharmaNetPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.FormatRTransMessage;
import ca.bc.gov.hlth.hnsecure.parsing.FormatTRPRequestMessage;

public class PharmanetRoute extends BaseRoute {

	private static final String CAMEL_HTTP_METHOD = "CamelHttpMethod";
    
    private static final String SSL_CONTEXT_PHARMANET = "ssl_context_pharmanet";

	@Override
	public void configure() throws Exception {
		setupSSLContextPharmanetRegistry(getContext());

		String pharmanetUrl = String.format(//NOSONAR S3457. Reading from properties.)
				properties.getValue(PHARMANET_URI) + "?bridgeEndpoint=true&sslContextParameters=#%s&authMethod=Basic&authUsername=%s&authPassword=%s", //NOSONAR
				SSL_CONTEXT_PHARMANET, properties.getValue(PHARMANET_USER), properties.getValue(PHARMANET_PASSWORD)); //N0SONAR
						
		String basicToken = RouteUtils.buildBasicAuthToken(properties.getValue(PHARMANET_USER), properties.getValue(PHARMANET_PASSWORD));

		handleExceptions();
		
		from("direct:pharmanet").routeId("pharmanet-route")
	    	.log("Message identified as PharmaNet message. Preparing message for PharmaNet.")
	    	.setBody().method(new FormatTRPRequestMessage()).id("FormatTRPRequestMessage")
	    	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	    	.wireTap("direct:audit").end()
			.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")
			.setBody(body().regexReplaceAll("\r\n","\r"))
	        .setBody().method(new Base64Encoder())
	        .setBody().method(new ProcessV2ToPharmaNetJson()).id("ProcessV2ToPharmaNetJson")
	        .log("Sending to Pharmanet")
	        .removeHeader(Exchange.HTTP_URI) //clean this header as it has been set in the "from" section
	        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
	        .setHeader(CAMEL_HTTP_METHOD, POST)
	        .setHeader(AUTHORIZATION, simple(basicToken))
	        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
	        .to(pharmanetUrl).id("ToPharmaNet")
	        .log("Received response from Pharmanet:${headers}")
	        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
	        .process(new PharmaNetPayloadExtractor())
	        .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	        .wireTap("direct:audit").end();
	}

	private void setupSSLContextPharmanetRegistry(CamelContext camelContext) {
		SSLContextParameters sslContextParameters = RouteUtils.setupSslContextParameters(properties.getValue(PHARMANET_CERT), properties.getValue(PHARMANET_CERT_PASSWORD));

        Registry registry = camelContext.getRegistry();
        registry.bind(SSL_CONTEXT_PHARMANET, sslContextParameters);
	}

}
