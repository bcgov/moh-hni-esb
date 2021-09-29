package ca.bc.gov.hlth.hnsecure.routes;

import org.apache.camel.Exchange;

import ca.bc.gov.hlth.hnsecure.parsing.PopulateVersionInformation;

/**
 * VersionRoute class is added to provide the version information of deployed artifacts
 * This information can be retrieved from URL: http://<hostname>:<port>/version
 * 
 */
public class VersionRoute extends BaseRoute {
	
	@Override
    public void configure() {
    	  	
        from("jetty:http://{{hostname}}:{{port}}/version").routeId("hnsecure-version")
        	.log("HNSecure received a request for version information")
        	.to("log:HttpLogger?level=DEBUG&showBody=true&multiline=true")
        	.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        	.process(new PopulateVersionInformation())
        	.log("HNSecure sent a response with version information")
        	;
    }
}
