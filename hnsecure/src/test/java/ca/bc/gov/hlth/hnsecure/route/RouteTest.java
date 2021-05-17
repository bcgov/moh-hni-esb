package ca.bc.gov.hlth.hnsecure.route;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.Route;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;
import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;
import ca.bc.gov.hlth.hnsecure.temporary.samplemessages.SampleMessages;

public class RouteTest extends CamelTestSupport {

	@Override
	public boolean isUseAdviceWith() {
		return true;
	}

	@Produce("direct:start")
	private ProducerTemplate mockRouteStart;

	@EndpointInject("mock:response")
	private MockEndpoint responseEndpoint;

	@EndpointInject("mock:validationExceptionResponse")
	private MockEndpoint validationExceptionEndpoint;

	@Before
	public void configureRoutes() throws Exception {

		// Since we're not running from the main we need to set the properties
		PropertiesComponent pc = context.getPropertiesComponent();
		pc.setLocation("classpath:application.properties"); // loading properties in test/resources
		ApplicationProperties properties = ApplicationProperties.getInstance() ;
		properties.injectProperties(pc.loadProperties());
		
		context.addRoutes(new Route());
		AdviceWithRouteBuilder.adviceWith(context, "hnsecure-route", a -> {
			a.replaceFromWith("direct:start");
			//a.weaveById("Validator").replace().to("mock:TokenValidator");
			a.weaveById("ValidationException").after().to("mock:validationExceptionResponse");
			a.weaveAddLast().to("mock:response");
		});
	}

	@Test
	public void testSuccessfulJMBMessage() throws Exception {

		context.start();

		// Set expectations
		getMockEndpoint("mock:response").expectedMessageCount(1);
		responseEndpoint.expectedBodiesReceived(SampleMessages.r03ResponseMessage);

		// Send a message with header
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", SamplesToSend.AUTH_HEADER);
		// trigger route execution by sending input to route
		mockRouteStart.sendBodyAndHeaders("direct:start", SamplesToSend.r03JsonMsgLocal, headers);

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}
	
	@Test
	public void testValidationError() throws Exception {

		String expectedErrorMsg = "MSH|^~\\&|HNSECURE|BC00002041|HNWeb|BC01000030|20191108083244|train96|ACK|R03|20191108083244|D|2.4\n" +
				"MSA|AR|20191108083244|VLDT008E  The Client Facility and HL7 Sending Facility IDs do not match.|";	
		
		context.start();

		// Set expectations
		getMockEndpoint("mock:validationExceptionResponse").expectedMessageCount(1);
		getMockEndpoint("mock:validationExceptionResponse").expectedBodiesReceived(expectedErrorMsg);

		// Send a message
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", SamplesToSend.AUTH_HEADER);
		mockRouteStart.sendBodyAndHeaders("direct:start", SamplesToSend.r03JsonMsg, headers);

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}
	
	

	/*
	 * Properties are injected in @Before method.
	 * Here, we are validating if correct values are loaded. 
	 * @throws Exception
	 */
	@Test
	public void testInjectProperties() throws Exception {
		String actual = ApplicationProperties.getInstance().getValue(ApplicationProperty.ENDPOINT);
		String expected = "hl7v2-test";
		assertTrue("Expected value "+expected+" is not as actual: "+actual, expected.contentEquals(actual));
	}

}
