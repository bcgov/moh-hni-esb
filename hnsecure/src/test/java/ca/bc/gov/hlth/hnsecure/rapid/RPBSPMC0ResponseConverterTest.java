package ca.bc.gov.hlth.hnsecure.rapid;

import static ca.bc.gov.hlth.hnsecure.parsing.Util.AUTHORIZATION;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_MESSAGE_TYPE;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_PROCESSING_DOMAIN;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RAPID_ORG;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RAPID_PHN;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_RECEIVING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_SENDING_APP;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_SENDING_FACILITY;
import static ca.bc.gov.hlth.hnsecure.parsing.Util.PROPERTY_USER_INFO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.exception.CustomHNSException;
import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.parsing.V2MessageUtil;

public class RPBSPMC0ResponseConverterTest {
	private static String v2Message = "MSH|^~\\&|HNWeb|E13DD6BB-69197950C48|RAIGT-CNT-PRDS|BC00001013|20210820125|anu-test|R32|20220920115331|D|2.4||\r\n"
			+ "ZHD|20220920115331|^^00000010|HNAIADMINISTRATION||||2.4\r\n" + "PID||9337796509^^^BC^PH";
	private static final String R32_SUCCESS = "        RPBSPMC000000010                                RESPONSERPBS9014TRANSACTION SUCCESSFUL                                                  98736722489873672248SPBIGDATASNAME                     SPBIGDATAFNAME                               1983-01-01F98736722550000001S2022-02-010000-00-00 98736722484044574C2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                         9873672255BIGDATASNAME                       BIGDATAFNAME                                 1983-09-09M98736722550000001C2022-02-010000-00-00 98736722484044574S2022-02-012022-02-28E                             ";
	private static final String R32_WARNING_MORE_THAN_20_PERSONS_FOUND = "        RPBSPMC000000010                                INFO    RPBS0086MORE THAN 20 PERSONS FOUND - NOT ALL DISPLAYED                          98736722559873672255BIGDATASNAME                       BIGDATAFNAME                                 1983-09-09M98736722550000001C2022-02-010000-00-00 98736722484044574S2022-02-012022-02-28E                                                                                                                                                                                                                                                                                                                        9873672248SPBIGDATASNAME                     SPBIGDATAFNAME                               1983-01-01F98736722550000001S2022-02-010000-00-00 98736722484044574C2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                         9873671593CHSIXTNBIGDSNAME                   CHSIXTNBIGFNAME                              2018-01-16M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671601CHFRTENBDSNAME                     CHFRTNBDFNAME                                2018-01-14M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671769CHTRTBIGDTSNAME                    CHTRTBIGDATFNAM                              2018-01-13M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671776CHTWTREBIGDTSNAME                  CHTWTREBIGDTFNA                              2018-01-23M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671783CHTWNTTWBISNAME                    CHETWNTTWEBFNAM                              2018-01-21M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671809CHTWTONBIGSNAME                    CHTWTONBIGFNAME                              2018-01-21M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671816CHTNTYBIGDTSNAME                   CHNINTYBIGDTFNA                              2018-01-20M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671823CHNINTNBIGDTSNAME                  CHNINTBIGDAFNAM                              2018-01-19F98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671848CHEITNBIGDTSNAME                   CHDITNBIDTAFNAM                              2018-01-18M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671941CHSETNBIGDTSNAME                   CHSEVTNBDTFNAME                              2018-01-17M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671959CHFIFTNBIGDTSNAME                  CHFIFTNBIGDAFNA                              2018-01-15M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671966CHTLEBIGDTSNAME                    CHTLEBIGDTFNAME                              2018-12-12M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671973CHELNBIGDASNAME                    CHELNBIGDFNAME                               2018-11-11M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873671998CHTENBIGSNAME                      CHTENBIGDFNAME                               2019-10-10M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873672001CHNINBIGDTSNAME                    CHNINBIGDATFNAM                              2019-09-09M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873672019CHETBIGDASNAME                     CHETBIGDTFNAME                               2018-08-08M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873672026CHSVNBIGDSNAME                     CHSVNBIGDFNAME                               2019-07-07M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                9873672033CHSIXBIGDTSNAME                    CHSIXBIGDTFNAME                              2018-06-06M98736722550000001D2022-02-010000-00-00                                                                                                                                                                                                                                                                                                                                                                ";
	
