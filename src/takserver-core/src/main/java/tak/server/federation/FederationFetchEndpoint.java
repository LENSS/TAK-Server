package tak.server.federation;

import jakarta.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.IOException;

public class FederationFetchEndpoint {

    private Server server;

    public void start(int port) throws Exception {
        server = new Server(port);

        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(FetchServlet.class, "/fetch");

        server.setHandler(handler);
        server.start();
    }

    public void stop() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    public static class FetchServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String iss = req.getParameter("iss");
            String sub = req.getParameter("sub");

            if (iss == null || sub == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing 'iss' or 'sub' parameter.");
                return;
            }

            // TODO: Replace with actual JWT generation logic
            String jwt = "mocked.jwt.token.for.subject." + sub;

            resp.setContentType("application/jwt");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(jwt);
        }
    }
}
