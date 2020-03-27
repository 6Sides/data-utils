package net.dashflight.data.mfa;

import com.google.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import net.dashflight.data.postgres.PostgresClient;
import org.postgresql.util.PGobject;

/**
 * Retrieves the necessary data associated with a user to construct an MFA URI
 */
class DashflightUriDataProvider implements MfaUriDataProvider {

    private final PostgresClient postgresClient;

    @Inject
    public DashflightUriDataProvider(PostgresClient client) {
        this.postgresClient = client;
    }


    @Override
    public BasicUserData getData(UUID userId) {
        String email = getUserEmail(userId);
        email = email != null ? email : "";

        BasicUserData result = new BasicUserData();
        result.setEmail(email);

        return result;
    }

    private String getUserEmail(UUID userId) {
        try (Connection conn = postgresClient.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("select email from accounts.users where id = ?");

            PGobject uid = new PGobject();
            uid.setType("uuid");
            uid.setValue(userId.toString());

            stmt.setObject(1, uid);

            ResultSet res = stmt.executeQuery();

            if (res.next()) {
                return res.getString("email");
            }
        } catch (SQLException ignored) {}

        return null;
    }
}
