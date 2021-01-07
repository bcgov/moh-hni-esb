package ca.bc.gov.hlth.hnsecure;

import org.apache.camel.main.Main;

/**
 * Main class that boots the Camel application.
 */
public final class MainMethod {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(Route.class);
        main.run(args);
    }
}
