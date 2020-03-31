package net.dashflight.data.keys

import com.google.inject.Inject
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * Creates an RSA key pair based on the provided data and transformer.
 */
class DynamicRSAKeyPairProvider @Inject constructor(
        dataProvider: RSAKeyPairDataProvider,
        dataTransformer: RSAKeyPairTransformer
) : RSAKeyPairProvider {

    override val publicKey: RSAPublicKey = dataTransformer.transformPublicKey(dataProvider.publicKeyData)
    override val privateKey: RSAPrivateKey = dataTransformer.transformPrivateKey(dataProvider.privateKeyData)

}