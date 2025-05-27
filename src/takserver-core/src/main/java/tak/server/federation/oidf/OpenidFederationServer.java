package tak.server.federation.oidf;


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
import java.util.Properties;
import java.util.stream.Stream;

import com.nimbusds.jose.jwk.*;

public class OpenidFederationServer {

    private static final Logger logger = LoggerFactory.getLogger(OpenidFederationServer.class);

    private Server server;

    private List<URI> authorityHints;

    private URI federationFetchEndpoint;

    private EntityStatementGenerator generator;

    public OpenidFederationServer() { }

    public OpenidFederationServer(Server server) {
        this.server = server;
    }

    public OpenidFederationServer setup() throws Exception {

        Properties props = new Properties();
        Path propertiesPath = Paths.get("..", "src", "main", "java", "tak", "server", "federation", "oidf", "federationEntity.properties").toAbsolutePath().normalize();
        try (FileInputStream in = new FileInputStream(propertiesPath.toString())) {
            props.load(in);
        }

        int port = Integer.parseInt(props.getProperty("server.port"));

        URI issuer = new URI(props.getProperty("issuer"));
        federationFetchEndpoint = issuer.resolve("/fetch");
        if (logger.isDebugEnabled()) {
            logger.debug("Set issuer: {}", issuer);
            logger.debug("Set federation_fetch_endpoint: {}", federationFetchEndpoint);
        }

        String authorityHintsString = props.getProperty("authorityHints", "").trim();
        authorityHints = authorityHintsString.isEmpty() ? List.of() :
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

        generator = new EntityStatementGenerator(rsaKey, issuer, jwks);

        ServletContextHandler handler = new ServletContextHandler();

        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(new FederationEndpointServlets.EntityConfigurationServlet(generator, authorityHints, federationFetchEndpoint)), "/.well-known/openid-federation");
        handler.addServlet(new ServletHolder(new FederationEndpointServlets.FederationFetchServlet(generator, authorityHints)), "/fetch");
        handler.addServlet(new ServletHolder(new FederationEndpointServlets.JWKSServlet(rsaKey)), "/jwks.json");

        Server jettyServer = new Server(port);
        ServerConnector connector = new ServerConnector(jettyServer);
        connector.setHost("0.0.0.0"); // bind to all interfaces
        connector.setPort(port);
        jettyServer.addConnector(connector);
        jettyServer.setHandler(handler);

        return new OpenidFederationServer(jettyServer);
    }

    public void start() throws Exception {
        try {
            server.start();
            logger.info("OpenID Federation Server started.");
        } catch (Exception e) {
            logger.error("Failed to start OpenID Federation Server: {}", e.getMessage());
            throw e;
        }
    }

    public void stop() throws Exception {
        try {
            server.stop();
            logger.info("OpenID Federation Server stopped.");
        } catch (Exception e) {
            logger.error("Exception while stopping OpenID Federation Server: {}", e.getMessage());
            throw e;
        }
    }

}
