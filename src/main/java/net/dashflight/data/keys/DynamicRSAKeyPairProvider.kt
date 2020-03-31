package net.dashflight.data.keys;

import com.google.inject.Inject;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Creates an RSA key pair based on the provided data and transformer.
 */
public class DynamicRSAKeyPairProvider implements RSAKeyPairProvider {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;


    @Inject
    public DynamicRSAKeyPairProvider(RSAKeyPairDataProvider dataProvider, RSAKeyPairTransformer dataTransformer) throws InvalidKeyException {
        publicKey = dataTransformer.transformPublicKey(dataProvider.getPublicKeyData());
        privateKey = dataTransformer.transformPrivateKey(dataProvider.getPrivateKeyData());
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

}
