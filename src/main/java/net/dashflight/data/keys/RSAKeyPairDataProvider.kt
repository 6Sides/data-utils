package net.dashflight.data.keys

/**
 * Returns the raw data of a key pair (E.g. base64 encoded, etc.)
 */
interface RSAKeyPairDataProvider {
    val publicKeyData: String
    val privateKeyData: String
}