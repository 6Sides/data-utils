package net.dashflight.data.mfa

import java.util.*

/**
 * Provides necessary functionality for implementing an MFA authentication flow
 */
interface MfaDataProvider {
    /**
     * Returns the user's current TOTP (Temporary One Time Password) needed
     * to authenticate
     */
    fun getTOTPCode(userId: UUID): String?

    /**
     * Returns the user's secret value (20 bytes)
     *
     * A user's secret should never change. This method is a pure function.
     */
    fun getUserSecret(userId: UUID): String?

    /**
     * Returns a URI that can be embedded in a QR Code. Used to make
     * adding mfa implementations simple for users.
     */
    fun getAuthenticatorURI(data: BasicUserData?): String

    /**
     * Generates a one time password to associate with a user
     */
    fun generateOneTimePassword(): String?
}