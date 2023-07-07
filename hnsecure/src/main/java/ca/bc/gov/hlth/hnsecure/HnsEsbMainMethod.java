package ca.bc.gov.hlth.hnsecure;

import org.apache.camel.main.Main;

import ca.bc.gov.hlth.hnsecure.routes.HIBCRoute;
import ca.bc.gov.hlth.hnsecure.routes.HandleResponseRoute;
import ca.bc.gov.hlth.hnsecure.routes.RapidRoute;
import ca.bc.gov.hlth.hnsecure.routes.PharmanetRoute;
import ca.bc.gov.hlth.hnsecure.routes.RTransRoute;
import ca.bc.gov.hlth.hnsecure.routes.RotateFilesRoute;
import ca.bc.gov.hlth.hnsecure.routes.Route;
import ca.bc.gov.hlth.hnsecure.routes.VersionRoute;

/**
 * Main class that boots the Camel application.
 */
public final class HnsEsbMainMethod {
	
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        /*
         * Setting the precedence of application properties in following order:
         * First preference: External property file with name application-external.properties at location ${env:HNSESB_HOME}/properties/ 
         * Second preference: Property file with name application-external.properties at classpath
         * Third preference: Property file with name application.properties at classpath 
         */
        main.setPropertyPlaceholderLocations(
        		"file:${env:HNSESB_HOME}/properties/application-external.properties;optional=true,"
        		+"classpath:application-external.properties;optional=true,"
        		+ "classpath:application.properties");
        main.configure().addRoutesBuilder(Route.class);
        main.configure().addRoutesBuilder(VersionRoute.class);
        main.configure().addRoutesBuilder(PharmanetRoute.class);
        main.configure().addRoutesBuilder(RTransRoute.class);
        main.configure().addRoutesBuilder(HIBCRoute.class);
        main.configure().addRoutesBuilder(RapidRoute.class);
        main.configure().addRoutesBuilder(HandleResponseRoute.class);
        main.configure().addRoutesBuilder(RotateFilesRoute.class);
        main.run(args);
    }
    
}
