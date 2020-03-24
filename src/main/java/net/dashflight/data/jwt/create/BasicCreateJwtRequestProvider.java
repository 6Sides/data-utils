package net.dashflight.data.jwt.create;

import java.time.Instant;
import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;
import net.dashflight.data.jwt.FingerprintService;
import net.dashflight.data.keys.RSAKeyPairProvider;

public class BasicCreateJwtRequestProvider implements CreateJwtRequestProvider, Configurable {

    @ConfigValue("issuer")
    private static String ISSUER = "https://api.dashflight.net";

    @ConfigValue("access_token_ttl")
    private static int TOKEN_TTL;

    private final FingerprintService fingerprintService = new FingerprintService();

    private RSAKeyPairProvider keyManager;


    public BasicCreateJwtRequestProvider(RSAKeyPairProvider keyPairProvider) {
        registerWith("jwt-utils");

        this.keyManager = keyPairProvider;
    }


    @Override
    public CreateJwtRequest create(String userId) {
        String fingerprint = fingerprintService.generateRandomFingerprint();

        return CreateJwtRequest.builder()
                .issuer(ISSUER)
                .ttl(TOKEN_TTL)
                .fingerprint(fingerprint)
                .fingerprintHash(fingerprintService.hashFingerprint(fingerprint))
                .issuedAt(Instant.now())
                .userId(userId)
                .privateKey(keyManager.getPrivateKey())
                .build();
    }
}
