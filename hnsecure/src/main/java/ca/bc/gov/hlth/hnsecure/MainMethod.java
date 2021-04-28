package ca.bc.gov.hlth.hnsecure;

import org.apache.camel.main.Main;

import lombok.Getter;

/**
 * Main class that boots the Camel application.
 */
public final class MainMethod {
	@Getter
	private static Main main;
	
    public static void main(String[] args) throws Exception {
        main = new Main();
        main.setPropertyPlaceholderLocations("file:./application.properties;optional=true, classpath:application.properties");
        main.configure().addRoutesBuilder(Route.class);
        main.run(args);
    }
    
}
