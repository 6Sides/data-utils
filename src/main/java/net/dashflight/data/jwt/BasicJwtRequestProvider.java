package net.dashflight.data.jwt;

import java.time.Instant;
import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;
import net.dashflight.data.keys.DefaultRSAKeyManager;
import net.dashflight.data.keys.RSAKeyManagerFactory;

public class BasicJwtRequestProvider implements JwtRequestProvider, Configurable {

    @ConfigValue("issuer")
    private static String ISSUER = "https://api.dashflight.net";

    @ConfigValue("access_token_ttl")
    private static int TOKEN_TTL;

    private final FingerprintService fingerprintService = new FingerprintService();

    private final DefaultRSAKeyManager keyManager = RSAKeyManagerFactory.withDefaults();


    public BasicJwtRequestProvider() {
        registerWith("jwt-utils");
    }


    @Override
    public CreateJwtRequest create(String userId) {
        return CreateJwtRequest.builder()
                .issuer(ISSUER)
                .ttl(TOKEN_TTL)
                .fingerprint(fingerprintService.generateRandomFingerprint())
                .issuedAt(Instant.now())
                .userId(userId)
                .privateKey(keyManager.getPrivateKey())
                .build();
    }
}
