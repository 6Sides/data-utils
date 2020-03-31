package net.dashflight.data.jwt

import com.google.common.hash.Hashing
import com.google.inject.Inject
import java.nio.charset.StandardCharsets
import java.util.*
import javax.xml.bind.DatatypeConverter

/**
 * Utility for generating token fingerprints to strengthen security of JWTs
 */
class FingerprintService @Inject constructor(private val random: Random) {

    /**
     * Generates a random fingerprint of length FINGERPRINT_LENGTH
     */
    fun generateRandomFingerprint(): String {
        val randomBytes = ByteArray(FINGERPRINT_LENGTH)
        random.nextBytes(randomBytes)
        return DatatypeConverter.printHexBinary(randomBytes)
    }

    /**
     * Hashes a fingerprint with SHA-256
     */
    fun hashFingerprint(fgp: String?): String? {
        if (fgp == null) {
            return null
        }
        val fingerprintDigest = Hashing.sha256().hashBytes(fgp.toByteArray(StandardCharsets.UTF_8)).asBytes()
        return DatatypeConverter.printHexBinary(fingerprintDigest)
    }

    companion object {
        private const val FINGERPRINT_LENGTH = 64
    }

}