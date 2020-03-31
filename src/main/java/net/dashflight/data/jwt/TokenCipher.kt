package net.dashflight.data.jwt;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.config.TinkConfig;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.xml.bind.DatatypeConverter;

/**
 * CURRENTLY NOT USING AS IT DOUBLES TOKEN SIZE (TO ~1KB)
 *
 * Ciphers JWTs before sending them back to the client.
 * Prevents leaking any internal information stored in the JWT payload.
 */
class TokenCipher {

    // TODO: Point to s3 bucket when deployed to production
    private static String KEYSET_HANDLE_FILE = "key_cipher.json";

    private transient Aead aead;
    private transient KeysetHandle keysetHandle;

    static {
        try {
            TinkConfig.register();
            AeadConfig.register();
        } catch (GeneralSecurityException e) {
            // This should never happen
            e.printStackTrace();
        }
    }

    TokenCipher() {
        try {
            keysetHandle = this.loadKeysetIfPresent();

            if (keysetHandle == null) {
                keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);
                this.saveKeyset();
            }

            this.aead = keysetHandle.getPrimitive(Aead.class);
        } catch(GeneralSecurityException e) {
            // This should never happen
            e.printStackTrace();
        }
    }

    /**
     * Loads the keyset from KEYSET_HANDLE_FILE path
     */
    private KeysetHandle loadKeysetIfPresent() {
        KeysetHandle result = null;

        try {
            result = CleartextKeysetHandle.read(JsonKeysetReader.withFile(new File(KEYSET_HANDLE_FILE)));
        } catch(IOException | GeneralSecurityException ex) {
            // This should never happen
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * Saves the current keyset to a file
     */
    private void saveKeyset() {
        try {
            CleartextKeysetHandle
                    .write(keysetHandle, JsonKeysetWriter.withFile(new File(KEYSET_HANDLE_FILE)));
        } catch(IOException e) {
            // This should never happen
            e.printStackTrace();
        }
    }

    String cipherToken(String jwt) throws GeneralSecurityException {
        // Cipher the token
        byte[] cipheredToken = aead.encrypt(jwt.getBytes(), null);

        // Convert to String
        return DatatypeConverter.printHexBinary(cipheredToken);
    }

    String decipherToken(String jwtInHex) throws GeneralSecurityException {
        // Decode the ciphered token
        byte[] cipheredToken = DatatypeConverter.parseHexBinary(jwtInHex);

        // Decipher the token
        byte[] decipheredToken = aead.decrypt(cipheredToken, null);

        return new String(decipheredToken);
    }
}