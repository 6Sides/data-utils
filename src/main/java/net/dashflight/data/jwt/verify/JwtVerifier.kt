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
    fun verifyToken(token: String, fingerprint: String): JwtVerificationResponse {
        val request = provider.create(token, fingerprint)

        val jwtVerifier: JWTVerifier = JWT.require(Algorithm.RSA512(request.publicKey, null))
                .withIssuer(request.issuer)
                .withClaim("fgp", request.fingerprintHash)
                .build()

        return try {
            JwtVerificationResponse.Success(jwtVerifier.verify(request.token))
        } catch (e: JWTVerificationException) {
            JwtVerificationResponse.Error(e.message ?: "Jwt Verification Error", e)
        }
    }
}

sealed class JwtVerificationResponse {
    data class Success(val result: DecodedJWT) : JwtVerificationResponse()
    data class Error(val message: String, val cause: JWTVerificationException? = null) : JwtVerificationResponse()
}