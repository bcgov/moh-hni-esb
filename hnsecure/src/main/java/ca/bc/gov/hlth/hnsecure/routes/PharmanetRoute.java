package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_CERT;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_CERT_PASSWORD;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_PASSWORD;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_URI;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.PHARMANET_USER;
import static org.apache.camel.component.http.HttpMethods.POST;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.http.conn.ssl.NoopHostnameVerifier;

import ca.bc.gov.hlth.hnsecure.audit.AuditSetupProcessor;
import ca.bc.gov.hlth.hnsecure.audit.entities.TransactionEventType;
import ca.bc.gov.hlth.hnsecure.json.Base64Encoder;
import ca.bc.gov.hlth.hnsecure.json.pharmanet.ProcessV2ToPharmaNetJson;
import ca.bc.gov.hlth.hnsecure.parsing.PharmaNetPayloadExtractor;

public class PharmanetRoute extends BaseRoute {
	private static final String SSL2 = "ssl2";

	private static final String CAMEL_HTTP_METHOD = "CamelHttpMethod";
	
	private static final String KEY_STORE_TYPE_PKCS12 = "PKCS12";
    
    private static final String SSL_CONTEXT_PHARMANET = "ssl_context_pharmanet";
   
    private static final String BASIC = "Basic ";

	@Override
	public void configure() throws Exception {
		setupSSLContextPharmanetRegistry(getContext());

		String pharmaNetUrl = String.format(//NOSONAR
				properties.getValue(PHARMANET_URI) + "?bridgeEndpoint=true&sslContextParameters=#%s&authMethod=Basic&authUsername=%s&authPassword=%s",//NOSONAR
				SSL_CONTEXT_PHARMANET, properties.getValue(PHARMANET_USER), properties.getValue(PHARMANET_PASSWORD));//NOSONAR
						
		String basicToken = buildBasicToken(properties.getValue(PHARMANET_USER), properties.getValue(PHARMANET_PASSWORD));

		handleExceptions();
		
		from("direct:pharmanet").routeId("pharmanet-route")
	    	.log("Message identified as PharmaNet message. Preparing message for PharmaNet.")
	    	.process(new AuditSetupProcessor(TransactionEventType.MESSAGE_SENT))
	    	.wireTap("direct:audit").end()
			.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")
			.setBody(body().regexReplaceAll("\r\n","\r"))
	        .setBody().method(new Base64Encoder())
	        .setBody().method(new ProcessV2ToPharmaNetJson()).id("ProcessV2ToPharmaNetJson")
	        .log("Sending to Pharamanet")
	        .removeHeader(Exchange.HTTP_URI) //clean this header as it has been set in the "from" section
	        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
	        .setHeader(CAMEL_HTTP_METHOD, POST)
	        .setHeader(AUTHORIZATION, simple(basicToken))
	        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
	        .to(pharmaNetUrl).id("ToPharmaNet")
	        .log("Received response from Pharmanet:${headers}")
	        .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
	        .process(new PharmaNetPayloadExtractor())
	        .process(new AuditSetupProcessor(TransactionEventType.MESSAGE_RECEIVED))
	        .wireTap("direct:audit").end();
	}

	private void setupSSLContextPharmanetRegistry(CamelContext camelContext) {
		KeyStoreParameters ksp = new KeyStoreParameters();
        ksp.setResource(properties.getValue(PHARMANET_CERT));
        ksp.setPassword(properties.getValue(PHARMANET_CERT_PASSWORD));
        ksp.setType(KEY_STORE_TYPE_PKCS12);

        KeyManagersParameters kmp = new KeyManagersParameters();
        kmp.setKeyStore(ksp);
        kmp.setKeyPassword(properties.getValue(PHARMANET_CERT_PASSWORD));

        SSLContextParameters sslContextParameters = new SSLContextParameters();
        sslContextParameters.setKeyManagers(kmp);

        HttpComponent httpComponent = camelContext.getComponent("https", HttpComponent.class);
        //This is important to make your cert skip CN/Hostname checks
        httpComponent.setX509HostnameVerifier(new NoopHostnameVerifier());

        Registry registry = camelContext.getRegistry();
        registry.bind(SSL_CONTEXT_PHARMANET, sslContextParameters);
        registry.bind(SSL2, new SSLContextParameters()); // If there is only one bound SSL context then Camel will default to always use it in every URL. So as a workaround to stop this a default new empty context is added here. 
	}

	private String buildBasicToken(String username, String password) {
		String usernamePassword = username + ":" + password;
		String token = new String(Base64.getEncoder().encode(usernamePassword.getBytes(StandardCharsets.UTF_8)));
		
		return BASIC + token;
	}
	
}
