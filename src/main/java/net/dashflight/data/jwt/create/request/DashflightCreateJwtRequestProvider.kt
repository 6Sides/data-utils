package net.dashflight.data.jwt.create.request

import com.google.inject.Inject
import net.dashflight.data.config.ConfigValue
import net.dashflight.data.config.Configurable
import net.dashflight.data.jwt.FingerprintService
import net.dashflight.data.keys.RSAKeyPairProvider
import java.time.Instant
import java.util.*

/**
 * Used to generate jwts for authentication with dashflight
 */
internal class DashflightCreateJwtRequestProvider @Inject constructor(fingerprintService: FingerprintService, keyPairProvider: RSAKeyPairProvider) : CreateJwtRequestProvider, Configurable {
    private val fingerprintService: FingerprintService
    private val keyManager: RSAKeyPairProvider
    override fun create(userId: String): CreateJwtRequest {
        val claims: MutableMap<String, String> = HashMap()
        claims["user_id"] = userId

        return CreateJwtRequest(ISSUER, Instant.now(), TOKEN_TTL, claims, keyManager.privateKey)
    }

    companion object {
        @ConfigValue("issuer")
        private val ISSUER: String? = null

        @ConfigValue("access_token_ttl")
        private val TOKEN_TTL = 0
    }

    init {
        registerWith("jwt-utils")
        this.fingerprintService = fingerprintService
        keyManager = keyPairProvider
    }
}