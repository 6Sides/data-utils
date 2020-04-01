package net.dashflight.data.keys

import java.security.InvalidKeyException
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * Transforms the raw key data into public and private RSA keys.
 */
interface RSAKeyPairTransformer {
    /**
     * Transforms raw data into a public key
     *
     * @param rawData The raw data to process
     * @return
     * @throws InvalidKeyException If the data is unable to be transformed into a key
     */
    @Throws(InvalidKeyException::class)
    fun transformPublicKey(rawData: String): RSAPublicKey

    /**
     * Transforms raw data into a private key
     *
     * @param rawData The raw data to process
     * @return
     * @throws InvalidKeyException If the data is unable to be transformed into a key
     */
    @Throws(InvalidKeyException::class)
    fun transformPrivateKey(rawData: String): RSAPrivateKey
}