package net.dashflight.data.jwt;

import static org.junit.Assert.*;

import org.junit.Test;

public class FingerprintServiceTest {

    private FingerprintService fingerprintService = new FingerprintService();

    @Test
    public void testHashFingerprint() {
        String input = "FA89CB6C2A1B1E6DC08742FAE1FF595BF50F01F6F07B560DF0340B8FB71C59FFCC8C6D5C71411C9363316226017BC97F596F6CDB60A07DB12CED8B8D9F833B9E";
        String expected = "32907BC5BAA7D694FCF5A358A98EED5D8C5F2F287B47D7DD580AC488ABD2E50D";

        String result = fingerprintService.hashFingerprint(input);

        assertEquals(result, expected);
    }
}