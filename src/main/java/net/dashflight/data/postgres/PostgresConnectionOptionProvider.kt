package net.dashflight.data.postgres

/**
 * Creates the connection options used by a Postgres client
 */
interface PostgresConnectionOptionProvider {
    fun get(): PostgresConnectionOptions
}