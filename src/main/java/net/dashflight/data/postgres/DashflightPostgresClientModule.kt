package net.dashflight.data.postgres

import com.google.inject.AbstractModule

class DashflightPostgresClientModule : AbstractModule() {
    override fun configure() {
        bind(PostgresConnectionOptionProvider::class.java).to(DashflightPostgresConnectionOptionProvider::class.java)
    }
}