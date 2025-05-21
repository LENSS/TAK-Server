package tak.server.federation.oidf;

import com.nimbusds.jose.JOSEException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.nio.file.*;

public class OIDFServer {

    private static final Logger logger = LoggerFactory.getLogger(OIDFServer.class);

    private final Path startupPath = Paths.get("..", "..", "oidf-server", "tomcat", "bin", "startup.sh").toAbsolutePath().normalize();
    private final Path shutdownPath = Paths.get("..", "..", "oidf-server", "tomcat", "bin", "shutdown.sh").toAbsolutePath().normalize();
    private final Path oidcProviderPath = Paths.get("..", "..", "oidf-server", "tomcat", "webapps", "c2id", "WEB-INF", "oidcProvider.properties").toAbsolutePath().normalize();
    private final Path oidcProviderBackupPath = Paths.get("..", "..", "oidf-server", "tomcat", "webapps", "c2id", "WEB-INF", "oidcProvider-backup.properties").toAbsolutePath().normalize();
    private final Path keyStorePath = Paths.get("..", "..", "oidf-server", "tomcat", "webapps", "c2id", "WEB-INF", "keyStore.properties").toAbsolutePath().normalize();
    private final Path keyStoreBackupPath = Paths.get("..", "..", "oidf-server", "tomcat", "webapps", "c2id", "WEB-INF", "keyStore-backup.properties").toAbsolutePath().normalize();
    private final Path JWKSetPath = Paths.get("..", "..", "oidf-server", "tomcat", "webapps", "c2id", "WEB-INF", "federationKeyStoreJWK.json.b64").toAbsolutePath().normalize();

    private final File oidcProviderPropertiesFile = oidcProviderPath.toFile();
    private final File keyStorePropertiesFile = keyStorePath.toFile();
    private final Properties oidcProviderProperties = new Properties();
    private final Properties keyStoreProperties = new Properties();

    private static final String TRUST_ANCHORS_PREFIX = "op.federation.trustAnchors.";
    private static final String AUTHORITY_HINTS_PREFIX = "op.federation.authorityHints.";
    private static final String ENABLE_FEDERATION_KEY = "op.federation.enable";
    private static final String MAX_PATH_LENGTH_KEY = "op.federation.constraints.maxPathLength";
    private static final String HTTP_CONNECT_TIMEOUT_KEY = "op.federation.httpConnectTimeout";
    private static final String HTTP_READ_TIMEOUT_KEY = "op.federation.httpReadTimeout";
    private static final String TRUST_MARKS_PREFIX = "op.federation.trustMarks.";
    private static final String STATIC_JWK_SET_FEDERATION_KEY = "keyStore.staticJWKSet.federation";

    private boolean isTrustAnchor;
    private boolean hasNoSuperiors;

    public OIDFServer() throws IOException {
        // Loads current properties files and stores them in Properties instances
        loadProperties(oidcProviderProperties, oidcProviderPropertiesFile);
        loadProperties(keyStoreProperties, keyStorePropertiesFile);
        isTrustAnchor = false;
        hasNoSuperiors = false;
    }

    public void start() {
        if (!Files.exists(startupPath)) {
            logger.error("Startup script not found at {}", startupPath);
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(startupPath.toString());
            pb.inheritIO();
            Process p = pb.start();

            logger.info("OpenID Federation server successfully started.");

        } catch (Exception e) {
            logger.error("OpenID Federation server failed to start", e);
        }
    }

