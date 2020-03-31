package net.dashflight.data.jwt.verify

import com.google.inject.AbstractModule
import net.dashflight.data.jwt.verify.request.DashflightVerifyJwtRequestProviderModule

class DashflightJwtVerifierModule : AbstractModule() {
    override fun configure() {
        install(DashflightVerifyJwtRequestProviderModule())
        bind(JwtVerifier::class.java)
    }
}