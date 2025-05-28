package tak.server.federation.oidf;

public class OpenidFederationServerHolder {

    private static final OpenidFederationServer INSTANCE = new OpenidFederationServer();

    public static OpenidFederationServer getInstance() {
        return INSTANCE;
    }

}
