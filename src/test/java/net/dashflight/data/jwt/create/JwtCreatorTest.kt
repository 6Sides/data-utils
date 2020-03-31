package net.dashflight.data.jwt.create

import net.dashflight.data.jwt.FingerprintService
import net.dashflight.data.jwt.create.request.CreateJwtRequest
import net.dashflight.data.jwt.create.request.CreateJwtRequestProvider
import net.dashflight.data.keys.RSAKeyPairProvider
import net.dashflight.data.keys.StaticRSAKeyPairProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.time.Instant
import java.util.*

class JwtCreatorTest {

    private lateinit var provider: CreateJwtRequestProvider

    @Before
    fun setup() {
        val keyManager: RSAKeyPairProvider = StaticRSAKeyPairProvider()
        provider = object : CreateJwtRequestProvider {
            override fun create(userId: String): CreateJwtRequest {
                val claims: MutableMap<String, String> = HashMap()
                claims["user_id"] = userId

                return CreateJwtRequest("test", Instant.ofEpochSecond(1584996995), 15, claims, keyManager.privateKey)
            }
        }
    }

    @Test
    fun testGenerateJwt() {
        val creator = JwtCreator(provider, FingerprintService(Random(0)))
        val expectedToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoxNTg0OTk3MDEwLCJpYXQiOjE1ODQ5OTY5OTUsImZncCI6IkM0RjA2OTNDMTUwRDI0NUFDQ0Y1NTg0MDJFNEJBQjBCNjdCNjExRjBBQ0Y3MTA5OTEyRThBNzRBOTMxQzcxQUIifQ.B_QRI3uk0qo_or5owq5D7c5yDNI3f2Nz5mwj2IBqKTVpwI2NFZT3T1--N2VH0xMm0diBChmOYVV-wC4UsGOW9OW6evUzDHQFCTT4Jtrpc-uMsyx-gMU5NjrJRnhobdhFgXSr5DDhYHaMlDYsQPgFDiD9idx2gqptx3cOszAqa5Q"
        val expectedFingerprint = "60B420BB3851D9D47ACB933DBE70399BF6C92DA33AF01D4FB770E98C0325F41D3EBAF8986DA712C82BCD4D554BF0B54023C29B624DE9EF9C2F931EFC580F9AFB"
        val result = creator.generateFor("11111")

        Assert.assertEquals(expectedToken, result.token)
        Assert.assertEquals(expectedFingerprint, result.fingerprint)
    }
}