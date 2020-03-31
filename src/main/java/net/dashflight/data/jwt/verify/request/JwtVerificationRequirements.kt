package net.dashflight.data.jwt.verify.request

import java.security.interfaces.RSAPublicKey

/**
 * The jwt to verify and decode
 */

/**
 * The fingerprint associated with the token
 */

/**
 * The hash of the fingerprint
 */

/**
 * The issuer (Who created the token)
 */

/**
 * The public key needed to decode the jwt
 */
data class JwtVerificationRequirements(
        val token: String,
        val fingerprint: String,
        val fingerprintHash: String,
        val issuer: String?,
        val publicKey: RSAPublicKey
)