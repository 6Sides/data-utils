package net.dashflight.data.random;

import java.security.SecureRandom;

public class SecureLavaRandom extends SecureRandom {

    public SecureLavaRandom() {
        super(new LavaRandomSpi(), new LavaRandomProvider());
    }

}
