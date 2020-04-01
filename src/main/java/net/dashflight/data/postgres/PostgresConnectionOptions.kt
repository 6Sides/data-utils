package net.dashflight.data.postgres

/**
 * Holds connection data for connecting to Postgres instance
 */
data class PostgresConnectionOptions(val host: String, val port: Int, val dbname: String?, val username: String?, val password: String?, val applicationName: String = "Java Application", val maxPoolSize: Int = 2) {
    /**
     * The host address of the instance
     */

    /**
     * The port of the instance
     */

    /**
     * The name of the database to connect to
     */

    /**
     * The username to authenticate with
     */

    /**
     * The password to authenticate with
     */

    /**
     * The name of the application connecting. This name shows up under `pg_stat_activity` table
     */

    /**
     * Maximum size of the connection pool
     */
}