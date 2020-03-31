package net.dashflight.data.keys

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * Responsible for returning a valid RSA key pair.
 */
interface RSAKeyPairProvider {
    val publicKey: RSAPublicKey?
    val privateKey: RSAPrivateKey?
}