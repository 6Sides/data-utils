package net.dashflight.data.random;

import java.security.SecureRandom;

public class DefaultRandom implements RandomGenerator {

    private final SecureRandom random = new SecureRandom();;

    @Override
    public void nextBytes(byte[] bytes) {
        random.nextBytes(bytes);
    }
}
