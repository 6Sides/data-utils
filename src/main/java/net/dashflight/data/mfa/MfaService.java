package net.dashflight.data.mfa;

import java.util.UUID;

public interface MfaService {

    /**
     * Returns the user's current TOTP (Temporary One Time Password) needed
     * to authenticate
     */
    String getTOTPCode(UUID userId);

    /**
     * Returns the user's secret value (20 bytes)
     *
     * A user's secret should never change. This method is a pure function.
     */
    String getUserSecret(UUID userId);

    /**
     * Returns a URI that can be embedded in a QR Code. Used to make
     * adding mfa implementations simple for users.
     */
    String getAuthenticatorURI(UUID userId);

    /**
     * Generates a one time password to associate with a user
     */
    String generateOneTimePassword();
}
