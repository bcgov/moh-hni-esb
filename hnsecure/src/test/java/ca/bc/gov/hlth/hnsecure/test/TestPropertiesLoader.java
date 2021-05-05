package ca.bc.gov.hlth.hnsecure.test;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.PropertiesComponent;
import org.apache.camel.support.DefaultExchange;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;

public class TestPropertiesLoader {

	public static CamelContext context = new DefaultCamelContext();
    public Exchange exchange = new DefaultExchange(context);

	
    @BeforeClass
	public static void loadProperties() throws Exception {
    	// Since we're not running from the main we need to set the properties
		PropertiesComponent pc = context.getPropertiesComponent();
		pc.setLocation("classpath:application.properties"); // laoding properties in test/resources
		ApplicationProperties properties = ApplicationProperties.getInstance() ;
		properties.injectProperties(pc.loadProperties());
		
	}
    
    @AfterClass
	public static void cleanUp() throws Exception {
    	context.close();
    	
    }


}
