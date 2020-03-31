package net.dashflight.data.jwt

import com.google.crypto.tink.*
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.config.TinkConfig
import java.io.File
import java.io.IOException
import java.security.GeneralSecurityException
import javax.xml.bind.DatatypeConverter

/**
 * CURRENTLY NOT USING AS IT DOUBLES TOKEN SIZE (TO ~1KB)
 *
 * Ciphers JWTs before sending them back to the client.
 * Prevents leaking any internal information stored in the JWT payload.
 */
internal class TokenCipher {
    @Transient
    private var aead: Aead? = null

    @Transient
    private var keysetHandle: KeysetHandle? = null

    companion object {
        // TODO: Point to s3 bucket when deployed to production
        private const val KEYSET_HANDLE_FILE = "key_cipher.json"

        init {
            try {
                TinkConfig.register()
                AeadConfig.register()
            } catch (e: GeneralSecurityException) {
                // This should never happen
                e.printStackTrace()
            }
        }
    }

    /**
     * Loads the keyset from KEYSET_HANDLE_FILE path
     */
    private fun loadKeysetIfPresent(): KeysetHandle? {
        var result: KeysetHandle? = null
        try {
            result = CleartextKeysetHandle.read(JsonKeysetReader.withFile(File(KEYSET_HANDLE_FILE)))
        } catch (ex: IOException) {
            // This should never happen
            ex.printStackTrace()
        } catch (ex: GeneralSecurityException) {
            ex.printStackTrace()
        }
        return result
    }

    /**
     * Saves the current keyset to a file
     */
    private fun saveKeyset() {
        try {
            CleartextKeysetHandle
                    .write(keysetHandle, JsonKeysetWriter.withFile(File(KEYSET_HANDLE_FILE)))
        } catch (e: IOException) {
            // This should never happen
            e.printStackTrace()
        }
    }

    @Throws(GeneralSecurityException::class)
    fun cipherToken(jwt: String): String {
        // Cipher the token
        val cipheredToken = aead!!.encrypt(jwt.toByteArray(), null)

        // Convert to String
        return DatatypeConverter.printHexBinary(cipheredToken)
    }

    @Throws(GeneralSecurityException::class)
    fun decipherToken(jwtInHex: String?): String {
        // Decode the ciphered token
        val cipheredToken = DatatypeConverter.parseHexBinary(jwtInHex)

        // Decipher the token
        val decipheredToken = aead!!.decrypt(cipheredToken, null)
        return String(decipheredToken)
    }

    init {
        try {
            keysetHandle = loadKeysetIfPresent()
            if (keysetHandle == null) {
                keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM)
                saveKeyset()
            }
            aead = keysetHandle!!.getPrimitive(Aead::class.java)
        } catch (e: GeneralSecurityException) {
            // This should never happen
            e.printStackTrace()
        }
    }
}