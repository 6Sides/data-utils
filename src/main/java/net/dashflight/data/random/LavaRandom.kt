package net.dashflight.data.random;

import java.security.SecureRandom;

public class LavaRandom extends SecureRandom {

    public LavaRandom() {
        super(new LavaRandomSpi(), new LavaRandomProvider());
    }

}
