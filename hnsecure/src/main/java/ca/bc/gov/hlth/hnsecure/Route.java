package ca.bc.gov.hlth.hnsecure;

import org.apache.camel.Exchange;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

import ca.bc.gov.hlth.hnscommon.json.Base64Encoder;
import ca.bc.gov.hlth.hnscommon.json.ProcessV2ToPharmaNetJson;
import ca.bc.gov.hlth.hnsecure.authorization.AuthorizationProperties;
import ca.bc.gov.hlth.hnsecure.authorization.ValidateAccessToken;
import ca.bc.gov.hlth.hnsecure.messagevalidation.V2PayloadValidator;
import ca.bc.gov.hlth.hnsecure.parsing.FhirPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PharmaNetPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateReqHeader;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.temporary.samplemessages.SampleMessages;

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

    public Route() {

    }

    // PropertyInject doesn't seem to work in the unit tests, allows creation of the route setting this value
    public Route(String validV2MessageTypes) {
        this.validV2MessageTypes = validV2MessageTypes;
    }

    @Override
    public void configure() {

        AuthorizationProperties authProperties = new AuthorizationProperties(audiences, authorizedParties, scopes, validV2MessageTypes, issuer);
        ValidateAccessToken validateAccessToken = new ValidateAccessToken(authProperties, certsEndpoint);

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
		            .log("Retrieving access token")
		            .setBody().method(new Base64Encoder())
		            .process(new ProcessV2ToPharmaNetJson()).id("ProcessV2ToPharmaNetJson")
		            .to("log:HttpLogger?level=DEBUG&showBody=true&showHeaders=true&multiline=true")
		            .log("Sending to Pharmanet")
		            //TODO (dbarrett) are we setting Headers here or in Processor
		            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		            .removeHeader(Exchange.HTTP_URI)
		            .to("http://{{pharmanet-hostname}}:{{pharmanet-port}}/{{pharmanet-endpoint}}?throwExceptionOnFailure=false").id("ToPharmaNet")
		            .log("Received response from Pharmanet")
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
}
