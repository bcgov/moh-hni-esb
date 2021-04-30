package ca.bc.gov.hlth.hnsecure.parsing;

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

	// TODO we should make a single spot with all of the messages we use to test
	public static String msgPhamanet = "00000352MSH|^~\\&|PLEXIAPNP|BC01000176|PNP|MD|2020/11/26 21:52:53|JHEWH$#!:192.168.22.66|ZPN|18|D|2.1||\n"
			+ "ZZZ|TID||18|91|XYACA||||\n" + "ZCA||03|00|PE|02\n" + "ZCB|BCXX000024|201126|18\n"
			+ "ZCC||||||||||0009735000001|";

	public static String msgE45 = "MSH|^~\\&|HR|BC00000098|RAIGET-DOC-SUM|BC0003000|19991004103039|lharris|E45|19980915000015|D|2.3"
			+ "HDR|||TRAININGAdmin\r\n" + "SFT|1.0||testorg^^orgid^^^MOH|1.0|barebones||\r\n"
			+ "QPD|E45^^HNET0003|1|^^00000001^^^CANBC^XX^MOH|^^00000001^^^CANBC^XX^MOH|^^00000754^^^CANBC^XX^MOH|9020198746^^^CANBC^JHN^MOH||19421112||||||19980601||PVC^^HNET9909||\r\n"
			+ "RCP|I|";

	public static String msgR50 = "MSH|^~\\&|HNWeb|BC01000030|RAIENROL-EMP|BC00002041|20210121120533|train96|R50^Z05|20210121120533|D|2.4||\r\n"
			+ "ZHD|20210121120533|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + "PID|||||||19990101|M\r\n"
			+ "ZIA||20200801|||||||||||||Gordon^Tom^^^^^L|1102 hilda st^^^^^^^^^^^^^^^^^^^Victoria^BC^v8v2z3^^H~1102 hilda st^^^^^^^^^^^^^^^^^^^Victoria^BC^v8v2z3^^M||||||||S|AB\r\n"
			+ "IN1||||||||6337109||||20210101|20221";

	public static String msgR09 = "MSH|^~\\&|HNWeb|BC01000030|RAIPRSN-NM-SRCH|BC00002041|20191108082211|train96|R09|20191108082211|D|2.4||\r\n"
			+ "ZHD|20191108082211|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + "QRD|||||||^RD||PSN\r\n"
			+ "PID||^^^BC^PH|||||1989|M\r\n" + "ZIA|||||||||||||||branton\r\n";

	public static String msgR03 = "MSH|^~\\&|HNWeb|BC01000030|RAIGT-PRSN-DMGR|BC00002041|20191108083244|train96|R03|20191108083244|D|2.4||\r\n"
			+ "ZHD|20191108083244|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + "PID||0891250000^^^BC^PH\r\n";

	public static String msgR15 = "MSH|^~\\&|HNWeb|BC01000030|RAICHK-BNF-CVST|BC00001013|20201015092224|10-jinzou|R15|20201015092224|D|2.4||\r\n"
			+ "ZHD|20201015092224|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + "PID||0314500001^^^BC^PH\r\n"
			+ "IN1||||||||||||20190228\r\n";

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
		mock.expectedBodiesReceived(msgPhamanet);
		// Phamanet message only cares the receiving application value. If it is PNP,
		// the message should be delivery to phamanet endpoint.
		mock.expectedHeaderReceived(PopulateReqHeader.RECEIVING_APP, "PNP");
		template.sendBody("direct:sampleHNSecure", msgPhamanet);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoE45() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(msgE45);
		mock.expectedHeaderReceived(PopulateReqHeader.MESSAGE_TYPE, "E45");
		mock.expectedHeaderReceived(PopulateReqHeader.RECEIVING_APP, "RAIGET-DOC-SUM");
		template.sendBody("direct:sampleHNSecure", msgE45);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR50() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(msgR50);
		mock.expectedHeaderReceived(PopulateReqHeader.MESSAGE_TYPE, "R50");
		mock.expectedHeaderReceived(PopulateReqHeader.RECEIVING_APP, "RAIENROL-EMP");
		template.sendBody("direct:sampleHNSecure", msgR50);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR09() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(msgR09);
		mock.expectedHeaderReceived(PopulateReqHeader.MESSAGE_TYPE, "R09");
		mock.expectedHeaderReceived(PopulateReqHeader.RECEIVING_APP, "RAIPRSN-NM-SRCH");
		template.sendBody("direct:sampleHNSecure", msgR09);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR03() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(msgR03);
		mock.expectedHeaderReceived(PopulateReqHeader.MESSAGE_TYPE, "R03");
		mock.expectedHeaderReceived(PopulateReqHeader.RECEIVING_APP, "RAIGT-PRSN-DMGR");
		template.sendBody("direct:sampleHNSecure", msgR03);
		assertMockEndpointsSatisfied();
	}

	@Test
	public void testParseV2MessageInfoR15() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:outputHNSecure");
		mock.expectedMessageCount(1);
		mock.expectedBodiesReceived(msgR15);
		mock.expectedHeaderReceived(PopulateReqHeader.MESSAGE_TYPE, "R15");
		mock.expectedHeaderReceived(PopulateReqHeader.RECEIVING_APP, "RAICHK-BNF-CVST");
		template.sendBody("direct:sampleHNSecure", msgR15);
		assertMockEndpointsSatisfied();
	}
}
