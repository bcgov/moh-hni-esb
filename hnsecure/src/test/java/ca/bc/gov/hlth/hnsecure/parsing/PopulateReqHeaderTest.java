package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_APP;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_E45_REQUEST;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_PHARMANET_REQUEST;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R03_REQUEST;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R09_REQUEST;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R15_REQUEST;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R50_Z05;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Test class for PopulateReqHeader
 *
 */
public class PopulateReqHeaderTest extends CamelTestSupport {

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {

		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("direct:sampleHNSecure").bean(new PopulateReqHeader())
						.log("populate the HTTP Request Header for HNSecure").to("mock:outputHNSecure");

			}
		};
	}

	@Test
	public void testParseV2MessageInfoPhamanet() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_PHARMANET_REQUEST);
		// Phamanet message only cares the receiving application value. If it is PNP,
		// the message should be delivery to phamanet endpoint.
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "ZPN");
		template.sendBody("direct:sampleHNSecure", MSG_PHARMANET_REQUEST);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoE45() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_E45_REQUEST);
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "E45");
		template.sendBody("direct:sampleHNSecure", MSG_E45_REQUEST);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR50() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_R50_Z05);
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "R50");
		template.sendBody("direct:sampleHNSecure", MSG_R50_Z05);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR09() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_R09_REQUEST);
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "R09");
		template.sendBody("direct:sampleHNSecure", MSG_R09_REQUEST);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR03() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_R03_REQUEST);
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "R03");
		template.sendBody("direct:sampleHNSecure", MSG_R03_REQUEST);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR15() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_R15_REQUEST);
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "R15");
		template.sendBody("direct:sampleHNSecure", MSG_R15_REQUEST);
		assertMockEndpointsSatisfied();
	}
}
