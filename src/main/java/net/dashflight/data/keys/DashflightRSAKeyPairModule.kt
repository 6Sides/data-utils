package net.dashflight.data.keys

import com.google.inject.AbstractModule

/**
 * Provides environment specific implementations used for Dashflight project.
 */
class DashflightRSAKeyPairModule : AbstractModule() {
    override fun configure() {
        bind(RSAKeyPairDataProvider::class.java).toInstance(DashflightRSAKeyPairDataProvider())
        bind(RSAKeyPairTransformer::class.java).toInstance(Base64RSAKeyTransformer())
        bind(RSAKeyPairProvider::class.java).to(DynamicRSAKeyPairProvider::class.java)
    }
}