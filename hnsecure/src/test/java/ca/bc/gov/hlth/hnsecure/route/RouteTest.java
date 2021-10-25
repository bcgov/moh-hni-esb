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

import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty;
import ca.bc.gov.hlth.hnsecure.routes.HIBCRoute;
import ca.bc.gov.hlth.hnsecure.routes.HandleResponseRoute;
import ca.bc.gov.hlth.hnsecure.routes.JMBRoute;
import ca.bc.gov.hlth.hnsecure.routes.PharmanetRoute;
import ca.bc.gov.hlth.hnsecure.routes.RTransRoute;
import ca.bc.gov.hlth.hnsecure.routes.Route;
import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;

public class RouteTest extends CamelTestSupport {

	private static final String WRAPPED_R03_RESPONSE = "{\"content\":[{" +
			"\"attachment\":{" + 
			"\"data\":\"TVNIfF5+XCZ8SE5XRUJ8bW9oX2huY2xpZW50X2RldnxSQUlHVC1QUlNOLURNR1J8QkMwMDAwMTAxM3wyMDE3MDEyNTEyMjEyNXx0cmFpbjk2fFIwM3wyMDE3MDEyNTEyMjEyNXxEfDIuNHx8ClpIRHwyMDE3MDEyNTEyMjEyNXxeXjAwMDAwMDEwfEhOQUlBRE1JTklTVFJBVElPTnx8fHwyLjQKUElEfHwxMjM0NTY3ODkwXl5eQkNeUEgN\"," +
			"\"contentType\":\"x-application\\/hl7-v2+er7\"}}]," +
			"\"resourceType\":\"DocumentReference\"," + 
			"\"status\":\"current\"}";
		
	@Override
	public boolean isUseAdviceWith() {
		return true;
	}

	@Produce("direct:testRouteStart")
	private ProducerTemplate mockRouteStart;

	@EndpointInject("mock:testRouteEnd")
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
		
		// Manually add the Routes to the context for testing
		context.addRoutes(new Route());
		context.addRoutes(new PharmanetRoute());
		context.addRoutes(new RTransRoute());
		context.addRoutes(new HIBCRoute());
		context.addRoutes(new JMBRoute());
		context.addRoutes(new HandleResponseRoute());
		
