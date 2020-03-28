package net.dashflight.data.mfa;

import com.google.inject.Inject;
import de.taimos.totp.TOTP;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;
import javax.xml.bind.DatatypeConverter;
import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;


/**
 * Service for handling google authenticator 2FA for Dashflight
 */
class DashflightMfaDataProvider implements Configurable, MfaDataProvider {

    @ConfigValue("issuer")
    private static String ISSUER;

    private final Base32 base32 = new Base32();
    private final Random random;

    @Inject
    public DashflightMfaDataProvider(Random random) {
        registerWith("multi-factor");

        this.random = random;
    }


    /**
     * Gets the current TOTP code associated with the specified user
     */
    @Override
    public String getTOTPCode(UUID userId) {
        byte[] bytes = base32.decode(getUserSecret(userId));
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    /**
     * Converts UUID (only 16 bytes) to 20 byte String for use with TOTP mfa
     */
    @Override
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
     * Returns a URI for the user to scan with the Authenticator app.
     */
    @Override
    public String getAuthenticatorURI(BasicUserData data) {
        UUID userId = data.getUserId();
        String email = data.getEmail();

        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(ISSUER + ":" + email, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(getUserSecret(userId), "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(ISSUER, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String generateOneTimePassword() {
        byte[] randomBytes = new byte[64];
        random.nextBytes(randomBytes);

        return DatatypeConverter.printHexBinary(randomBytes);
    }
}
