package net.dashflight.data.passwords

import org.junit.Assert
import org.junit.Test
import java.nio.charset.StandardCharsets

class PasswordServiceTest {
    @Test
    fun testPasswordHashAndVerify() {
        val pwd = PasswordService()
        val password = "testPassword".toByteArray(StandardCharsets.UTF_8)
        val passwordCopy = ByteArray(password.size)
        for (i in password.indices) {
            passwordCopy[i] = password[i]
        }
        val passwordHash = pwd.hashPassword(password)
        Assert.assertTrue(pwd.verifyPassword(passwordCopy, passwordHash))
    }
}