		AdviceWithRouteBuilder.adviceWith(context, "hnsecure-route", a -> {
			a.replaceFromWith("direct:testRouteStart");
			a.weaveById("Validator").replace().to("mock:ValidateAccessToken");		
			a.weaveById("ValidationException").after().to("mock:validationExceptionResponse");
			a.weaveById("SetExchangeIdFromHeader").replace().to("mock:SetExchangeIdFromHeader");
			a.weaveById("HandleResponse").after().to("mock:testRouteEnd");
		});
		AdviceWithRouteBuilder.adviceWith(context, "pharmanet-route", a -> {
			a.weaveById("ToPharmaNet").replace().to("mock:pharmanetEndpoint");
		});
		AdviceWithRouteBuilder.adviceWith(context, "rtrans-route", a -> {
			a.weaveById("ToRTrans").replace().to("mock:rtransEndpoint");
		});
		AdviceWithRouteBuilder.adviceWith(context, "hibc-http-route", a -> {
			a.weaveById("ToHibcHttpUrl").replace().to("mock:hibcHttp");
		});
		AdviceWithRouteBuilder.adviceWith(context, "hibc-mq-route", a -> {
			a.weaveById("ToHibcMqUrl").replace().to("mock:hibcMq");
		});
		AdviceWithRouteBuilder.adviceWith(context, "jmb-http-route", a -> {
			a.weaveById("ToJmbHttpUrl").replace().to("mock:jmbHttp");
		});
		AdviceWithRouteBuilder.adviceWith(context, "jmb-mq-route", a -> {
			a.weaveById("ToJmbUrl").replace().to("mock:jmb");
		});
	}

	@Test
	public void testSuccessfulRTransMessage() throws Exception {

		context.start();

		// Set expectations
		responseEndpoint.expectedMessageCount(1);
		responseEndpoint.expectedBodiesReceived(WRAPPED_R03_RESPONSE);

		// Send a message with header
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", SamplesToSend.AUTH_HEADER);
		// trigger route execution by sending input to route
		mockRouteStart.sendBodyAndHeaders("direct:testRouteStart", SamplesToSend.r03JsonMsgLocal, headers);

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}

	@Test
	public void testCustomTransactionIdFromHttpHeader() throws Exception {
		context.start();

		// Set expectations
		responseEndpoint.expectedMessageCount(1);
		responseEndpoint.expectedBodiesReceived(WRAPPED_R03_RESPONSE);
		
		// Not an ideal way to test if the code to set the exchangeId works, but the mock ResponseEndpoint doesn't
		// return the updated ExchangeId even though it shows correctly in all logging.
		// i.e. assertEquals("test-request-id", responseEndpoint.getReceivedExchanges().get(0).getExchangeId()) fails
		// even though the code works
		// Instead just test if the code is called
		getMockEndpoint("mock:SetExchangeIdFromHeader").expectedMessageCount(1);

		String customTransactionId = "test-request-id";
		// Send a message with header
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", SamplesToSend.AUTH_HEADER);
		headers.put("X-Request-Id", customTransactionId);
		// trigger route execution by sending input to route
		mockRouteStart.sendBodyAndHeaders("direct:testRouteStart", SamplesToSend.r03JsonMsgLocal, headers);

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}
	
	/*
	 * In old validator framework, there were multiple validators ( for Token and for payload)
	 * One was registered as a Processor and other was registered as a bean.
	 * In the configure method of this class, we were mocking token validator only but running payload validator with request message.
	 * In new framework, all the validators are registered under single id. So mocking will mock all validations and we are not getting any validation error
	 * Hence commenting this test case in RouteTest.
	 * This functionality is validated in PayLoadValidatorTest.testHL7ErrorMsgFacilityIdMismatch method.    
	 */
	//@Test
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
		mockRouteStart.sendBodyAndHeaders("direct:testRouteStart", SamplesToSend.r03JsonMsg, headers);

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}
	/*
	 * In old validator framework, there were multiple validators ( for Token and for payload)
	 * One was registered as a Processor and other was registered as a bean.
	 * In the configure method of this class, we were mocking token validator only but running payload validator with request message.
	 * In new framework, all the validators are registered under single id. So mocking will mock all validations and we are not getting any validation error
	 * Hence commenting this test case in RouteTest.
	 */
	//@Test
	public void testValidationPNPError() throws Exception {

		String expectedErrorMsg = "MSH|dd\\&|HNSecure|PP|PLEXIAPNP|moh_hnclient_dev|2020/11/26 21:52:53|ACK|ZPN|18|D|2.1\n" +
				"MSA|AR|18|HNPS002E  Invalid MSH segment format|";		

		context.start();

		// Set expectations
		getMockEndpoint("mock:validationExceptionResponse").expectedMessageCount(1);
		getMockEndpoint("mock:validationExceptionResponse").expectedBodiesReceived(expectedErrorMsg);

		// Send a message
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", SamplesToSend.AUTH_HEADER);
		mockRouteStart.sendBodyAndHeaders("direct:testRouteStart", SamplesToSend.pnpJsonErrorMsg, headers);

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}
	
	@Test
	public void testSuccessFullPharmanetMessage() throws Exception {

		context.start();

		// Set expectations
		getMockEndpoint("mock:pharmanetEndpoint").expectedMessageCount(1);
		
		// Send a message
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", SamplesToSend.AUTH_HEADER);
		mockRouteStart.sendBodyAndHeaders("direct:testRouteStart", SamplesToSend.pnpJsonMsg, headers);

		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}
	
	@Test
	public void testSuccessFullHIBCMessage() throws Exception {

		context.start();

		// Set expectations
		getMockEndpoint("mock:hibcMq").expectedMessageCount(1);
		
		// Send a message
		mockRouteStart.sendBodyAndProperty("direct:testRouteStart", SamplesToSend.hibcJsonMsg, Util.PROPERTY_MESSAGE_TYPE, "E45");
		
		// Verify our expectations were met
		assertMockEndpointsSatisfied();

		context.stop();
	}
	
	@Test
	public void testSuccessFullJMBMessage() throws Exception {

		context.start();

		// Set expectations
		getMockEndpoint("mock:jmb").expectedMessageCount(1);		
		mockRouteStart.sendBodyAndProperty("direct:testRouteStart", SamplesToSend.hibcJsonMsg, Util.PROPERTY_MESSAGE_TYPE, "R32");

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
