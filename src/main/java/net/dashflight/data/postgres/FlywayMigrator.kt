package net.dashflight.data.postgres

import java.io.IOException
import java.security.NoSuchAlgorithmException

/**
 * Migrates the test container to the correct version and returns a client connected to it.
 */
object FlywayMigrator {

    @Throws(Exception::class)
    fun migrateAndExecute(container: PostgresContainer, testCase: (client: PostgresClient) -> Unit) {
        val address = container.containerIpAddress
        val port = container.firstMappedPort
        val env = container.envMap

        val username = env["POSTGRES_USER"]
        val password = env["POSTGRES_PASSWORD"]
        val database = env["POSTGRES_DB"]

        val options = PostgresConnectionOptions(address, port, database, username, password)

        PostgresClient(object : PostgresConnectionOptionProvider {
            override fun get(): PostgresConnectionOptions {
                return options
            }
        }).use { pgClient ->
            try {
                FlywayManager(pgClient).flyway.migrate()
            } catch (e: IOException) {
                e.printStackTrace()
                throw RuntimeException("Flyway could not migrate database")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                throw RuntimeException("Flyway could not migrate database")
            }
            testCase(pgClient)
        }
    }
}