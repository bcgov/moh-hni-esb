package ca.bc.gov.hlth.hnsecure.parsing;

import org.junit.Assert;
import org.junit.Test;

public class PopulateVersionInformationTest {

	/*
	 *  When executed in eclipse or in junit jenkins, package information is returned null.
	 *  This test verifies the syntax of verison information, not the values.
	 */
	@Test
	public void testGetVersionInformation() {
		String actualVersionInformation = PopulateVersionInformation.getVersionInformation().toJSONString();
		String expectedVersionInformation = "{\"Implementation-Version\":null}";
		Assert.assertEquals(expectedVersionInformation, actualVersionInformation);
	}

}
