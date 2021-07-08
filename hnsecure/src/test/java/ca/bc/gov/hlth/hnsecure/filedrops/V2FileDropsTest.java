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
		// 11b7bbb2-4668-4ab3-9794-624147d5d9e8-R03-moh_hnclient_dev-20210519225836
		exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
		String sendingFacility = Util.getSendingFacility((String)exchange.getIn().getHeader("Authorization"));
		String fileName = Util.buildFileName(sendingFacility,exchange.getExchangeId(), "R03");
		String[] sections = fileName.split("-");
		
		// First 5 positions are the UUID
		assertNotNull("transactionid should not be null", sections[0]);
		assertEquals(sections[5], "R03");
		assertEquals(sections[6], "moh_hnclient_dev");	
	}
	
}
