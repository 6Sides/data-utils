package net.dashflight.data.jwt.create.request

import com.google.inject.AbstractModule

class DashflightCreateJwtRequestProviderModule : AbstractModule() {

    override fun configure() {
        bind(CreateJwtRequestProvider::class.java).to(DashflightCreateJwtRequestProvider::class.java)
    }
}