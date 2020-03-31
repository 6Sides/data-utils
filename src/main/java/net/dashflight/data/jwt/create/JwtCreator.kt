package net.dashflight.data.jwt.create

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.google.inject.Inject
import net.dashflight.data.jwt.FingerprintService
import net.dashflight.data.jwt.SecuredJwt
import net.dashflight.data.jwt.create.request.CreateJwtRequestProvider
import java.time.Instant
import java.util.*

/**
 * Handles creating JWTs
 */
class JwtCreator @Inject constructor(
        private val provider: CreateJwtRequestProvider,
        private val fingerprintService: FingerprintService
) {

    /**
     * Creates a secure JWT with the userId encoded in its payload.
     *
     * How it works:
     * 1. Random fingerprint is generated.
     * 2. JWT is created storing any necessary claims and the hash of the fingerprint.
     * 3. The JWT is ciphered to obfuscate any internal data stored in the payload. (Currently omitted)
     */
    @Throws(JWTCreationException::class)
    fun generateFor(userId: String): SecuredJwt {

        val request = provider.create(userId)
        val fingerprint = fingerprintService.generateRandomFingerprint()
        val tokenBuilder = JWT.create()

        request.issuer?.let { issuer ->
            tokenBuilder.withIssuer(issuer)
        }
        request.issuedAt?.let { issuedAt ->
            tokenBuilder.withIssuedAt(Date.from(issuedAt))
        }
        request.ttl?.let { ttl ->
            val start = request.issuedAt ?: Instant.now()
            val expiryDate = Date.from(start.plusSeconds(ttl.toLong()))
            tokenBuilder.withExpiresAt(expiryDate)
        }

        request.claims?.let {
            for ((key, value) in it) {
                tokenBuilder.withClaim(key, value)
            }
        }

        tokenBuilder.withClaim("fgp", fingerprintService.hashFingerprint(fingerprint))

        val token = tokenBuilder.sign(Algorithm.RSA512(null, request.privateKey))

        return SecuredJwt(token, fingerprint)
    }
}