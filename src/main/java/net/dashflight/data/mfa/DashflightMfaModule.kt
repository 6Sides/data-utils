package net.dashflight.data.mfa

import com.google.inject.AbstractModule

class DashflightMfaModule : AbstractModule() {
    override fun configure() {
        bind(MfaDataProvider::class.java).to(DashflightMfaDataProvider::class.java)
        bind(MfaUriDataProvider::class.java).to(DashflightUriDataProvider::class.java)
        bind(MfaService::class.java).to(BasicMfaService::class.java)
    }
}