package ca.bc.gov.hlth.hnsecure.parsing;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_APP;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_E45;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_PHARMANET;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R03;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R09;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R15;
import static ca.bc.gov.hlth.hnsecure.test.TestMessages.MSG_R50_Z05;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * @author Tony.Ma *
 * @date Feb.9, 2021
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
		mock.expectedBodiesReceived(MSG_PHARMANET);
		// Phamanet message only cares the receiving application value. If it is PNP,
		// the message should be delivery to phamanet endpoint.
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "ZPN");
		mock.expectedPropertyReceived(PROPERTY_RECEIVING_APP, "PNP");
		mock.expectedHeaderReceived(Util.HEADER_PHARMACY_ID, "BCXX000024");
		mock.expectedHeaderReceived(Util.HEADER_TRACING_ID, "18");
		template.sendBody("direct:sampleHNSecure", MSG_PHARMANET);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoE45() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_E45);
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "E45");
		mock.expectedPropertyReceived(PROPERTY_RECEIVING_APP, "RAIGET-DOC-SUM");
		template.sendBody("direct:sampleHNSecure", MSG_E45);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR50() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_R50_Z05);
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "R50");
		mock.expectedPropertyReceived(PROPERTY_RECEIVING_APP, "RAIENROL-EMP");
		template.sendBody("direct:sampleHNSecure", MSG_R50_Z05);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR09() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_R09);
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "R09");
		mock.expectedPropertyReceived(PROPERTY_RECEIVING_APP, "RAIPRSN-NM-SRCH");
		template.sendBody("direct:sampleHNSecure", MSG_R09);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR03() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_R03);
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "R03");
		mock.expectedPropertyReceived(PROPERTY_RECEIVING_APP, "RAIGT-PRSN-DMGR");
		template.sendBody("direct:sampleHNSecure", MSG_R03);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR15() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(MSG_R15);
		mock.expectedPropertyReceived(PROPERTY_MESSAGE_TYPE, "R15");
		mock.expectedPropertyReceived(PROPERTY_RECEIVING_APP, "RAICHK-BNF-CVST");
		template.sendBody("direct:sampleHNSecure", MSG_R15);
		assertMockEndpointsSatisfied();
	}
}
