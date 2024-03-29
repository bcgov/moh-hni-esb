package ca.bc.gov.hlth.hnsecure.properties;

import java.util.Properties;


public class ApplicationProperties {
	
	
	private Properties properties;
	private static ApplicationProperties applicationProperties;
	
	private ApplicationProperties() {
		// 
	}
	
    /**
     * This method injects application properties on start up
     * @param properties
     */
    public void injectProperties(Properties properties) {
    	this.properties = properties;
    }

	public static ApplicationProperties getInstance() {
		if(applicationProperties ==null) {
			applicationProperties = new ApplicationProperties();
		}
		return applicationProperties;
	}
	
	public String getValue(ApplicationProperty applicationProperty) {
		return (String) properties.get(applicationProperty.getKey());
	}
	
	public String getValue(String key) {
		return (String) properties.get(key);
	}
	
	

}
