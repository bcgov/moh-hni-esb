package ca.bc.gov.hlth.hnsecure;

import org.apache.camel.main.Main;

import ca.bc.gov.hlth.hnsecure.routes.HIBCRoute;
import ca.bc.gov.hlth.hnsecure.routes.HandleResponseRoute;
import ca.bc.gov.hlth.hnsecure.routes.JMBRoute;
import ca.bc.gov.hlth.hnsecure.routes.PharmanetRoute;
import ca.bc.gov.hlth.hnsecure.routes.RTransRoute;
import ca.bc.gov.hlth.hnsecure.routes.Route;

/**
 * Main class that boots the Camel application.
 */
public final class HnsEsbMainMethod {
	
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.setPropertyPlaceholderLocations(
        		"file:${env:HNSESB_HOME}/properties/application-external.properties;optional=true,"
        		+ "classpath:application.properties");
        main.configure().addRoutesBuilder(Route.class);
        main.configure().addRoutesBuilder(PharmanetRoute.class);
        main.configure().addRoutesBuilder(RTransRoute.class);
        main.configure().addRoutesBuilder(HIBCRoute.class);
        main.configure().addRoutesBuilder(JMBRoute.class);
        main.configure().addRoutesBuilder(HandleResponseRoute.class);
        main.run(args);
    }
    
}
