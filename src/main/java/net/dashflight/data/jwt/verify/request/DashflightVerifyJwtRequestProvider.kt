package net.dashflight.data.jwt.verify.request

import com.google.inject.Inject
import hydro.engine.Hydro.hydrate
import net.dashflight.data.config.ConfigValue
import net.dashflight.data.config.Configurable
import net.dashflight.data.jwt.FingerprintService
import net.dashflight.data.keys.RSAKeyPairProvider

class DashflightVerifyJwtRequestProvider @Inject constructor(
        private val fingerprintService: FingerprintService,
        private val keyManager: RSAKeyPairProvider
) : VerifyJwtRequestProvider {

    private val ISSUER: String by hydrate("issuer")

    override fun create(token: String, fingerprint: String): JwtVerificationRequirements {
        return JwtVerificationRequirements(
                issuer = ISSUER,
                token = token,
                fingerprint = fingerprint,
                fingerprintHash = fingerprintService.hashFingerprint(fingerprint),
                publicKey = keyManager.publicKey
        )
    }
}