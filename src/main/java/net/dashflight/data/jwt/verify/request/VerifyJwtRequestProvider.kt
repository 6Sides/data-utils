package net.dashflight.data.jwt.verify.request

interface VerifyJwtRequestProvider {
    fun create(token: String, fingerprint: String): JwtVerificationRequirements
}