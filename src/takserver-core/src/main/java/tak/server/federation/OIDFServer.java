package tak.server.federation;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;

public class OIDFServer {

    private static final Logger logger = LoggerFactory.getLogger(OIDFServer.class);

    private final File oidcProviderPropertiesFile = new File("../../oidf-server/tomcat/webapps/c2id/WEB-INF/oidcProvider.properties");
    private final File keyStorePropertiesFile = new File("../../oidf-server/tomcat/webapps/c2id/WEB-INF/keyStore.properties");
    private Properties oidcProviderProperties = new Properties();
    private Properties keyStoreProperties = new Properties();

    private static final String TRUST_ANCHORS_PREFIX = "op.federation.trustAnchors.";
    private static final String AUTHORITY_HINTS_PREFIX = "op.federation.authorityHints.";
    private static final String ENABLE_FEDERATION_VALUE = "op.federation.enable";
    private static final String MAX_PATH_LENGTH_VALUE = "op.federation.constraints.maxPathLength";

    public OIDFServer() throws IOException {
        // Loads current properties files and stores them in Properties instances
        loadProperties(oidcProviderProperties, oidcProviderPropertiesFile);
        loadProperties(keyStoreProperties, keyStorePropertiesFile);
    }

    public void start() {
        try {
            ProcessBuilder pb = new ProcessBuilder("../../oidf-server/tomcat/bin/startup.sh");
            pb.inheritIO();
            Process p = pb.start();

            logger.info("OpenID Federation server successfully started.");

        } catch (Exception e) {
            logger.error("OpenID Federation server failed to start", e);
        }
    }

