package tak.server.federation.oidf;

import jakarta.servlet.http.*;
import com.nimbusds.jwt.SignedJWT;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class FederationEndpointServlets {

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
                resp.setContentType("application/entity-statement+jwt");
                resp.getWriter().write(subordinateStatement.serialize());
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Error generating subordinate statement: " + e.getMessage());
            }
        }
    }
}
