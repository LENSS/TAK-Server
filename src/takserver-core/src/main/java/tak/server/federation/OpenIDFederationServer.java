package tak.server.federation;

import org.apache.catalina.startup.Tomcat;

public class OpenIDFederationServer {

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080); // Or any port you want

        // Define base directory (used for temp files)
        tomcat.setBaseDir("temp");

        // Set webapp directory (can be a WAR or exploded folder)
        String webappDir = "src/main/webapp";
        tomcat.addWebapp("", new java.io.File(webappDir).getAbsolutePath());

        // Start and keep Tomcat running
        tomcat.start();
        tomcat.getServer().await();
    }

}
