package net.dashflight.data.jwt.create.request

import java.security.interfaces.RSAPrivateKey
import java.time.Instant

/**
 * A data object containing the necessary information to create a jwt
 */
data class CreateJwtRequest(val issuer: String?, val issuedAt: Instant?, val ttl: Int = 0, val claims: Map<String, String> = emptyMap(), val privateKey: RSAPrivateKey?) {
    /**
     * The name of the service that issued the token
     */

    /**
     * The time the token was issued
     */

    /**
     * How long the token should live for (in seconds)
     */

    /**
     * Claims to be added to the jwt payload. Defaults to empty Map.
     */

    /**
     * The private key used to sign the token
     */
}