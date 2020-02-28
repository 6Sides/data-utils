package net.dashflight.data.random;

public interface RandomGenerator {

    /**
     * Populates the specified array with random bytes
     */
    void nextBytes(byte[] bytes);

}
