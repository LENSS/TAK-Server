package tak.server.federation;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.openid.connect.sdk.federation.entities.*;
import com.nimbusds.openid.connect.sdk.federation.trust.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrustChainResolutionModule {

    private EntityID trustAnchor;
    private Map<EntityID, JWKSet> trustAnchors = new ConcurrentHashMap<>();
    private EntityID leafEntity;

    private TakTrustChainResolver resolver;
    private TrustChainSet resolvedChains;
    private TrustChain shortestResolvedChain;
    private List<String> trustChainJWT;
    private boolean resolutionSuccess = false;

    private static final Logger logger = LoggerFactory.getLogger(TrustChainResolutionModule.class);

    // Trust Chain Resolution Module based on a single Trust Anchor
    public TrustChainResolutionModule(String trustAnchorAddress) throws Exception {
        String addr = getBaseUrl(trustAnchorAddress);
        trustAnchor = new EntityID(addr);
        JWKSet trustAnchorJWKSet = JWKFetcher.fetch(addr + "/jwks.json");
        trustAnchors.put(trustAnchor, trustAnchorJWKSet);
        resolver = new TakTrustChainResolver(trustAnchors, 10000, 10000);
    }

    // Trust Chain Resolution Module based on a Trust Anchor set
    public TrustChainResolutionModule(Map<EntityID, JWKSet> trustAnchors) throws Exception {
        this.trustAnchors = trustAnchors;
        resolver = new TakTrustChainResolver(trustAnchors, DefaultEntityStatementRetriever.DEFAULT_HTTP_CONNECT_TIMEOUT_MS, DefaultEntityStatementRetriever.DEFAULT_HTTP_READ_TIMEOUT_MS);
    }

    public void resolve(String leafAddress) {
        leafEntity = new EntityID(getBaseUrl(leafAddress));
        try {
            resolvedChains = resolver.resolveTrustChains(leafEntity);
            shortestResolvedChain = resolvedChains.getShortest();
            trustChainJWT = shortestResolvedChain.toSerializedJWTs();
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

    public List<String> getTrustChainJWT() {
        return trustChainJWT;
    }

    public boolean isResolved() {
        return resolutionSuccess;
    }

    private String getBaseUrl(String address) {
        return "http://" + address + ":8080/c2id";
    }

}