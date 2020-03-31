package net.dashflight.data.jwt.verify.request

import com.google.inject.AbstractModule

class DashflightVerifyJwtRequestProviderModule : AbstractModule() {
    override fun configure() {
        bind(VerifyJwtRequestProvider::class.java).to(DashflightVerifyJwtRequestProvider::class.java)
    }
}