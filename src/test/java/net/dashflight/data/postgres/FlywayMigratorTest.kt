package net.dashflight.data.postgres

import net.dashflight.data.postgres.FlywayMigrator.migrateAndExecute
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlywayMigratorTest {

    @get:Rule
    var postgresContainer = PostgresContainer()

    @Before
    @Throws(Exception::class)
    fun setup() {
        migrateAndExecute(postgresContainer) { client ->
            client.connection.use { conn ->
                val stmt = conn.prepareStatement("insert into accounts.organizations (id, name) VALUES (?, ?), (?,?)")
                stmt.setInt(1, 1)
                stmt.setString(2, "Organization!")
                stmt.setInt(3, 2)
                stmt.setString(4, "Organization 2!")
                stmt.executeUpdate()
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun test() {
        migrateAndExecute(postgresContainer) { client ->
            client.connection.use { conn ->
                val stmt = conn.prepareStatement("select * from accounts.organizations order by id asc")
                val res = stmt.executeQuery()
                if (res.next()) {
                    Assert.assertEquals(1, res.getInt("id").toLong())
                }
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun test1() {
        migrateAndExecute(postgresContainer) { client ->
            client.connection.use { conn ->
                val stmt = conn.prepareStatement("select * from accounts.organizations order by id asc")
                val res = stmt.executeQuery()
                if (res.next()) {
                    Assert.assertEquals(1, res.getInt("id").toLong())
                }
            }
        }
    }
}