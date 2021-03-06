package net.dashflight.data.jwt

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class FingerprintServiceTest {
    private var fingerprintService: FingerprintService? = null

    @Before
    fun setup() {
        fingerprintService = FingerprintService(Random(1))
    }

    @Test
    fun testHashFingerprint() {
        val input = "FA89CB6C2A1B1E6DC08742FAE1FF595BF50F01F6F07B560DF0340B8FB71C59FFCC8C6D5C71411C9363316226017BC97F596F6CDB60A07DB12CED8B8D9F833B9E"
        val expected = "32907BC5BAA7D694FCF5A358A98EED5D8C5F2F287B47D7DD580AC488ABD2E50D"
        val result = fingerprintService!!.hashFingerprint(input)
        Assert.assertEquals(result, expected)
    }

    @Test
    fun testGenerateRandomFingerprint() {
        val expected = "73D51ABBD89CB8196F0EFB6892F94D68FCCC2C35F0B84609E5F12C55DD85ABA8D5D9BEF76808F3B572E5900112B81927BA5BB5F67E1BDA28B4049BF0E4AED78D"
        val result = fingerprintService!!.generateRandomFingerprint()
        Assert.assertEquals(result, expected)
    }
}