    public void stop() {
        try{
            storeProperties(oidcProviderProperties, oidcProviderPropertiesFile);
            storeProperties(keyStoreProperties, keyStorePropertiesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("../../oidf-server/tomcat/bin/shutdown.sh");
            pb.inheritIO();
            Process p = pb.start();

            logger.info("OpenID Federation server shutdown.");

        } catch (Exception e) {
            logger.error("OpenID Federation server failed to shutdown", e);
        }
    }

    public void enableOpenIDFederation() {
        oidcProviderProperties.setProperty(ENABLE_FEDERATION_VALUE, "true");
    }

    public void disableOpenIDFederation() {
        oidcProviderProperties.setProperty(ENABLE_FEDERATION_VALUE, "false");
    }

    public void setTrustAnchor(String trustAnchorAddress) {
        if(isNotDuplicateAddress(TRUST_ANCHORS_PREFIX, trustAnchorAddress)) {
            int index = 1;
            String key = TRUST_ANCHORS_PREFIX + index;

            while (oidcProviderProperties.containsKey(key) && !oidcProviderProperties.getProperty(key).trim().isEmpty()) {
                index++;
                key = TRUST_ANCHORS_PREFIX + index;
            }

            oidcProviderProperties.setProperty(key, getBaseUrl(trustAnchorAddress));
        } else {
            logger.info("{} is already registered as a Trust Anchor.", trustAnchorAddress);
        }
    }

    public void setTrustAnchors(List<String> trustAnchors) {
        int index = 1;

        for (String address : trustAnchors) {
            if(isNotDuplicateAddress(TRUST_ANCHORS_PREFIX, address)) {
                String key = TRUST_ANCHORS_PREFIX + index;
                while (oidcProviderProperties.containsKey(key) && !oidcProviderProperties.getProperty(key).trim().isEmpty()) {
                    index++;
                    key = TRUST_ANCHORS_PREFIX + index;
                }

                oidcProviderProperties.setProperty(key, getBaseUrl(address));
                index++; // advance to look for next slot
            } else {
                logger.info("{} is already registered as a Trust Anchor.", address);
            }
        }
    }

    public void removeTrustAnchor(String trustAnchorAddress) {
        for (String key : oidcProviderProperties.stringPropertyNames()) {
            if (key.startsWith(TRUST_ANCHORS_PREFIX)) {
                String value = oidcProviderProperties.getProperty(key);
                if (getBaseUrl(trustAnchorAddress).equals(value)) {
                    oidcProviderProperties.setProperty(key, "");
                } else {
                    logger.info("{} is not registered as a Trust Anchor.", trustAnchorAddress);
                }
            }
        }
    }

    public void setAuthorityHint(String authorityHintAddress) {
        if(isNotDuplicateAddress(AUTHORITY_HINTS_PREFIX, authorityHintAddress)) {
            int index = 1;
            String key = AUTHORITY_HINTS_PREFIX + index;

            while (oidcProviderProperties.containsKey(key) && !oidcProviderProperties.getProperty(key).trim().isEmpty()) {
                index++;
               key = AUTHORITY_HINTS_PREFIX + index;
           }

            oidcProviderProperties.setProperty(key, getBaseUrl(authorityHintAddress));
        } else {
            logger.info("{} is already registered as an authority.", authorityHintAddress);
        }
    }

    public void setAuthorityHints(List<String> authorityHints) {
        int index = 1;

        for (String address : authorityHints) {
            if(isNotDuplicateAddress(AUTHORITY_HINTS_PREFIX, address)) {
                String key = AUTHORITY_HINTS_PREFIX + index;
                while (oidcProviderProperties.containsKey(key) && !oidcProviderProperties.getProperty(key).trim().isEmpty()) {
                    index++;
                    key = AUTHORITY_HINTS_PREFIX + index;
                }

                oidcProviderProperties.setProperty(key, getBaseUrl(address));
                index++; // advance to look for next slot
            } else {
                logger.info("{} is already registered as an authority.", address);
            }
        }
    }

    public void removeAuthorityHint(String authorityHintAddress) {
        for (String key : oidcProviderProperties.stringPropertyNames()) {
            if (key.startsWith(AUTHORITY_HINTS_PREFIX)) {
                String value = oidcProviderProperties.getProperty(key);
                if (getBaseUrl(authorityHintAddress).equals(value)) {
                    oidcProviderProperties.setProperty(key, "");
                } else {
                    logger.info("{} is not registered as an authority.", authorityHintAddress);
                }
            }
        }
    }

    public void setMaxPathLength(int maxPathLength) {
        oidcProviderProperties.setProperty(MAX_PATH_LENGTH_VALUE, String.valueOf(maxPathLength));
    }

    public void resetOidcProviderProperties() throws IOException {
        Properties backupProperties = new Properties();
        File backupFile = new File("../../oidf-server/tomcat/webapps/c2id/WEB-INF/oidcProvider-backup.properties");

        try (FileInputStream in = new FileInputStream(backupFile)) {
            backupProperties.load(in);
        }

        oidcProviderProperties = backupProperties;

        try (FileOutputStream out = new FileOutputStream(oidcProviderPropertiesFile, false)) {
            backupProperties.store(out, "Updated by OIDFServer");
            logger.info("Reset {} by OIDFServer.", oidcProviderPropertiesFile);
        }
    }

    public void resetKeyStoreProperties() throws IOException {
        Properties backupProperties = new Properties();
        File backupFile = new File("../../oidf-server/tomcat/webapps/c2id/WEB-INF/keyStore-backup.properties");

        try (FileInputStream in = new FileInputStream(backupFile)) {
            backupProperties.load(in);
        }

        keyStoreProperties = backupProperties;

        try (FileOutputStream out = new FileOutputStream(keyStorePropertiesFile, false)) {
            backupProperties.store(out, "Updated by OIDFServer");
            logger.info("Reset {} by OIDFServer.", keyStorePropertiesFile);
        }
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

    private boolean isNotDuplicateAddress(String keyPrefix, String address) {
        for (String key : oidcProviderProperties.stringPropertyNames()) {
            if (key.startsWith(keyPrefix)) {
                if (getBaseUrl(address).equals(oidcProviderProperties.getProperty(key).trim())) {
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
