package ca.bc.gov.hlth.hnsecure.filedrops;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.bc.gov.hlth.hnsecure.samplemessages.SamplesToSend;
import ca.bc.gov.hlth.hnsecure.test.TestPropertiesLoader;

public class V2FileDropsTest extends TestPropertiesLoader{
	
	private final V2FileDrops filedrops = new  V2FileDrops();

	@Test
	public void test_buildFileName() {
		//20191108083244-R03-moh_hnclient_dev-20210517213625-request.txt
		exchange.getIn().setHeader("Authorization", SamplesToSend.AUTH_HEADER);
		String fileName=filedrops.buildFileName(SamplesToSend.msgR03, exchange.getIn().getHeader("Authorization").toString());
		String[] sections = fileName.split("-");
		assertEquals(sections[0],"20191108083244");
		assertEquals(sections[1],"R03");
		assertEquals(sections[2],"moh_hnclient_dev");	
	}
	
}
