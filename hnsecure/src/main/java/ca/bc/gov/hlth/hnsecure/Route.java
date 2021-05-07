package ca.bc.gov.hlth.hnsecure;


import java.util.Properties;

import org.apache.camel.builder.RouteBuilder;

import ca.bc.gov.hlth.hnsecure.authorization.ValidateAccessToken;
import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.message.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.messagevalidation.ExceptionHandler;
import ca.bc.gov.hlth.hnsecure.messagevalidation.V2PayloadValidator;
import ca.bc.gov.hlth.hnsecure.parsing.FhirPayloadExtractor;
import ca.bc.gov.hlth.hnsecure.parsing.PopulateReqHeader;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.temporary.samplemessages.SampleMessages;

public class Route extends RouteBuilder {

    @Override
    public void configure() {
    	injectProperties();
    	//The purpose is to set custom unique id for logging
    	getContext().setUuidGenerator(new IDGenerator());

        V2PayloadValidator v2PayloadValidator = new V2PayloadValidator();
        ValidateAccessToken validateAccessToken = new ValidateAccessToken();
        
        // TODO Exception class name is not inline with other exception. It is generic name as compared to ValidationFailedException 
        onException(CustomHNSException.class)
        	.process(new ExceptionHandler())
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
            // TODO if Payload validator is called beforeFhirPayloadExtractor(), no need of separate validator in validateAccessToken 
            .bean(v2PayloadValidator).id("V2PayloadValidator")
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

}
