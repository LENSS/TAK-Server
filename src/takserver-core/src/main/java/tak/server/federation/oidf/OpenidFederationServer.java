package tak.server.federation.oidf;


import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.nimbusds.jose.jwk.*;

public class OpenidFederationServer {

    private static final Logger logger = LoggerFactory.getLogger(OpenidFederationServer.class);

    private Server server;

    private boolean started = false;

    private int port;

    private List<URI> authorityHints;

    private List<URI> trustAnchors;

    private TrustChainResolutionModule trustChainResolutionModule;

    public void setup() throws Exception {

        Properties props = new Properties();
        Path propertiesPath = Paths.get("..", "src", "main", "java", "tak", "server", "federation", "oidf", "oidfServer.properties").toAbsolutePath().normalize();
        try (FileInputStream in = new FileInputStream(propertiesPath.toString())) {
            props.load(in);
        }

        this.port = Integer.parseInt(props.getProperty("server.port"));

        URI issuer = new URI(props.getProperty("issuer"));
        URI federationFetchEndpoint = issuer.resolve("/fetch");

        if (logger.isDebugEnabled()) {
            logger.debug("Set issuer: {}", issuer);
            logger.debug("Set federation_fetch_endpoint: {}", federationFetchEndpoint);
        }

        String trustAnchorsString = props.getProperty("trustAnchors", "").trim();
        this.trustAnchors = trustAnchorsString.isEmpty() ? List.of() :
                Stream.of(trustAnchorsString.split(",")).map(String::trim).map(URI::create).toList();

        if (logger.isDebugEnabled()) {
            logger.debug("Set trust anchors: {}", trustAnchors);
        }

        String authorityHintsString = props.getProperty("authorityHints", "").trim();
        this.authorityHints = authorityHintsString.isEmpty() ? List.of() :
                Stream.of(authorityHintsString.split(",")).map(String::trim).map(URI::create).toList();

        if (logger.isDebugEnabled()) {
            logger.debug("Set authority_hints: {}", authorityHints);
        }

        JWKSet jwks = JWKSetGenerator.generateOIDFJWKSet();
        if (logger.isDebugEnabled()) {
            logger.debug("Set jwks: {}", jwks);
        }

        RSAKey rsaKey = null;
        for (JWK jwk : jwks.getKeys()) {
            try {
                if (jwk instanceof RSAKey && jwk.isPrivate()) {
                    rsaKey = (RSAKey) jwk;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        EntityStatementGenerator generator = new EntityStatementGenerator(rsaKey, issuer, jwks);

        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(new FederationEndpointServlets.EntityConfigurationServlet(generator, authorityHints, federationFetchEndpoint)), "/.well-known/openid-federation");
        handler.addServlet(new ServletHolder(new FederationEndpointServlets.FederationFetchServlet(generator, authorityHints)), "/fetch");
        handler.addServlet(new ServletHolder(new FederationEndpointServlets.JWKSServlet(Objects.requireNonNull(rsaKey))), "/jwks.json");

        Server jettyServer = new Server();
        ServerConnector connector = new ServerConnector(jettyServer);
        connector.setHost("0.0.0.0"); // bind to all interfaces
        connector.setPort(this.port);
        jettyServer.addConnector(connector);
        jettyServer.setHandler(handler);

        this.server = jettyServer;
    }

    public void start() throws Exception {
        if (started) return;

        try {
            server.start();
            started = true;
            logger.info("OpenID Federation server started at {}", server.getURI());
        } catch (Exception e) {
            logger.error("Port {} already in use. Choose a different one.", port, e);
            throw e;
        }
    }

    public void stop() throws Exception {
        try {
            server.stop();
            started = false;
            logger.info("OpenID Federation Server stopped.");
        } catch (Exception e) {
            logger.error("Exception while stopping OpenID Federation Server: {}", e.getMessage());
            throw e;
        }
    }

    public TrustChainResolutionModule initAndGetTcrm() throws Exception {

        Map<EntityID, JWKSet> trustAnchorSet = new ConcurrentHashMap<>();
        for (URI trustAnchorURI : trustAnchors) {
            EntityID trustAnchor = new EntityID(trustAnchorURI);
            JWKSet trustAnchorJWKSet = JWKFetcher.fetch(trustAnchorURI + "/jwks.json");
            trustAnchorSet.put(trustAnchor, trustAnchorJWKSet);
        }

        trustChainResolutionModule = new TrustChainResolutionModule(trustAnchorSet);

        return trustChainResolutionModule;
    }

}
