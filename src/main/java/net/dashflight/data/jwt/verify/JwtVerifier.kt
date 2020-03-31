package net.dashflight.data.jwt.verify

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import com.google.inject.Inject
import net.dashflight.data.jwt.verify.request.VerifyJwtRequestProvider

/**
 * Handles verifying and decoding JWTs
 */
class JwtVerifier @Inject constructor(private val provider: VerifyJwtRequestProvider) {

    /**
     * Decodes a JWT and returns it if it is valid
     *
     * How it works:
     * 1. Token is deciphered. (Currently omitted as tokens are not ciphered)
     * 2. JWT is verified using the signing algorithm. Any time expiration checks are performed here.
     * 3. The hash of the provided fingerprint is matched against the user_fingerprint claim in the payload.
     * If they match, the token is valid. Otherwise the JWT is rejected.
     *
     * @throws JWTVerificationException if token is unable to be decoded or verified
     */
    @Throws(JWTVerificationException::class)
    fun verifyToken(token: String?, fingerprint: String?): DecodedJWT {
        val request = provider.create(token, fingerprint)
        if (request.token == null || request.fingerprint == null) {
            throw JWTVerificationException("The token and fingerprint must both be non-null.")
        }
        val jwtVerifier: JWTVerifier = JWT.require(Algorithm.RSA512(request.publicKey, null))
                .withIssuer(request.issuer)
                .withClaim("fgp", request.fingerprintHash)
                .build()
        return jwtVerifier.verify(request.token)
    }

}