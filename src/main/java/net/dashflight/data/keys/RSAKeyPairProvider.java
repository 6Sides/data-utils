package net.dashflight.data.keys;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Responsible for returning a valid RSA key pair.
 */
public interface RSAKeyPairProvider {

    RSAPublicKey getPublicKey();

    RSAPrivateKey getPrivateKey();

}
