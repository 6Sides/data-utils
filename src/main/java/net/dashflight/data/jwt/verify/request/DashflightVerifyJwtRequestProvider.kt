package net.dashflight.data.jwt.verify.request

import com.google.inject.Inject
import net.dashflight.data.config.ConfigValue
import net.dashflight.data.config.Configurable
import net.dashflight.data.jwt.FingerprintService
import net.dashflight.data.keys.RSAKeyPairProvider

class DashflightVerifyJwtRequestProvider @Inject constructor(fingerprintService: FingerprintService, keyPairProvider: RSAKeyPairProvider) : VerifyJwtRequestProvider, Configurable {
    private val fingerprintService: FingerprintService
    private val keyManager: RSAKeyPairProvider

    override fun create(token: String?, fingerprint: String?): JwtVerificationRequirements {
        return JwtVerificationRequirements(ISSUER, token, fingerprint, fingerprintService.hashFingerprint(fingerprint), keyManager.publicKey)
    }

    companion object {
        @ConfigValue("issuer")
        private val ISSUER: String? = null
    }

    init {
        registerWith("jwt-utils")
        this.fingerprintService = fingerprintService
        keyManager = keyPairProvider
    }
}