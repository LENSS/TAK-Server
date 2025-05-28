package tak.server.federation.oidf;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.openid.connect.sdk.federation.entities.*;
import com.nimbusds.openid.connect.sdk.federation.trust.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class TrustChainResolutionModule {

    private final TakTrustChainResolver resolver;
    private TrustChainSet resolvedChains;
    private TrustChain shortestResolvedChain;
    private List<String> shortestTrustChainJWT;
    private boolean resolutionSuccess = false;

    private static final Logger logger = LoggerFactory.getLogger(TrustChainResolutionModule.class);

    // Trust Chain Resolution Module based on a Trust Anchor set
    public TrustChainResolutionModule(Map<EntityID, JWKSet> trustAnchorSet) {
        resolver = new TakTrustChainResolver(trustAnchorSet, 10000, 10000);

        if (logger.isDebugEnabled()) {
            logger.debug("Constructing new module using Trust Anchors: {}", trustAnchorSet);
            logger.debug("Created Trust Chain Resolver: {},", resolver);
        }
    }

    public void resolve(String leafAddress) {
        EntityID leafEntity = new EntityID(leafAddress);
        if (logger.isDebugEnabled()) {
            logger.debug("Leaf Entity address: {}", leafEntity);
        }
        try {
            resolvedChains = resolver.resolveTrustChains(leafEntity);
            shortestResolvedChain = resolvedChains.getShortest();
            shortestTrustChainJWT = shortestResolvedChain.toSerializedJWTs();
            resolutionSuccess = true;
        } catch (ResolveException e) {
            logger.error("Error while resolving Trust Chains", e);
            resolutionSuccess = false;
        }
    }

    public TrustChainSet getResolvedChains() {
        return resolvedChains;
    }

    public TrustChain getShortestResolvedChain() {
        return shortestResolvedChain;
    }

    public List<String> getShortestTrustChainJWT() {
        return shortestTrustChainJWT;
    }

    public boolean isResolved() {
        return resolutionSuccess;
    }

}