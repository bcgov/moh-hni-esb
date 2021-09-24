package ca.bc.gov.hlth.hnsecure.routes;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.spi.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;


public class VersionRouteTest extends CamelTestSupport {

	
	@Produce("direct:testRouteStart")
	private ProducerTemplate mockRouteStart;
	
	
	@Before
	public void configureRoute() throws Exception {
		
		// Since we're not running from the main we need to set the properties
		PropertiesComponent pc = context.getPropertiesComponent();
		pc.setLocation("classpath:application.properties"); // loading properties in test/resources
		ApplicationProperties properties = ApplicationProperties.getInstance() ;
		properties.injectProperties(pc.loadProperties());
		
		context.addRoutes(new VersionRoute());
		
		AdviceWithRouteBuilder.adviceWith(context, "hnsecure-version", a -> {
			a.replaceFromWith("direct:testRouteStart");
			a.weaveById("hnsecure-version-id").replace().to("mock:versionurl");
		});
		
	}

	@Test
	public void testSuccessVersionMessage() throws Exception {

		context.start();
		
		// Set expectations
		getMockEndpoint("mock:versionurl").expectedMessageCount(1);
		
		// Send a message
		String emptyBody = "";
		mockRouteStart.sendBody(emptyBody);

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}

}
