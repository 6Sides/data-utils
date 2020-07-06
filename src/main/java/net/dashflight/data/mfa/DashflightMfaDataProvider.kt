package net.dashflight.data.mfa

import com.google.inject.Inject
import de.taimos.totp.TOTP
import hydro.engine.Hydro.hydrate
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Hex
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.util.*
import javax.xml.bind.DatatypeConverter

/**
 * Service for handling google authenticator 2FA for Dashflight
 */
internal class DashflightMfaDataProvider @Inject constructor(
        private val random: Random
) : MfaDataProvider {

    private val ISSUER: String by hydrate("issuer")
    
    private val base32 = Base32()

    /**
     * Gets the current TOTP code associated with the specified user
     */
    override fun getTOTPCode(userId: UUID): String? {
        val bytes = base32.decode(getUserSecret(userId))
        val hexKey = Hex.encodeHexString(bytes)
        return TOTP.getOTP(hexKey)
    }

    /**
     * Converts UUID (only 16 bytes) to 20 byte String for use with TOTP mfa
     */
    override fun getUserSecret(userId: UUID): String? {
        val bb = ByteBuffer.wrap(ByteArray(20))

        // Some static bytes to pad the uuid
        // Spells out `DASH` because why not
        bb.put(byteArrayOf(24, 36, 112, 94))
        bb.putLong(userId.mostSignificantBits)
        bb.putLong(userId.leastSignificantBits)
        return base32.encodeToString(bb.array())
    }

    /**
     * Returns a URI for the user to scan with the Authenticator app.
     */
    override fun getAuthenticatorURI(data: BasicUserData?): String {
        val userId = data?.userId!!
        val email = data?.email
        return try {
            ("otpauth://totp/"
                    + URLEncoder.encode("$ISSUER:$email", "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(getUserSecret(userId), "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(ISSUER, "UTF-8").replace("+", "%20"))
        } catch (e: UnsupportedEncodingException) {
            throw IllegalStateException(e)
        }
    }

    override fun generateOneTimePassword(): String? {
        val randomBytes = ByteArray(64)
        random.nextBytes(randomBytes)
        return DatatypeConverter.printHexBinary(randomBytes)
    }

}