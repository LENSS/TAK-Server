package tak.server.federation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenIDFederationServer {

    private static final Logger logger = LoggerFactory.getLogger(OpenIDFederationServer.class);

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

        try {
            ProcessBuilder pb = new ProcessBuilder("../../oidf-server/tomcat/bin/shutdown.sh");
            pb.inheritIO();
            Process p = pb.start();

            logger.info("OpenID Federation server shutdown.");

        } catch (Exception e) {
            logger.error("OpenID Federation server failed to shutdown", e);
        }

    }

}
