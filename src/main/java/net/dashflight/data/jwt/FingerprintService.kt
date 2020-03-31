package net.dashflight.data.jwt;

import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;
import net.dashflight.data.random.LavaRandom;

/**
 * Utility for generating token fingerprints to strengthen security of JWTs
 */
public class FingerprintService {

    private static final int FINGERPRINT_LENGTH = 64;

    private final Random random;

    @Inject
    public FingerprintService(Random random) {
        this.random = random;
    }

    /**
     * Generates a random fingerprint of length FINGERPRINT_LENGTH
     */
    public String generateRandomFingerprint() {
        byte[] randomBytes = new byte[FINGERPRINT_LENGTH];
        random.nextBytes(randomBytes);

        return DatatypeConverter.printHexBinary(randomBytes);
    }

    /**
     * Hashes a fingerprint with SHA-256
     */
    public String hashFingerprint(String fgp) {
        if (fgp == null) {
            return null;
        }

        byte[] fingerprintDigest = Hashing.sha256().hashBytes(fgp.getBytes(StandardCharsets.UTF_8)).asBytes();

        return DatatypeConverter.printHexBinary(fingerprintDigest);
    }

}
