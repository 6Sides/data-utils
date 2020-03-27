package net.dashflight.data.keys;

import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Transforms the raw key data into public and private RSA keys.
 */
public interface RSAKeyPairTransformer {

    /**
     * Transforms raw data into a public key
     *
     * @param rawData The raw data to process
     * @return
     * @throws InvalidKeyException If the data is unable to be transformed into a key
     */
    RSAPublicKey transformPublicKey(String rawData) throws InvalidKeyException;

    /**
     * Transforms raw data into a private key
     *
     * @param rawData The raw data to process
     * @return
     * @throws InvalidKeyException If the data is unable to be transformed into a key
     */
    RSAPrivateKey transformPrivateKey(String rawData) throws InvalidKeyException;

}
