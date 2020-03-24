package net.dashflight.data.passwords;

/**
 * Handles hashing and verifying passwords
 */
public interface PasswordService {

    byte[] hashPassword(byte[] password);

    boolean verifyPassword(byte[] password, byte[] passwordHash);
}
