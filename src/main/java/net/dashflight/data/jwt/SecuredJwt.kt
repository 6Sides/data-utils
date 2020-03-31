package net.dashflight.data.jwt;

/**
 * Stores a JWT token and the fingerprint associated with it.
 * The fingerprint should be set as a cookie when returned to the client.
 */
public class SecuredJwt {
    private final String token;
    private final transient String fingerprint;

    public String getToken() {
        return token;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public SecuredJwt(String token, String fingerprint) {
        this.token = token;
        this.fingerprint = fingerprint;
    }
}