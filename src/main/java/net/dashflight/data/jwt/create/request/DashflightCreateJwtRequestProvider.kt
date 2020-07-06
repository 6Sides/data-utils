package net.dashflight.data.jwt.create.request

import com.google.inject.Inject
import hydro.engine.Hydro.hydrate
import net.dashflight.data.config.ConfigValue
import net.dashflight.data.config.Configurable
import net.dashflight.data.keys.RSAKeyPairProvider
import java.time.Instant
import java.util.*

/**
 * Used to generate jwts for authentication with dashflight
 */
internal class DashflightCreateJwtRequestProvider @Inject constructor(
    private val keyManager: RSAKeyPairProvider
) : CreateJwtRequestProvider {

    private val ISSUER: String by hydrate("issuer")

    private val TOKEN_TTL: Int by hydrate("access_token_ttl")

    override fun create(userId: String): CreateJwtRequest {
        val claims: MutableMap<String, String> = HashMap()
        claims["user_id"] = userId

        return CreateJwtRequest(ISSUER, Instant.now(), TOKEN_TTL, claims, keyManager.privateKey)
    }
}