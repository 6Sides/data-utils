package net.dashflight.data.jwt.create

import com.google.inject.AbstractModule
import net.dashflight.data.jwt.create.request.DashflightCreateJwtRequestProviderModule
import net.dashflight.data.random.LavaRandom
import java.security.SecureRandom
import java.util.*

class DashflightJwtCreatorModule : AbstractModule() {

    override fun configure() {
        install(DashflightCreateJwtRequestProviderModule())

        bind(JwtCreator::class.java)
        bind(Random::class.java).to(SecureRandom::class.java)
    }
}