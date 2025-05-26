package tak.server.federation.oidf;


import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.JSONObjectUtils;
import jakarta.servlet.http.*;
import com.nimbusds.jwt.SignedJWT;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FederationEndpointServlets {

    private static final Logger logger = LoggerFactory.getLogger(OpenidFederationServer.class);

    public static class EntityConfigurationServlet extends HttpServlet {

        private final EntityStatementGenerator generator;
        private final List<URI> authorityHints;
        private final URI fetchEndpoint;

        public EntityConfigurationServlet(EntityStatementGenerator generator, List<URI> authorityHints, URI fetchEndpoint) {
            this.generator = generator;
            this.authorityHints = authorityHints;
            this.fetchEndpoint = fetchEndpoint;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            try {
                SignedJWT entityConfig = generator.generateEntityConfiguration(authorityHints, fetchEndpoint);
                if (logger.isDebugEnabled()) {
                    logger.debug("Entity Configuration generated: {}", entityConfig);
                }
                resp.setContentType("application/entity-statement+jwt");
                resp.getWriter().write(entityConfig.serialize());
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Error generating entity configuration: " + e.getMessage());
            }
        }
    }

    public static class FederationFetchServlet extends HttpServlet {

        private final EntityStatementGenerator generator;
        private final List<URI> authorityHints;

        public FederationFetchServlet(EntityStatementGenerator generator, List<URI> authorityHints) {
            this.generator = generator;
            this.authorityHints = authorityHints;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String sub = req.getParameter("sub");
            if (sub == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing 'sub' parameter");
                return;
            }

            try {
                URI subject = new URI(sub);
                SignedJWT subordinateStatement = generator.generateSubordinateStatement(authorityHints, subject);
                if (logger.isDebugEnabled()) {
                    logger.debug("Subordinate Statement generated: {}", subordinateStatement);
                }
                resp.setContentType("application/entity-statement+jwt");
                resp.getWriter().write(subordinateStatement.serialize());
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Error generating subordinate statement: " + e.getMessage());
            }
        }
    }

    public static class JWKSServlet extends HttpServlet {

        private final JWKSet jwks;

        public JWKSServlet(RSAKey privateKey) {
            this.jwks = new JWKSet(privateKey.toPublicJWK());
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("application/json");
            resp.getWriter().write(JSONObjectUtils.toJSONString(jwks.toJSONObject(false)));
            if (logger.isDebugEnabled()) {
                logger.debug("JWK Set generated: {}", jwks);
            }
        }
    }
}
