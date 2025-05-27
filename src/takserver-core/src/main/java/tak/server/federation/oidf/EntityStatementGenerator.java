package tak.server.federation.oidf;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatementClaimsSet;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityType;
import net.minidev.json.JSONObject;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityStatementGenerator {

    private static final Logger logger = LoggerFactory.getLogger(OpenidFederationServer.class);

    private final RSAKey signingJWK;
    private final URI issuer;
    private final JWKSet jwks;

    public EntityStatementGenerator(RSAKey signingJWK, URI issuer, JWKSet jwks) {
        this.signingJWK = signingJWK;
        this.issuer = issuer;
        this.jwks = jwks;
    }

    public SignedJWT generateEntityConfiguration(List<URI> authorityHints, URI federationFetchEndpoint) throws JOSEException, ParseException {
        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plusSeconds(3600));

        JSONObject fedMetadata = new JSONObject();
        fedMetadata.put("federation_fetch_endpoint", federationFetchEndpoint.toString());

        EntityID issuerID = new EntityID(issuer);
        List<EntityID> authorityHintsID = authorityHints.stream()
                .map(EntityID::new)
                .collect(Collectors.toList());

        EntityStatementClaimsSet claims = new EntityStatementClaimsSet(issuerID, issuerID, iat, exp, jwks);
        claims.setAuthorityHints(authorityHintsID);
        claims.setMetadata(EntityType.FEDERATION_ENTITY, fedMetadata);

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(signingJWK.getKeyID()).type(JOSEObjectType.JWT).build();
        SignedJWT jwt = new SignedJWT(header, claims.toJWTClaimsSet());
        jwt.sign(new RSASSASigner(signingJWK.toPrivateKey()));
        return jwt;
    }

    public SignedJWT generateSubordinateStatement(List<URI> authorityHints, URI subject) throws JOSEException, ParseException {
        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plusSeconds(3600));

        EntityID issuerID = new EntityID(issuer);
        EntityID subjectID = new EntityID(subject);
        List<EntityID> authorityHintsID = authorityHints.stream()
                .map(EntityID::new)
                .collect(Collectors.toList());

        EntityStatementClaimsSet claims = new EntityStatementClaimsSet(issuerID, subjectID, iat, exp, jwks);
        claims.setAuthorityHints(authorityHintsID);

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(signingJWK.getKeyID()).type(JOSEObjectType.JWT).build();
        SignedJWT jwt = new SignedJWT(header, claims.toJWTClaimsSet());
        jwt.sign(new RSASSASigner(signingJWK.toPrivateKey()));
        return jwt;
    }
}
