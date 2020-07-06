package net.dashflight.data.postgres

import hydro.engine.Hydro.hydrate

/**
 * Pulls the required postgres connection options from s3
 */
class DashflightPostgresConnectionOptionProvider(applicationName: String) : PostgresConnectionOptionProvider {

    private val host: String? by hydrate("pg_host")

    private val port: Int by hydrate("pg_port")

    private val dbname: String? by hydrate("pg_dbname")

    private val username: String? by hydrate("pg_username")

    private val password: String? by hydrate("pg_password")

    private val _applicationName: String? by hydrate("application_name", applicationName)

    private val maxPoolSize: Int by hydrate("max_pool_size")

    override fun get(): PostgresConnectionOptions {
        return PostgresConnectionOptions(host!!, port, dbname, username, password, _applicationName ?: "Dashflight Java App", maxPoolSize)
    }
}