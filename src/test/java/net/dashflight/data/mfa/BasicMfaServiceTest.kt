package net.dashflight.data.mfa

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class BasicMfaServiceTest {

    val USER_ID = UUID.fromString("b107f4cc-6df9-11ea-bc55-0242ac130003")
    lateinit var service: BasicMfaService

    @Before
    fun setup() {
        service = BasicMfaService(
                object : MfaDataProvider {
                    override fun getTOTPCode(userId: UUID): String? {
                        return "12345"
                    }

                    override fun getUserSecret(userId: UUID): String? {
                        return "54321"
                    }

                    override fun getAuthenticatorURI(data: BasicUserData?): String {
                        return String.format("test://%s:%s:%s", data!!.userId, data.userSecret, data.email)
                    }

                    override fun generateOneTimePassword(): String? {
                        return "1"
                    }
                },
                object : MfaUriDataProvider {
                    override fun getData(userId: UUID): BasicUserData {
                        val data = BasicUserData()
                        data.email = "test@email.com"
                        return data
                    }
                }
        )
    }

    @Test
    fun testTOTPCode() = Assert.assertEquals("12345", service.getTOTPCode(USER_ID))


    @Test
    fun testUserSecret() = Assert.assertEquals("54321", service.getUserSecret(USER_ID))


    @Test
    fun testAuthenticatorURI() = Assert.assertEquals("test://b107f4cc-6df9-11ea-bc55-0242ac130003:54321:test@email.com", service.getAuthenticatorURI(USER_ID))

}