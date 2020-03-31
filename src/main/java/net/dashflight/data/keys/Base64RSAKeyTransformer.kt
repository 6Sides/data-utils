package net.dashflight.data.keys

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.ByteArrayInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec
import java.util.*

/**
 * Transforms Base64 json representations of and RSA key pair into java objects
 */
class Base64RSAKeyTransformer : RSAKeyPairTransformer {
    private var keyFactory: KeyFactory? = null
    private val mapper = ObjectMapper()

    @Throws(InvalidKeyException::class)
    override fun transformPublicKey(rawData: String?): RSAPublicKey {
        return try {
            val components = parseData(rawData)
            val spec = RSAPublicKeySpec(components.modulus, components.exponent)
            keyFactory!!.generatePublic(spec) as RSAPublicKey
        } catch (e: IOException) {
            throw InvalidKeyException(e.message)
        } catch (e: InvalidKeySpecException) {
            throw InvalidKeyException(e.message)
        }
    }

    @Throws(InvalidKeyException::class)
    override fun transformPrivateKey(rawData: String?): RSAPrivateKey {
        return try {
            val components = parseData(rawData)
            val spec = RSAPrivateKeySpec(components.modulus, components.exponent)
            keyFactory!!.generatePrivate(spec) as RSAPrivateKey
        } catch (e: IOException) {
            throw InvalidKeyException(e.message)
        } catch (e: InvalidKeySpecException) {
            throw InvalidKeyException(e.message)
        }
    }

    @Throws(IOException::class)
    private fun parseData(rawData: String?): KeyComponents {
        ByteArrayInputStream(Base64.getDecoder().decode(rawData)).use { input ->
            val data: Map<String?, String?>? = mapper.readValue(input, object : TypeReference<HashMap<String?, String?>?>() {})

            val modulus = BigInteger(Base64.getDecoder().decode(data?.get("n")))
            val exponent = BigInteger(Base64.getDecoder().decode(data?.get("e")))
            return KeyComponents(modulus, exponent)
        }
    }

    /**
     * Representation of RSA key components
     */
    private class KeyComponents(val modulus: BigInteger, val exponent: BigInteger)

    init {
        try {
            keyFactory = KeyFactory.getInstance("RSA")
        } catch (ex: NoSuchAlgorithmException) {
            ex.printStackTrace()
        }
    }
}