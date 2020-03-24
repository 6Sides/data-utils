package net.dashflight.data.jwt.verify;

import com.google.inject.Inject;
import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;
import net.dashflight.data.jwt.FingerprintService;
import net.dashflight.data.keys.RSAKeyPairProvider;

public class BasicVerifyJwtRequestProvider implements VerifyJwtRequestProvider, Configurable {

    @ConfigValue("issuer")
    private static String ISSUER;

    private final FingerprintService fingerprintService;
    private final RSAKeyPairProvider keyManager;


    @Inject
    public BasicVerifyJwtRequestProvider(FingerprintService fingerprintService, RSAKeyPairProvider keyPairProvider) {
        registerWith("jwt-utils");

        this.fingerprintService = fingerprintService;
        this.keyManager = keyPairProvider;
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
