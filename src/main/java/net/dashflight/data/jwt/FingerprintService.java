package net.dashflight.data.jwt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.xml.bind.DatatypeConverter;
import net.dashflight.data.random.LavaRandom;

/**
 * Utility for generating token fingerprints to strengthen security of JWTs
 */
public class FingerprintService {

    private static final int FINGERPRINT_LENGTH = 64;

    private SecureRandom secureRandom;
    private MessageDigest digest;


    public FingerprintService() {
        this.secureRandom = new LavaRandom();

        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a random fingerprint of length FINGERPRINT_LENGTH
     */
    public String generateRandomFingerprint() {
        byte[] randomBytes = new byte[FINGERPRINT_LENGTH];
        secureRandom.nextBytes(randomBytes);

        return DatatypeConverter.printHexBinary(randomBytes);
    }

    /**
     * Hashes a fingerprint with SHA-256
     */
    public synchronized String hashFingerprint(String fgp) {
        if (fgp == null) {
            return null;
        }

        byte[] fingerprintDigest = digest.digest(fgp.getBytes(StandardCharsets.UTF_8));

        return DatatypeConverter.printHexBinary(fingerprintDigest);
    }

}
