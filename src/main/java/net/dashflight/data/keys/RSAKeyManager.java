package net.dashflight.data.keys;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public abstract class RSAKeyManager {

    protected static KeyFactory keyFactory;

    static {
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public abstract RSAPublicKey getPublicKey();

    public abstract RSAPrivateKey getPrivateKey();

}
