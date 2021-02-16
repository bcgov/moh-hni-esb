package ca.bc.gov.hlth.hnsecure;

import ca.bc.gov.hlth.hnsecure.authorization.AuthorizationProperties;
import ca.bc.gov.hlth.hnsecure.authorization.V2PayloadValidator;
import ca.bc.gov.hlth.hnsecure.authorization.ValidateAccessToken;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

public class Route extends RouteBuilder {

    private static final String responseMessage = "MSH|^~\\&|RAIGT-PRSN-DMGR|BC00002041|HNWeb|BC01000030|20200206123841|train96|R03|1819924|D|2.4^M\n" +
            "MSA|AA|20200206123840|HJMB001ISUCCESSFULLY COMPLETED^M\n" +
            "ERR|^^^HJMB001I&SUCCESSFULLY COMPLETED^M\n" +
            "PID||123456789^^^BC^PH^MOH|||||19840225|M^M\n" +
            "ZIA|||||||||||||||LASTNAME^FIRST^S^^^^L|912 VIEW ST^^^^^^^^^^^^^^^^^^^VICTORIA^BC^V8V3M2^CAN^H^^^^N|^PRN^PH^^^250^1234568";

    @PropertyInject(value = "audience")
    private String audiences;
    @PropertyInject(value = "authorized-parties")
    private String authorizedParties;
    @PropertyInject(value = "scopes")
    private String scopes;
    @PropertyInject(value = "valid-v2-message-types")
    private String validV2MessageTypes;
    

    @Override
    public void configure() {

        AuthorizationProperties authProperties = new AuthorizationProperties(audiences, authorizedParties, scopes, validV2MessageTypes);
        //TODO just pass auth properties into the method
        V2PayloadValidator v2PayloadValidator = new V2PayloadValidator(authProperties);
        ValidateAccessToken validateAccessToken = new ValidateAccessToken(authProperties);

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
	            .log("The pharmaNet endpoint(${in.header.receivingApp}) is reached and message will delivery through a socket.")
	            
	            //sending message to HIBC for ELIG
	            .when(simple("${in.header.messageType} == {{hibc-r15-endpoint}} || ${in.header.messageType} == {{hibc-e45-endpoint}}"))
	            .log("the HIBC endpoint(${in.header.messageType}) is reached and message will be dispatched to message queue(ELIG).")
	            
	            //sending message to HIBC for ENROL
	            .when(simple("${in.header.messageType} == {{hibc-r50-endpoint}}"))
	            .log("the HIBC endpoint (${in.header.messageType}) is reached and message will be dispatched to message queue(ENROL).")
	            
	            //others sending to JMB
	            .otherwise()
	            .log("the JMB endpoint is reached and message will be dispatched to JMB!!")
            .end()
            
            .filter(simple("${header.CamelHttpResponseCode} == '200'"))
            .setBody(simple(responseMessage));
    }
}
