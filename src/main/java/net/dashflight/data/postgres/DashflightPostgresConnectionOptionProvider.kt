package net.dashflight.data.postgres

import net.dashflight.data.config.ConfigValue
import net.dashflight.data.config.Configurable

/**
 * Pulls the required postgres connection options from s3
 */
class DashflightPostgresConnectionOptionProvider @JvmOverloads constructor(applicationName: String? = DEFAULT_APP_NAME) : PostgresConnectionOptionProvider, Configurable {
    @ConfigValue("pg_host")
    private val host: String? = null

    @ConfigValue("pg_port")
    private val port = 0

    @ConfigValue("pg_dbname")
    private val dbname: String? = null

    @ConfigValue("pg_username")
    private val username: String? = null

    @ConfigValue("pg_password")
    private val password: String? = null

    @ConfigValue("application_name")
    private val applicationName = "Dashflight Java App"

    @ConfigValue("max_pool_size")
    private val maxPoolSize = 0

    override fun get(): PostgresConnectionOptions {
        return PostgresConnectionOptions(host!!, port, dbname, username, password, applicationName, maxPoolSize)
    }

    companion object {
        private const val DEFAULT_APP_NAME = "java-postgres"
    }

    init {
        registerWith(applicationName)
    }
}