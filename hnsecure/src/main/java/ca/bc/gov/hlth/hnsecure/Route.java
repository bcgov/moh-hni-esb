package ca.bc.gov.hlth.hnsecure;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.IS_FILEDDROPS_ENABLED;
import static org.apache.camel.component.http.HttpMethods.POST;

import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Properties;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.exception.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.filedrops.RequestFileDropGenerater;
import ca.bc.gov.hlth.hnsecure.filedrops.ResponseFileDropGenerater;
import ca.bc.gov.hlth.hnsecure.json.Base64Encoder;
import ca.bc.gov.hlth.hnsecure.json.fhir.ProcessV2ToJson;
import ca.bc.gov.hlth.hnsecure.json.pharmanet.ProcessV2ToPharmaNetJson;
import ca.bc.gov.hlth.hnsecure.messagevalidation.ExceptionHandler;
import ca.bc.gov.hlth.hnsecure.parsing.FhirPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PharmaNetPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateReqHeader;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.temporary.samplemessages.SampleMessages;
import ca.bc.gov.hlth.hnsecure.validation.PayLoadValidator;
import ca.bc.gov.hlth.hnsecure.validation.TokenValidator;
import ca.bc.gov.hlth.hnsecure.validation.Validator;
import ca.bc.gov.hlth.hnsecure.validation.ValidatorImpl;

public class Route extends RouteBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(Route.class);

	private static final String KEY_STORE_TYPE_PKCS12 = "PKCS12";
    
    private static final String SSL_CONTEXT_PHARMANET = "ssl_context_pharmanet";

	private static final String CAMEL_HTTP_METHOD = "CamelHttpMethod";

	private static final String BASIC = "Basic ";

    // PharmaNet Endpoint values
	@PropertyInject(value = "pharmanet.uri")
    private String pharmanetUri;

	@PropertyInject(value = "pharmanet.cert")
    private String pharmanetCert;
    
    private static final String pharmanetCertPassword = System.getenv("PHARMANET_CERT_PASSWORD");
    
    private static final String pharmanetUser = System.getenv("PHARMANET_USER");

    private static final String pharmanetPassword = System.getenv("PHARMANET_PASSWORD");
    
	private static ApplicationProperties properties;
    
    private Validator validator;
    
    @Override
    public void configure() {
    	init();

    	onException(CustomHNSException.class)
        	.process(new ExceptionHandler())
        	.handled(true);
        
    	onException(org.apache.http.conn.HttpHostConnectException.class)
		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500)).handled(true)
		.setBody(constant(null))
		.log("Failed to connect remote server");
        
        onException(ValidationFailedException.class)
                .log("Validation exception response: ${body}")
                .handled(true)
                .id("ValidationException");
        
        setupSSLConextPharmanetRegistry(getContext());
        String pharmNetUrl = String.format(pharmanetUri + "?bridgeEndpoint=true&sslContextParameters=#%s&authMethod=Basic&authUsername=%s&authPassword=%s", SSL_CONTEXT_PHARMANET, pharmanetUser, pharmanetPassword);
        String basicToken = buildBasicToken(pharmanetUser, pharmanetPassword);
        log.info("Using pharmNetUrl: " + pharmNetUrl);
        
        String isFileDropsEnabled = properties.getValue(IS_FILEDDROPS_ENABLED);

        from("jetty:http://{{hostname}}:{{port}}/{{endpoint}}").routeId("hnsecure-route")
        	//this route is only invoked when the original route is complete as a kind
			// of completion callback.The onCompletion method is called once per route execution.
			//Making it global will generate two response file drops.
   
			.onCompletion().modeBeforeConsumer().onWhen(body().isNotNull())
			//creating filedrops if enabled
		    	.choice().when(header("isFileDropsEnabled").isEqualToIgnoreCase(Boolean.TRUE))
		    		.bean(ResponseFileDropGenerater.class).id("ResponseFileDropGenerater").end()
		    		//encoding response before sending to consumer
		    		.setBody().method(new Base64Encoder()).id("Base64Encoder")
		    		.setBody().method(new ProcessV2ToJson()).id("ProcessV2ToJson")
		    		.end()
			
			// here the original route continues
        	.log("HNSecure received a request")
        	.setHeader("isFileDropsEnabled").simple(isFileDropsEnabled)
        	// Extract the message using custom extractor and log 
        	.setBody().method(new FhirPayloadExtractor()).log("Decoded V2: ${body}")
        	// Added wireTap for asynchronous call to filedrop request
			.wireTap("direct:start").end()
        	// Validate the message
        	.process(validator).id("Validator")
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
            		.setBody(body().regexReplaceAll("\r\n","\r"))
                    .setBody().method(new Base64Encoder())
		            .setBody().method(new ProcessV2ToPharmaNetJson()).id("ProcessV2ToPharmaNetJson")
		            .log("Sending to Pharmanet")
		            .removeHeader(Exchange.HTTP_URI) //clean this header as it has been set in the "from" section
		            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))		            
		            .setHeader(CAMEL_HTTP_METHOD, POST)
		            .setHeader(AUTHORIZATION, simple(basicToken))
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
           
        
        from("direct:start").log("wireTap route").choice()
			.when(header("isFileDropsEnabled").isEqualToIgnoreCase(Boolean.TRUE))
			.bean(RequestFileDropGenerater.class).id("V2FileDropsRequest").log("wire tap done");

    }

	private String buildBasicToken(String username, String password) {
		String usernamePassword = username + ":" + password;
		Charset charSet = Charset.forName("UTF-8");
		String token = new String(Base64.getEncoder().encode(usernamePassword.getBytes(charSet)));
		String basicToken = BASIC + token;
		return basicToken;
	}

	private void setupSSLConextPharmanetRegistry(CamelContext camelContext) {
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
        registry.bind(SSL_CONTEXT_PHARMANET, sslContextParameters);
        registry.bind("ssl2", new SSLContextParameters()); //TODO (dbarrett) If there is only one bound SSL context then Camel will default to always use it in every URL. This is a workaround to stop this for now. Can be removed when another endpoint is configured with it's context. 
	}
	
    /**
     * This method performs the steps required before configuring the route
     * 1. Set the transaction id generator for messages
     * 2. Load application properties
     * 3. Initializes the validator classes
     */
    private void init() {
    	//The purpose is to set custom unique id for logging
    	getContext().setUuidGenerator(new TransactionIdGenerator());
    	injectProperties();
    	properties = ApplicationProperties.getInstance();
    	loadValidator();
    	
    }
    
	/**
     * This method injects application properties set in the context to ApplicationProperties class
     * This helps in using the properties across the application without extending classes as RouteBuilder.
     * Sample test code to use application properties: RouteTest.testApplicationPropertiesLoader()
     */
    public void injectProperties() {
    	Properties properties  = getContext().getPropertiesComponent().loadProperties();
    	ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    	applicationProperties.injectProperties(properties);

    }
    
	/**
     * This method initializes the Validators. 
     * If there was an error in loading validators, HNSecure server will not start
     * Currently only TokenValidator is throwing error. 
     */
    public void loadValidator() {
    	try {
    		// This syntax will make sure validation is happening in the order of First Token validation then Payload validation then  ValidatorImpl
			validator = new TokenValidator(new PayLoadValidator(new ValidatorImpl()));
		} catch (MalformedURLException e) {
			logger.error("Error in server startup: ", e);
			logger.error("Stopping HNSecure Server. ");
			System.exit(0);
		}
    	
    }
}