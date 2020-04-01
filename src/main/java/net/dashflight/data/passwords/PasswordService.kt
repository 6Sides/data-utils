package net.dashflight.data.passwords

import at.favre.lib.crypto.bcrypt.BCrypt
import java.nio.ByteBuffer

/**
 * Utility for hashing & verifying user passwords
 */
class PasswordService {
    private val hasher = BCrypt.withDefaults()
    private val verifier = BCrypt.verifyer()

    /**
     * Overwrites the byte array to remove its contents from memory.
     */
    private fun overwriteBytes(arr: ByteArray) {
        ByteBuffer.wrap(arr).put(ByteArray(arr.size))
    }

    /**
     * Hashes the specified byte array and overwrites it.
     * @return The hashed password
     */
    fun hashPassword(password: ByteArray): ByteArray {
        val result = hasher.hash(12, password)
        overwriteBytes(password)
        return result
    }

    /**
     * Checks if the password matches the password hash. Then overwrites password data.
     * @return true if the passwords match, false otherwise.
     */
    fun verifyPassword(password: ByteArray, passwordHash: ByteArray): Boolean {
        val result = verifier.verify(password, passwordHash).verified
        overwriteBytes(password)
        return result
    }
}