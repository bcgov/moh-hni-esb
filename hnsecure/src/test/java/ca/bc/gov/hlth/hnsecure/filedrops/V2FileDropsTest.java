package ca.bc.gov.hlth.hnsecure.filedrops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.parsing.Util;
import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;
import ca.bc.gov.hlth.hnsecure.test.TestPropertiesLoader;

public class V2FileDropsTest extends TestPropertiesLoader{
	
	@Test
	public void test_buildFileName() {
		//ID-CA-LJPDZTN2-1621486716331-0-1-R03-moh_hnclient_dev-20210519225836
		exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
		String fileName=Util.buildFileName(SamplesToSend.msgR03, exchange.getIn().getHeader("Authorization").toString(),exchange.getIn().getMessageId());
		String[] sections = fileName.split("-");
		assertNotNull("transactionid should not be null", sections[0]);
		assertEquals(sections[6],"R03");
		assertEquals(sections[7],"moh_hnclient_dev");	
	}
	
}
