package net.dashflight.data.postgres

import net.dashflight.data.config.S3ConfigurationDelegate

/**
 * Pulls the required postgres connection options from s3
 */
class DashflightPostgresConnectionOptionProvider @JvmOverloads constructor(applicationName: String = DEFAULT_APP_NAME) : PostgresConnectionOptionProvider {

    private val host: String? by S3ConfigurationDelegate(applicationName, "pg_host")

    private val port: Int by S3ConfigurationDelegate(applicationName, "pg_port")

    private val dbname: String? by S3ConfigurationDelegate(applicationName, "pg_dbname")

    private val username: String? by S3ConfigurationDelegate(applicationName, "pg_username")

    private val password: String? by S3ConfigurationDelegate(applicationName, "pg_password")

    private val _applicationName: String? by S3ConfigurationDelegate(applicationName, "application_name")

    private val maxPoolSize: Int by S3ConfigurationDelegate(applicationName, "max_pool_size")

    override fun get(): PostgresConnectionOptions {
        return PostgresConnectionOptions(host!!, port, dbname, username, password, _applicationName ?: "Dashflight Java App", maxPoolSize)
    }

    companion object {
        private const val DEFAULT_APP_NAME = "java-postgres"
    }
}