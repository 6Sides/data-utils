package net.dashflight.data.jwt

import com.google.inject.AbstractModule
import net.dashflight.data.jwt.create.DashflightJwtCreatorModule
import net.dashflight.data.jwt.verify.DashflightJwtVerifierModule

class DashflightJwtUtilModule : AbstractModule() {
    override fun configure() {
        install(DashflightJwtCreatorModule())
        install(DashflightJwtVerifierModule())

        bind(JwtUtil::class.java)
    }
}