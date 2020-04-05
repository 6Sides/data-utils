package net.dashflight.data.postgres

import com.google.inject.Inject
import com.google.inject.Singleton
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jdbi.v3.core.Jdbi
import java.net.URI
import java.net.URISyntaxException
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource
import kotlin.system.exitProcess

/**
 * Creates a pool of Postgres connections
 */
@Singleton
class PostgresClient @Inject internal constructor(optionProvider: PostgresConnectionOptionProvider) : AutoCloseable {
    private val config = HikariConfig()

    private var connectionPool: HikariDataSource? = null

    lateinit var jdbi: Jdbi

    private val options: PostgresConnectionOptions = optionProvider.get()

    private fun init() {
        val dbUrl = String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s",
                options.host,
                options.port,
                options.dbname,
                options.username,
                options.password
        )

        val dbUri: URI = try {
            URI(dbUrl)
        } catch (e: URISyntaxException) {
            // Kill the program
            exitProcess(1)
        }

        if (dbUri.userInfo != null) {
            config.username = dbUri.userInfo.split(":").toTypedArray()[0]
            config.password = dbUri.userInfo.split(":").toTypedArray()[1]
        }

        config.jdbcUrl = dbUrl
        config.poolName = options.applicationName
        config.maximumPoolSize = options.maxPoolSize
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        config.addDataSourceProperty("ApplicationName", options.applicationName)

        connectionPool = HikariDataSource(config)

        // Close the pool on shutdown
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { connectionPool!!.close() }))
        jdbi = Jdbi.create(connectionPool)

        // Installs all jdbi plugins found in classpath
        jdbi.installPlugins()
    }

    @get:Throws(SQLException::class)
    val connection: Connection
        get() = connectionPool!!.connection

    val dataSource: DataSource?
        get() = connectionPool

    override fun close() {
        connectionPool!!.close()
    }

    init {
        init()
    }
}