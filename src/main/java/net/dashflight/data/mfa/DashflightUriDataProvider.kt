package net.dashflight.data.mfa

import com.google.inject.Inject
import net.dashflight.data.postgres.PostgresClient
import org.postgresql.util.PGobject
import java.sql.SQLException
import java.util.*

/**
 * Retrieves the necessary data associated with a user to construct an MFA URI
 */
internal class DashflightUriDataProvider @Inject constructor(private val postgresClient: PostgresClient) : MfaUriDataProvider {

    override fun getData(userId: UUID): BasicUserData {
        var email = getUserEmail(userId)
        email = email ?: ""
        val result = BasicUserData()
        result.email = email
        return result
    }

    private fun getUserEmail(userId: UUID): String? {
        try {
            postgresClient.connection.use { conn ->
                val stmt = conn.prepareStatement("select email from accounts.users where id = ?")
                val uid = PGobject()
                uid.type = "uuid"
                uid.value = userId.toString()
                stmt.setObject(1, uid)
                val res = stmt.executeQuery()
                if (res.next()) {
                    return res.getString("email")
                }
            }
        } catch (ignored: SQLException) {
        }
        return null
    }

}