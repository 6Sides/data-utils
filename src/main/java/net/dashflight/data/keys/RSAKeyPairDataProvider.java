package net.dashflight.data.keys;

/**
 *  Returns the raw data of a key pair (E.g. base64 encoded, etc.)
 */
public interface RSAKeyPairDataProvider {

    String getPublicKeyData();

    String getPrivateKeyData();

}
