package net.dashflight.data.jwt.verify.request

import com.google.inject.Inject
import net.dashflight.data.config.ConfigValue
import net.dashflight.data.config.Configurable
import net.dashflight.data.jwt.FingerprintService
import net.dashflight.data.keys.RSAKeyPairProvider

class DashflightVerifyJwtRequestProvider @Inject constructor(
        private val fingerprintService: FingerprintService,
        private val keyManager: RSAKeyPairProvider
) : VerifyJwtRequestProvider, Configurable {

    init {
        registerWith("jwt-utils")
    }

    override fun create(token: String, fingerprint: String): JwtVerificationRequirements {
        return JwtVerificationRequirements(ISSUER, token, fingerprint, fingerprintService.hashFingerprint(fingerprint), keyManager.publicKey)
    }

    companion object {
        @ConfigValue("issuer")
        private lateinit var ISSUER: String
    }
}