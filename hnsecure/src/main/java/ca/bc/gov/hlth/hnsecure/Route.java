package ca.bc.gov.hlth.hnsecure;

import ca.bc.gov.hlth.hnsecure.authorization.AuthorizationProperties;
import ca.bc.gov.hlth.hnsecure.messagevalidation.AccessValidator;
import ca.bc.gov.hlth.hnsecure.messagevalidation.V2PayloadValidator;
import ca.bc.gov.hlth.hnsecure.authorization.ValidateAccessToken;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.message.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.parsing.FhirPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateReqHeader;
import ca.bc.gov.hlth.hnsecure.temporary.samplemessages.SampleMessages;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

public class Route extends RouteBuilder {

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
    @PropertyInject(value = "version")
    private String version;
    

    public Route() {

    }

    // PropertyInject doesn't seem to work in the unit tests, allows creation of the route setting this value
    public Route(String validV2MessageTypes, String validReceivingFacility, String processingDomain) {
        this.validV2MessageTypes = validV2MessageTypes;
        this.validReceivingFacility = validReceivingFacility;
        this.processingDomain = processingDomain;
    }

    @Override
    public void configure() {

        AuthorizationProperties authProperties = new AuthorizationProperties(audiences, authorizedParties, scopes, validV2MessageTypes, issuer, validReceivingFacility,processingDomain,version);
        //TODO just pass auth properties into the method
        V2PayloadValidator v2PayloadValidator = new V2PayloadValidator(authProperties);
        ValidateAccessToken validateAccessToken = new ValidateAccessToken(authProperties, certsEndpoint);

        // Handling custom exception  
        onException(CustomHNSException.class)
        	.process(new AccessValidator())
        	.handled(true);
        
        onException(ValidationFailedException.class)
                .log("Validation exception response: ${body}")
                .handled(true)
                .id("ValidationException");

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
	            .when(simple("${in.header.receivingApp} == {{pharmanet-endpoint}}"))
                    .log("The pharmaNet endpoint(${in.header.receivingApp}) is reached and message will be sent to PharmaNet webservices")
                    .setBody(simple(SampleMessages.pnpResponseMessage))
	            
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


}