	private static final String R32_ERROR_PHN_NOT_FOUND = "        RPBSPMC000000010                                ERRORMSGRPBS9145PHN NOT FOUND                                                           9159869673		";
	
	@Test
	public void rapid_successMessage() throws CustomHNSException {
		CamelContext ctx = new DefaultCamelContext();
		Exchange ex = new DefaultExchange(ctx);
		setProperties(ex);
		String ziaSegment = "ZIA|||||||||||||||SPBIGDATASNAME^SPBIGDATAFNAME^^^^^L";
		RPBSPMC0ResponseConverter converter = new RPBSPMC0ResponseConverter();
		String response = converter.convertResponse(R32_SUCCESS, ex);
		String dataSegments[] = response.split("\r");
		assertTrue(response.startsWith("MSH"));		
		assertEquals(ziaSegment, dataSegments[3]);

	}

	@Test
	public void rapid_phnNotFound() throws CustomHNSException {
		CamelContext ctx = new DefaultCamelContext();
		Exchange ex = new DefaultExchange(ctx);
		setProperties(ex);
		String errResponse = "ERR|^^^RPBS9145&PHN NOT FOUND";
		RPBSPMC0ResponseConverter converter = new RPBSPMC0ResponseConverter();
		String response = converter.convertResponse(R32_ERROR_PHN_NOT_FOUND, ex);
		
		assertTrue(response.startsWith("MSH"));
		String datasegments[] = response.split("\r");
		String errSegment = datasegments[2];
		assertEquals(errResponse.trim(), errSegment);
		assertTrue(response.startsWith("MSH"));
	}

	@Test
	public void rapid_moreThan20PersonsFound() throws CustomHNSException {
		CamelContext ctx = new DefaultCamelContext();
		Exchange ex = new DefaultExchange(ctx);
		setProperties(ex);
		RPBSPMC0ResponseConverter converter = new RPBSPMC0ResponseConverter();
		String response = converter.convertResponse(R32_WARNING_MORE_THAN_20_PERSONS_FOUND, ex);
		System.out.println(response);
		String datasegments[] = response.split("\r");
	
		assertTrue(response.startsWith("MSH"));
		assertEquals("PID||9873672255^^^BC^PH", datasegments[4]);
		assertEquals("PID||9873672255^^^BC^PH", datasegments[5]);
		assertEquals("IN1||||||||0000001||||20220201", datasegments[109]);
		
	}

	private void setProperties(Exchange exchange) {
		Map<String, Object> exchangeProperties = exchange.getProperties();

		String msgType = V2MessageUtil.getMsgType(v2Message);
		String receivingFacility = V2MessageUtil.getReceivingFacility(v2Message);
		String userInfo = V2MessageUtil.getUserInfo(v2Message);
		String receivingApp = V2MessageUtil.getReceivingApp(v2Message);
		String sendingApp = V2MessageUtil.getSendingApplication(v2Message);
		String domain = V2MessageUtil.getProcessingDomain(v2Message);

		String accessToken = (String) exchange.getIn().getHeader(AUTHORIZATION);
		String sendingFacility = Util.getSendingFacility(accessToken);
		exchangeProperties.put(PROPERTY_MESSAGE_TYPE, msgType);
		exchangeProperties.put(PROPERTY_SENDING_FACILITY, sendingFacility);
		exchangeProperties.put(PROPERTY_RECEIVING_FACILITY, receivingFacility);
		exchangeProperties.put(PROPERTY_SENDING_APP, sendingApp);
		exchangeProperties.put(PROPERTY_RECEIVING_APP, receivingApp);

		exchangeProperties.put(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK_200);

		if (StringUtils.equals(Util.MESSAGE_TYPE_R32, msgType)) {
			String zhdSegment = V2MessageUtil.getDataSegment(v2Message, Util.ZHD_SEGMENT);
			String pidSegment = V2MessageUtil.getDataSegment(v2Message, Util.PID_SEGMENT);
			String org = V2MessageUtil.getOrg(zhdSegment);
			String pid = V2MessageUtil.getPHN(pidSegment);

			exchangeProperties.put(PROPERTY_USER_INFO, userInfo);
			exchangeProperties.put(PROPERTY_PROCESSING_DOMAIN, domain);
			exchangeProperties.put(PROPERTY_RAPID_PHN, pid);
			exchangeProperties.put(PROPERTY_RAPID_ORG, org);
		}
	}
	
}
