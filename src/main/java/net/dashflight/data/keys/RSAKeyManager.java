package net.dashflight.data.keys;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public interface RSAKeyManager {

    RSAPublicKey getPublicKey();

    RSAPrivateKey getPrivateKey();

}
