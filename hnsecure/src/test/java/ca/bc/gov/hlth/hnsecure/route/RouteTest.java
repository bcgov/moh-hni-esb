package ca.bc.gov.hlth.hnsecure.route;

import ca.bc.gov.hlth.hnsecure.Route;
import ca.bc.gov.hlth.hnsecure.json.FHIRJsonUtil;
import ca.bc.gov.hlth.hnsecure.message.ValidationFailedException;
import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;
import ca.bc.gov.hlth.hnsecure.temporary.samplemessages.SampleMessages;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

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
		pc.setLocation("classpath:application.properties");

		context.addRoutes(new Route("r03, r07, r09, R50^Z05, r15, e45, ZPN", "BC00002041,BC00002047,BC00001013"));
		AdviceWithRouteBuilder.adviceWith(context, "hnsecure-route", a -> {
			a.replaceFromWith("direct:start");
			a.weaveById("ValidateAccessToken").replace().to("mock:ValidateAccessToken");
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

		String expectedErrorMsg = "MSH|^~\\&|HNSecure|BC00002041|HNWeb|BC01000030|20191108083244|ACK|R03|20191108083244|D|2.4\n" +
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

}
