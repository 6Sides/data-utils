package net.dashflight.data.postgres

import net.dashflight.data.config.Configurable
import net.dashflight.data.config.S3ConfigurationDelegate

/**
 * Pulls the required postgres connection options from s3
 */
class DashflightPostgresConnectionOptionProvider @JvmOverloads constructor(applicationName: String? = null) : PostgresConnectionOptionProvider, Configurable {

    init {
        registerWith(applicationName ?: DEFAULT_APP_NAME)
    }

    private val host: String? by S3ConfigurationDelegate(DEFAULT_APP_NAME, "pg_host")

    private val port: Int by S3ConfigurationDelegate(DEFAULT_APP_NAME, "pg_port")

    private val dbname: String? by S3ConfigurationDelegate(DEFAULT_APP_NAME, "pg_dbname")

    private val username: String? by S3ConfigurationDelegate(DEFAULT_APP_NAME, "pg_username")

    private val password: String? by S3ConfigurationDelegate(DEFAULT_APP_NAME, "pg_password")

    private val applicationName: String? by S3ConfigurationDelegate(DEFAULT_APP_NAME, "application_name")

    private val maxPoolSize: Int by S3ConfigurationDelegate(DEFAULT_APP_NAME, "max_pool_size")

    override fun get(): PostgresConnectionOptions {
        return PostgresConnectionOptions(host!!, port, dbname, username, password, applicationName ?: "Dashflight Java App", maxPoolSize)
    }

    companion object {
        private const val DEFAULT_APP_NAME = "java-postgres"
    }
}