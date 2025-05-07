package tak.server.federation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nimbusds.openid.connect.sdk.federation.entities.*;
import com.nimbusds.openid.connect.sdk.federation.policy.*;
import com.nimbusds.openid.connect.sdk.federation.trust.*;
import com.nimbusds.openid.connect.sdk.federation.trust.ResolveException;

public class TrustChainResolutionModule {


    EntityID trustAnchor;

    EntityID leafEntity;

    TrustChainResolver resolver;

    TrustChainSet resolvedChains;

    private static final Logger logger = LoggerFactory.getLogger(TrustChainResolutionModule.class);

    public TrustChainResolutionModule(String trustAnchorAddress, String leafAddress) {
        trustAnchor = new EntityID(trustAnchorAddress);
        leafEntity = new EntityID(leafAddress);
        resolver = new TrustChainResolver(trustAnchor);
    }

    private void resolve() {
        try {
            resolvedChains = resolver.resolveTrustChains(leafEntity);
        } catch (ResolveException e) {
            // Couldn't resolve a valid trust chain
            System.err.println(e.getMessage());
            return;
        }
    }

    private TrustChainSet getResolvedChains() {
        return resolvedChains;
    }

    private TrustChain getShortestResolvedChain() {
        return resolvedChains.getShortest();
    }

    private MetadataPolicy getMetadataPolicy() {
        return resolvedChains.getShortest();
    }
        // Get the policy for registering a relying party with the OpenID provider
        MetadataPolicy metadataPolicy = chain.resolveCombinedMetadataPolicy();
        System.out.println(metadataPolicy.toJSONObject());
    }


}