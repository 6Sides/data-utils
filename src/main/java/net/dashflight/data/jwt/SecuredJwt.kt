package net.dashflight.data.jwt

/**
 * Stores a JWT token and the fingerprint associated with it.
 * The fingerprint should be set as a cookie when returned to the client.
 */
class SecuredJwt(val token: String, @Transient val fingerprint: String?)