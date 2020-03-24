package net.dashflight.data.jwt.create.request;

import com.google.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;
import net.dashflight.data.jwt.FingerprintService;
import net.dashflight.data.keys.RSAKeyPairProvider;

/**
 * Used to generate jwts for authentication with dashflight
 */
class DashflightCreateJwtRequestProvider implements CreateJwtRequestProvider, Configurable {

    @ConfigValue("issuer")
    private static String ISSUER;

    @ConfigValue("access_token_ttl")
    private static int TOKEN_TTL;

    private final FingerprintService fingerprintService;
    private final RSAKeyPairProvider keyManager;


    @Inject
    public DashflightCreateJwtRequestProvider(FingerprintService fingerprintService, RSAKeyPairProvider keyPairProvider) {
        registerWith("jwt-utils");

        this.fingerprintService = fingerprintService;
        this.keyManager = keyPairProvider;
    }


    @Override
    public CreateJwtRequest create(String userId) {
        String fingerprint = fingerprintService.generateRandomFingerprint();

        Map<String, String> claims = new HashMap<>();

        claims.put("user_id", userId);
        claims.put("user_fingerprint", fingerprintService.hashFingerprint(fingerprint));

        return CreateJwtRequest.builder()
                .issuer(ISSUER)
                .issuedAt(Instant.now())
                .ttl(TOKEN_TTL)
                .claims(claims)
                .privateKey(keyManager.getPrivateKey())
                .build();
    }
}
