package ca.bc.gov.hlth.hnsecure;

import java.nio.charset.Charset;
import java.util.Base64;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.http.conn.ssl.NoopHostnameVerifier;

import ca.bc.gov.hlth.hnscommon.json.Base64Encoder;
import ca.bc.gov.hlth.hnscommon.json.ProcessV2ToPharmaNetJson;
import ca.bc.gov.hlth.hnsecure.authorization.AuthorizationProperties;
import ca.bc.gov.hlth.hnsecure.authorization.ValidateAccessToken;
import ca.bc.gov.hlth.hnsecure.message.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.messagevalidation.V2PayloadValidator;
import ca.bc.gov.hlth.hnsecure.parsing.FhirPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PharmaNetPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateReqHeader;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.temporary.samplemessages.SampleMessages;

public class Route extends RouteBuilder {

    private static final String KEY_STORE_TYPE_PKCS12 = "PKCS12";
    
	@PropertyInject(value = "audience")
    private String audiences;
    @PropertyInject(value = "authorized-parties")
    private String authorizedParties;
    @PropertyInject(value = "scopes")
    private String scopes;
    @PropertyInject(value = "issuer")
    private String issuer;
    @PropertyInject(value = "valid-v2-message-types")
    private String validV2MessageTypes;
    @PropertyInject(value = "certs-endpoint")
    private String certsEndpoint;
    @PropertyInject(value = "valid-receiving-facility")
    private String validReceivingFacility;
    @PropertyInject(value = "processing-domain")
    private String processingDomain;
    
    // PharmaNet Endpoint values
	@PropertyInject(value = "pharmanet.uri")
    private String pharmanetUri;

	@PropertyInject(value = "pharmanet.cert")
    private String pharmanetCert;
    
    @PropertyInject(value = "pharmanet.cert.password")
    private String 	pharmanetCertPassword;
    
	@PropertyInject(value = "pharmanet.user")
    private String pharmanetUser;
    
	@PropertyInject(value = "pharmanet.password")
    private String pharmanetPassword;    
    
    public Route() {
    }

    // PropertyInject doesn't seem to work in the unit tests, allows creation of the route setting this value
    public Route(String validV2MessageTypes, String validReceivingFacility, String processingDomain) {
        this.validV2MessageTypes = validV2MessageTypes;
        this.validReceivingFacility = validReceivingFacility;
        this.processingDomain = processingDomain;
    }

    @Override
    public void configure() throws Exception {
        AuthorizationProperties authProperties = new AuthorizationProperties(audiences, authorizedParties, scopes, validV2MessageTypes, issuer, validReceivingFacility,processingDomain);
        //TODO just pass auth properties into the method
        V2PayloadValidator v2PayloadValidator = new V2PayloadValidator(authProperties);
        ValidateAccessToken validateAccessToken = new ValidateAccessToken(authProperties, certsEndpoint);
        
    	onException(org.apache.http.conn.HttpHostConnectException.class)
		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500)).handled(true)
		.log("Failed to connect remote server");
        
        onException(ValidationFailedException.class)
                .log("Validation exception response: ${body}")
                .handled(true)
                .id("ValidationException");
        
        setupSSLConextPharmanetRegistry(getContext());
        String pharmNetUrl = String.format(pharmanetUri + "?bridgeEndpoint=true&sslContextParameters=#ssl&authMethod=Basic&authUsername=%s&authPassword=%s", pharmanetUser, pharmanetPassword);
        String basicToken = buildBasicToken(pharmanetUser, pharmanetPassword);
        log.info("Using pharmNetUrl: " + pharmNetUrl);

        from("jetty:http://{{hostname}}:{{port}}/{{endpoint}}").routeId("hnsecure-route")
            .log("HNSecure received a request")
            .process(validateAccessToken).id("ValidateAccessToken")
            .setBody().method(new FhirPayloadExtractor())
            .log("Decoded V2: ${body}")            
            .bean(V2PayloadValidator.class).id("V2PayloadValidator")
            //set the receiving app, message type into headers
            .bean(PopulateReqHeader.class).id("PopulateReqHeader")
            .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
            .log("The message receiving application is <${in.header.receivingApp}> and the message type is <${in.header.messageType}>.")     
            
            //dispatch the message based on the receiving application code and message type
            .choice()
	            //sending message to pharmaNet
            	.when(header("receivingApp").isEqualTo(Util.RECEIVING_APP_PNP))
                	.log("Message identified as PharmaNet message. Preparing message for PharmaNet.")
            		.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")
            		.setBody(body().regexReplaceAll("\r\n","\r").regexReplaceAll("moh_hnclient_dev", "MOH_HNCLIENT_DEV"))
                    .setBody().method(new Base64Encoder())
		            .process(new ProcessV2ToPharmaNetJson()).id("ProcessV2ToPharmaNetJson")
		            .log("Sending to Pharmanet")
		            .removeHeader(Exchange.HTTP_URI) //clean this header as it has been set in the "from" section
		            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))		            
		            .setHeader("CamelHttpMethod", constant("POST"))
		            .setHeader("Authorization", simple(basicToken))
		            .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
		            .to(pharmNetUrl).id("ToPharmaNet")
		            .log("Received response from Pharmanet")
		            .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
		            .process(new PharmaNetPayloadExtractor())
		            
	            //sending message to HIBC for ELIG
	            .when(simple("${in.header.messageType} == {{hibc-r15-endpoint}} || ${in.header.messageType} == {{hibc-e45-endpoint}}"))
	                .log("the HIBC endpoint(${in.header.messageType}) is reached and message will be dispatched to message queue(ELIG).")
                    .setBody(simple(SampleMessages.e45ResponseMessage))
	            
	            //sending message to HIBC for ENROL
	            .when(simple("${in.header.messageType} == {{hibc-r50-endpoint}}"))
	                .log("the HIBC endpoint (${in.header.messageType}) is reached and message will be dispatched to message queue(ENROL).")
                    .setBody(simple(SampleMessages.r50ResponseMessage))
	            
	            //others sending to JMB
	            .otherwise()
                    .log("the JMB endpoint is reached and message will be dispatched to JMB!!")
                    .setBody(simple(SampleMessages.r03ResponseMessage))
            .end();

    }

	private String buildBasicToken(String username, String password) {
		String usernamePassword = username + ":" + password;
		Charset charSet = Charset.forName("UTF-8");
		String token = new String(Base64.getEncoder().encode(usernamePassword.getBytes(charSet)));
		String basicToken = "Basic " + token;
		return basicToken;
	}

	private void setupSSLConextPharmanetRegistry(CamelContext camelContext) throws Exception {
		KeyStoreParameters ksp = new KeyStoreParameters();
        ksp.setResource(pharmanetCert);
        ksp.setPassword(pharmanetCertPassword);
        ksp.setType(KEY_STORE_TYPE_PKCS12);

        KeyManagersParameters kmp = new KeyManagersParameters();
        kmp.setKeyStore(ksp);
        kmp.setKeyPassword(pharmanetCertPassword);

        SSLContextParameters sslContextParameters = new SSLContextParameters();
        sslContextParameters.setKeyManagers(kmp);

        HttpComponent httpComponent = camelContext.getComponent("https", HttpComponent.class);
        //This is important to make your cert skip CN/Hostname checks
        httpComponent.setX509HostnameVerifier(new NoopHostnameVerifier());

        Registry registry = camelContext.getRegistry();
        registry.bind("ssl", sslContextParameters);
        registry.bind("ssl2", new SSLContextParameters()); //If there is only one bound SSL context then Camel will default to always use it in every URL. This is a workaround to stop this. 
	}
}