    public void stop() {
        if (!Files.exists(shutdownPath)) {
            logger.error("Shutdown script not found at {}", shutdownPath);
            return;
        }

        try{
            storeProperties(oidcProviderProperties, oidcProviderPropertiesFile);
            storeProperties(keyStoreProperties, keyStorePropertiesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(shutdownPath.toString());
            pb.inheritIO();
            Process p = pb.start();

            logger.info("OpenID Federation server shutdown.");

        } catch (Exception e) {
            logger.error("OpenID Federation server failed to shutdown", e);
        }
    }

    public OIDFServer enableOpenIDFederation() {
        oidcProviderProperties.setProperty(ENABLE_FEDERATION_KEY, "true");
        return this;
    }

    public OIDFServer disableOpenIDFederation() {
        oidcProviderProperties.setProperty(ENABLE_FEDERATION_KEY, "false");
        return this;
    }

    public OIDFServer setTrustAnchor(String trustAnchorAddress) {
        String baseUrl = getBaseUrl(trustAnchorAddress);

        if (isNotDuplicate(TRUST_ANCHORS_PREFIX, baseUrl)) {
            int index = 1;
            String key = TRUST_ANCHORS_PREFIX + index;

            while (oidcProviderProperties.containsKey(key) && !oidcProviderProperties.getProperty(key).trim().isEmpty()) {
                index++;
                key = TRUST_ANCHORS_PREFIX + index;
            }

            oidcProviderProperties.setProperty(key, baseUrl);
        } else {
            logger.info("{} is already registered as a Trust Anchor.", trustAnchorAddress);
        }

        return this;
    }

    public OIDFServer setTrustAnchors(List<String> trustAnchors) {
        int index = 1;

        for (String address : trustAnchors) {
            String baseUrl = getBaseUrl(address);

            if (isNotDuplicate(TRUST_ANCHORS_PREFIX, baseUrl)) {
                String key = TRUST_ANCHORS_PREFIX + index;
                while (oidcProviderProperties.containsKey(key) && !oidcProviderProperties.getProperty(key).trim().isEmpty()) {
                    index++;
                    key = TRUST_ANCHORS_PREFIX + index;
                }

                oidcProviderProperties.setProperty(key, baseUrl);
                index++; // advance to look for next slot
            } else {
                logger.info("{} is already registered as a Trust Anchor.", address);
            }
        }

        return this;
    }

    public OIDFServer removeTrustAnchor(String trustAnchorAddress) {
        boolean found = false;

        for (String key : oidcProviderProperties.stringPropertyNames()) {
            if (key.startsWith(TRUST_ANCHORS_PREFIX)) {
                String value = oidcProviderProperties.getProperty(key);
                if (getBaseUrl(trustAnchorAddress).equals(value)) {
                    oidcProviderProperties.setProperty(key, "");
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            logger.info("{} is not registered as a Trust Anchor.", trustAnchorAddress);
        }

        return this;
    }

    public OIDFServer setAuthorityHint(String authorityHintAddress) {
        if (isTrustAnchor && hasNoSuperiors) {
            logger.info("Trust Anchors without Superiors cannot have authority hints.");
            return this;
        }

        String baseUrl = getBaseUrl(authorityHintAddress);

        if (isNotDuplicate(AUTHORITY_HINTS_PREFIX, baseUrl)) {
            int index = 1;
            String key = AUTHORITY_HINTS_PREFIX + index;

            while (oidcProviderProperties.containsKey(key) && !oidcProviderProperties.getProperty(key).trim().isEmpty()) {
                index++;
               key = AUTHORITY_HINTS_PREFIX + index;
           }

            oidcProviderProperties.setProperty(key, baseUrl);
        } else {
            logger.info("{} is already registered as an authority.", authorityHintAddress);
        }

        return this;
    }

    public OIDFServer setAuthorityHints(List<String> authorityHints) {
        if (isTrustAnchor && hasNoSuperiors) {
            logger.info("Trust Anchors without Superiors cannot have authority hints.");
            return this;
        }

        int index = 1;

        for (String address : authorityHints) {
            String baseUrl = getBaseUrl(address);

            if (isNotDuplicate(AUTHORITY_HINTS_PREFIX, baseUrl)) {
                String key = AUTHORITY_HINTS_PREFIX + index;
                while (oidcProviderProperties.containsKey(key) && !oidcProviderProperties.getProperty(key).trim().isEmpty()) {
                    index++;
                    key = AUTHORITY_HINTS_PREFIX + index;
                }

                oidcProviderProperties.setProperty(key, baseUrl);
                index++; // advance to look for next slot
            } else {
                logger.info("{} is already registered as an authority.", address);
            }
        }

        return this;
    }

    public OIDFServer removeAuthorityHint(String authorityHintAddress) {
        boolean found = false;

        for (String key : oidcProviderProperties.stringPropertyNames()) {
            if (key.startsWith(AUTHORITY_HINTS_PREFIX)) {
                String value = oidcProviderProperties.getProperty(key);
                if (getBaseUrl(authorityHintAddress).equals(value)) {
                    oidcProviderProperties.setProperty(key, "");
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            logger.info("{} is not registered as an authority.", authorityHintAddress);
        }

        return this;
    }

    // The maximum path length when resolving trust chains. The default value is 2 (up to two intermediates to the trust anchor).
    public OIDFServer setMaxPathLength(int maxPathLength) {
        oidcProviderProperties.setProperty(MAX_PATH_LENGTH_KEY, String.valueOf(maxPathLength));
        return this;
    }

    // The HTTP connect timeout (in milliseconds) when resolving trust chains. Zero implies no timeout. The default value is 1000.
    public OIDFServer setHttpConnectTimeout(int httpConnectTimeout) {
        oidcProviderProperties.setProperty(HTTP_CONNECT_TIMEOUT_KEY, String.valueOf(httpConnectTimeout));
        return this;
    }

    // The HTTP read timeout (in milliseconds) when resolving trust chains. Zero implies no timeout. The default value is 1000.
    public OIDFServer setHttpReadTimeout(int httpReadTimeout) {
        oidcProviderProperties.setProperty(HTTP_READ_TIMEOUT_KEY, String.valueOf(httpReadTimeout));
        return this;
    }

    public OIDFServer setTrustMark(String trustMark) {
        if (isNotDuplicate(TRUST_MARKS_PREFIX, trustMark)) {
            int index = 1;
            String key = TRUST_MARKS_PREFIX + index;

            while (oidcProviderProperties.containsKey(key) && !oidcProviderProperties.getProperty(key).trim().isEmpty()) {
                index++;
                key = TRUST_MARKS_PREFIX + index;
            }

            oidcProviderProperties.setProperty(key, trustMark);
        } else {
            logger.info("{} is already registered as a Trust Mark.", trustMark);
        }

        return this;
    }

    public OIDFServer setTrustMarks(List<String> trustMarks) {
        int index = 1;

        for (String trustMark : trustMarks) {
            if (isNotDuplicate(TRUST_MARKS_PREFIX, trustMark)) {
                String key = TRUST_MARKS_PREFIX + index;
                while (oidcProviderProperties.containsKey(key) && !oidcProviderProperties.getProperty(key).trim().isEmpty()) {
                    index++;
                    key = TRUST_MARKS_PREFIX + index;
                }

                oidcProviderProperties.setProperty(key, trustMark);
                index++; // advance to look for next slot
            } else {
                logger.info("{} is already registered as a Trust Mark.", trustMark);
            }
        }

        return this;
    }

    public OIDFServer removeTrustMark(String trustMark) {
        boolean found = false;

        for (String key : oidcProviderProperties.stringPropertyNames()) {
            if (key.startsWith(TRUST_MARKS_PREFIX)) {
                String value = oidcProviderProperties.getProperty(key);
                if (trustMark.equals(value)) {
                    oidcProviderProperties.setProperty(key, "");
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            logger.info("{} is not registered as a Trust Mark", trustMark);
        }

        return this;
    }

    public OIDFServer setJWKSet() throws JOSEException, IOException {
        String json = JWKSetGenerator.generateAndSaveOIDFJWKSetBase64(JWKSetPath.toString());
        keyStoreProperties.setProperty(STATIC_JWK_SET_FEDERATION_KEY, json);
        return this;
    }

    private OIDFServer setThisAsTrustAnchor() {
        isTrustAnchor = true;
        return this;
    }

    private OIDFServer setThisAsNotTrustAnchor() {
        isTrustAnchor = false;
        return this;
    }

    private OIDFServer setThisHasNoSuperiors() {
        hasNoSuperiors = true;
        return this;
    }

    private OIDFServer setThisHasSuperiors() {
        hasNoSuperiors = false;
        return this;
    }

    public OIDFServer resetOidcProviderProperties() throws IOException {
        Properties backupProperties = new Properties();
        File backupFile = oidcProviderBackupPath.toFile();

        try (FileInputStream in = new FileInputStream(backupFile)) {
            backupProperties.load(in);
        }

        oidcProviderProperties.clear();
        oidcProviderProperties.putAll(backupProperties);

        try (FileOutputStream out = new FileOutputStream(oidcProviderPropertiesFile, false)) {
            backupProperties.store(out, "Updated by OIDFServer");
            logger.info("Reset {} by OIDFServer.", oidcProviderPropertiesFile);
        }

        return this;
    }

    public OIDFServer resetKeyStoreProperties() throws IOException {
        Properties backupProperties = new Properties();
        File backupFile = keyStoreBackupPath.toFile();

        try (FileInputStream in = new FileInputStream(backupFile)) {
            backupProperties.load(in);
        }

        keyStoreProperties.clear();
        keyStoreProperties.putAll(backupProperties);

        try (FileOutputStream out = new FileOutputStream(keyStorePropertiesFile, false)) {
            backupProperties.store(out, "Updated by OIDFServer");
            logger.info("Reset {} by OIDFServer.", keyStorePropertiesFile);
        }

        return this;
    }

    public void loadProperties(@NotNull Properties properties, File propertiesFile) throws IOException {
        try (FileInputStream in = new FileInputStream(propertiesFile)) {
            properties.load(in);
        }
    }

    public void storeProperties(@NotNull Properties properties, File propertiesFile) throws IOException {
        try (FileOutputStream out = new FileOutputStream(propertiesFile)) {
            properties.store(out, "Updated by OIDFServer");
            logger.info("Updated {} by OIDFServer.", propertiesFile);
        }
    }

    private boolean isNotDuplicate(String keyPrefix, String value) {
        for (String key : oidcProviderProperties.stringPropertyNames()) {
            if (key.startsWith(keyPrefix)) {
                if (value.equals(oidcProviderProperties.getProperty(key).trim())) {
                    return false;
                }
            }
        }

        return true;
    }

    private String getBaseUrl(String address) {
        return "http://" + address + ":8080/c2id";
    }

}
