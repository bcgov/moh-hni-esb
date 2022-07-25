package ca.bc.gov.hlth.hnsecure.routes;

import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class RouteUtils {

    private static final String KEY_STORE_TYPE_PKCS12 = "PKCS12";
    private static final String BASIC = "Basic ";

    public static SSLContextParameters setupSslContextParameters(String certName, String certPassword) {
        KeyStoreParameters ksp = new KeyStoreParameters();
        ksp.setResource(certName);
        ksp.setPassword(certPassword);
        ksp.setType(KEY_STORE_TYPE_PKCS12);

        KeyManagersParameters kmp = new KeyManagersParameters();
        kmp.setKeyStore(ksp);
        kmp.setKeyPassword(certPassword);

        SSLContextParameters sslContextParameters = new SSLContextParameters();
        sslContextParameters.setKeyManagers(kmp);

        return sslContextParameters;
    }

    public static String buildBasicAuthToken(String username, String password) {
        String usernamePassword = username + ":" + password;
        String token = new String(Base64.getEncoder().encode(usernamePassword.getBytes(StandardCharsets.UTF_8)));

        return BASIC + token;
    }

}
