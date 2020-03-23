package net.dashflight.data.jwt.verify;

import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;
import net.dashflight.data.jwt.FingerprintService;
import net.dashflight.data.keys.RSAKeyManager;
import net.dashflight.data.keys.RSAKeyManagerFactory;

public class BasicVerifyJwtRequestProvider implements VerifyJwtRequestProvider, Configurable {

    @ConfigValue("issuer")
    private static String ISSUER;

    private final RSAKeyManager keyManager = RSAKeyManagerFactory.withDefaults();

    private final FingerprintService fingerprintService = new FingerprintService();

    public BasicVerifyJwtRequestProvider() {
        registerWith("jwt-utils");
    }


    @Override
    public VerifyJwtRequest create(String token, String fingerprint) {
        return VerifyJwtRequest.builder()
                .issuer(ISSUER)
                .token(token)
                .fingerprint(fingerprint)
                .fingerprintHash(fingerprintService.hashFingerprint(fingerprint))
                .publicKey(keyManager.getPublicKey())
                .build();
    }
}
