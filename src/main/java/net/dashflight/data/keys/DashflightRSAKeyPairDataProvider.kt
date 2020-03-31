package net.dashflight.data.keys

import com.fasterxml.jackson.databind.ObjectMapper
import net.dashflight.data.config.ConfigValue
import net.dashflight.data.config.Configurable
import java.io.IOException
import java.security.interfaces.RSAKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

class DashflightRSAKeyPairDataProvider internal constructor() : RSAKeyPairDataProvider, Configurable {

    companion object {
        private const val APP_NAME = "rsa-keypair"

        fun keyToJson(key: RSAKey, kid: String): String? {
            val data: MutableMap<String, String> = HashMap()
            data["kty"] = "RSA"
            data["alg"] = "RS512"
            data["use"] = "sig"
            data["kid"] = kid
            data["n"] = String(Base64.getEncoder().encode(key.modulus.toByteArray()))
            if (key is RSAPublicKey) {
                data["e"] = String(Base64.getEncoder().encode(key.publicExponent.toByteArray()))
            } else if (key is RSAPrivateKey) {
                data["e"] = String(Base64.getEncoder().encode(key.privateExponent.toByteArray()))
            }
            try {
                return ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(data)
            } catch (e: IOException) {
                // This should never happen
                e.printStackTrace()
            }
            return null
        }
    }

    init {
        registerWith(APP_NAME)
    }

    @ConfigValue("public_key")
    override lateinit var publicKeyData: String; private set

    @ConfigValue("private_key")
    override lateinit var privateKeyData: String; private set

}