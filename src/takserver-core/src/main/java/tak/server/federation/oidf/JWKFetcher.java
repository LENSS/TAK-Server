package tak.server.federation.oidf;

import com.nimbusds.jose.jwk.JWKSet;

import java.io.InputStream;
import java.net.URL;

public class JWKFetcher {
    public static JWKSet fetch(String jwksUri) throws Exception {
        URL url = new URL(jwksUri);
        try (InputStream is = url.openStream()) {
            return JWKSet.load(is);
        }
    }
}

