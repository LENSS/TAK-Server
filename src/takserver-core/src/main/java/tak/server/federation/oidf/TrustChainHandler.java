package tak.server.federation.oidf;


import com.nimbusds.openid.connect.sdk.federation.trust.TrustChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tak.server.federation.DistributedFederationManager;


public class TrustChainHandler {

    private static final Logger logger = LoggerFactory.getLogger(TrustChainHandler.class);

    public static void tryResolveFromRemote(String leafAddress) {
        logger.info("Attempting Trust Chain resolution from {}", leafAddress);

        try {
            TrustChainResolutionModule module = OpenidFederationServerHolder.getInstance().initAndGetTcrm();
            module.resolve(leafAddress);

            for (TrustChain chain : module.getResolvedChains()) {
                logger.info("Resolved Trust Chain: {}", chain.toSerializedJWTs());
            }

            if (module.getResolvedChains() != null) {
                for (TrustChain chain : module.getResolvedChains()) {
                    //fetchCertFromTrustAnchors(chain);
                }
            } else {
                logger.info("No valid Trust Chain found.");
            }
        } catch (Exception e) {
            logger.error("Error during Trust Chain resolution from {}: {}", leafAddress, e.toString(), e);
        }
    }

}
