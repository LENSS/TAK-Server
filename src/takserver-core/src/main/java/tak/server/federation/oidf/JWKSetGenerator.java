package tak.server.federation.oidf;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
//import com.nimbusds.jose.util.JSONObjectUtils;

//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
import java.util.*;

public class JWKSetGenerator {

    public static JWKSet generateOIDFJWKSet() throws JOSEException {
        return new JWKSet(new RSAKeyGenerator(2048)
                .keyUse(KeyUse.SIGNATURE) // used for signing
                .keyID(UUID.randomUUID().toString())
                .algorithm(JWSAlgorithm.RS256)
                .generate());
    }

    /*
    // Serialize to JSON including private keys
    public static String jwkSetToJSON(JWKSet jwkSet) {
        return JSONObjectUtils.toJSONString(jwkSet.toJSONObject(false));
    }

    // Encode as Base64 for use in properties
    public static String encodeToBase64(String json) {
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public static void saveToFile(String string, String fileName) throws IOException {
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(string);
        }
    }

    public static String generateAndSaveOIDFJWKSetBase64(String fileName) throws JOSEException, IOException {
        String base64 = encodeToBase64(jwkSetToJSON(generateOIDFJWKSet()));
        saveToFile(base64, fileName);
        return base64;
    }
    */
}
