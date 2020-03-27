package net.dashflight.data.passwords;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class PasswordServiceTest {

    @Test
    public void testPasswordHashAndVerify() {
        PasswordService pwd = new PasswordService();

        byte[] password = "testPassword".getBytes(StandardCharsets.UTF_8);

        byte[] passwordCopy = new byte[password.length];

        for (int i = 0; i < password.length; i++) {
            passwordCopy[i] = password[i];
        }

        byte[] passwordHash = pwd.hashPassword(password);

        assertTrue(pwd.verifyPassword(passwordCopy, passwordHash));
    }
}