package ca.bc.gov.hlth.hnsecure.route;

import ca.bc.gov.hlth.hnsecure.Route;
import ca.bc.gov.hlth.hnsecure.json.FHIRJsonUtil;
import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;
import ca.bc.gov.hlth.hnsecure.temporary.samplemessages.SampleMessages;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
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

	@Before
	public void configureRoutes() throws Exception {

		// Since we're not running from the main we need to set the properties
		PropertiesComponent pc = context.getPropertiesComponent();
		pc.setLocation("classpath:application.properties");

		context.addRoutes(new Route("r03, r07, r09, R50^Z05, r15, e45, ZPN", "BC00002041,BC00002047,BC00001013"));
		AdviceWithRouteBuilder.adviceWith(context, "hnsecure-route", a -> {
			a.replaceFromWith("direct:start");
			a.weaveById("ValidateAccessToken").replace().to("mock:ValidateAccessToken");
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
		mockRouteStart.sendBodyAndHeaders("direct:start", SamplesToSend.r03JsonMsgLocal, headers);// triggering
		// route execution by sending input to route

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}
	
	@Test
	public void testValidationError() throws Exception {

		context.start();

		// Set expectations
		getMockEndpoint("mock:response").expectedMessageCount(1);
		// responseEndpoint.expectedBodiesReceived(SampleMessages.r03ResponseMessage);
		responseEndpoint.expectedHeaderReceived("CamelHttpResponseCode", "403");

		// Send a message
		mockRouteStart.sendBody(SamplesToSend.r03JsonMsg);

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}

	@Test
	public void testValidationError_MismatchFascilityId() throws Exception {

		String expectedMsg = "VLDT3  The Client Facility and HL7 Sending Facility IDs do not match.";

		context.start();

		// Set expectations
		getMockEndpoint("mock:response").expectedMessageCount(1);

		responseEndpoint.expectedMessagesMatches(new Predicate() {

			@Override
			public boolean matches(Exchange exchange) {
				String obj = (String) exchange.getIn().getBody();
				String[] arr = obj.split("\\|");

				return (arr[14].equals(expectedMsg));
			}
		});
	
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", SamplesToSend.AUTH_HEADER);
		mockRouteStart.sendBodyAndHeaders("direct:start", SamplesToSend.r03JsonMsg, headers);// triggering

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}

}
