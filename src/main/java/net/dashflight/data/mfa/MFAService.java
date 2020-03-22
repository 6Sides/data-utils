package net.dashflight.data.mfa;

import de.taimos.totp.TOTP;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;
import net.dashflight.data.postgres.PostgresFactory;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.postgresql.util.PGobject;


/**
 * Service for handling MFA related tasks
 */
public class MFAService implements Configurable {

    @ConfigValue("issuer")
    private static String ISSUER;

    private final Base32 base32 = new Base32();

    public MFAService() {
        registerWith("multi-factor");
    }

    /**
     * Gets the current TOTP code associated with the specified user
     */
    public String getTOTPCode(UUID userId) {
        byte[] bytes = base32.decode(getUserSecret(userId));
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    /**
     * Converts UUID (only 16 bytes) to 20 byte String for use with TOTP mfa
     */
    public String getUserSecret(UUID userId) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[20]);

        // Some static bytes to pad the uuid
        // Spells out `DASH` because why not
        bb.put(new byte[] {24,36,112,94});
        bb.putLong(userId.getMostSignificantBits());
        bb.putLong(userId.getLeastSignificantBits());

        return base32.encodeToString(bb.array());
    }

    /**
     * Returns a uri for the user to scan with the Authenticator app.
     */
    public String getGoogleAuthenticatorBarCode(UUID userId) {
        String email = getUserEmail(userId);
        email = email != null ? email : "";

        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(ISSUER + ":" + email, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(getUserSecret(userId), "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(ISSUER, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }


    /**
     * Retrieves the email address associated with the user
     */
    private String getUserEmail(UUID userId) {
        try (Connection conn = PostgresFactory.withDefaults().getConnection()) {
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
