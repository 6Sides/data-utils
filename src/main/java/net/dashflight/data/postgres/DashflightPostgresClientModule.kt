package net.dashflight.data.postgres

import com.google.inject.AbstractModule

class DashflightPostgresClientModule constructor(private val applicationName: String? = null): AbstractModule() {

    override fun configure() {
        bind(PostgresConnectionOptionProvider::class.java).toInstance(DashflightPostgresConnectionOptionProvider(applicationName))
    }
}