package tak.server.federation;
import java.io.File;

import org.apache.catalina.startup.Tomcat;

public class OpenIDFederationServer {

    private Tomcat tomcat;


    public void start() throws Exception {
        if (tomcat != null) {
            return; // already started
        }

        tomcat = new Tomcat();
        tomcat.setPort(8080); // Or any port you want

        // Define base directory (used for temp files)
        tomcat.setBaseDir("../../oidf-server/tomcat/temp");

        // Set webapp directory (can be a WAR or exploded folder)
        File webappDir = new File("../../oidf-server/tomcat/webapps");
        tomcat.addWebapp("/", webappDir.getAbsolutePath());

        System.out.println("Starting embedded OpenID Federation server on port 8080 from " + System.getProperty("user.dir"));
        tomcat.start();

        // Run in non-blocking thread
        new Thread(() -> tomcat.getServer().await()).start();
    }

    public void stop() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat = null;
        }
    }
}
