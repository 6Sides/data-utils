package net.dashflight.data.jwt

import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.inject.Inject
import net.dashflight.data.jwt.create.JwtCreator
import net.dashflight.data.jwt.verify.JwtVerificationResponse
import net.dashflight.data.jwt.verify.JwtVerifier

class JwtUtil @Inject constructor(private val creator: JwtCreator, private val verifier: JwtVerifier) {

    @Throws(JWTCreationException::class)
    fun generateFor(userId: String): SecuredJwt? {
        return creator.generateFor(userId)
    }

    fun verifyToken(token: String, fingerprint: String): JwtVerificationResponse {
        return verifier.verifyToken(token, fingerprint)
    }

}