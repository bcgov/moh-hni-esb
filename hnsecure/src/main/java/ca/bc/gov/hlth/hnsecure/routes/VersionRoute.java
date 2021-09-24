package ca.bc.gov.hlth.hnsecure.routes;

import org.apache.camel.Exchange;

import ca.bc.gov.hlth.hnsecure.parsing.PopulateVersionInformation;

public class VersionRoute extends BaseRoute {
	

    
	@Override
    public void configure() {
    	  	
        from("jetty:http://{{hostname}}:{{port}}/version").routeId("hnsecure-version")
        	.log("HNSecure received a request for version information")
        	.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true").id("hnsecure-version-id")
        	.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        	.process(new PopulateVersionInformation())
        	.log("HNSecure sent a response with version infomration")
        	;
    }
	
	
	
 
}
