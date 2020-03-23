package net.dashflight.data.jwt;

import java.time.Instant;
import net.dashflight.data.jwt.create.CreateJwtRequest;
import net.dashflight.data.jwt.create.JwtCreator;
import net.dashflight.data.keys.RSAKeyManager;
import net.dashflight.data.keys.StaticRSAKeyManager;
import org.junit.Assert;
import org.junit.Test;

public class JwtCreatorTest {

    @Test
    public void testGenerateJwt() {
        JwtCreator creator = new JwtCreator();
        RSAKeyManager keyManager = new StaticRSAKeyManager();

        CreateJwtRequest request = CreateJwtRequest.builder()
                .issuer("test")
                .ttl(15)
                .fingerprint("12345")
                .fingerprintHash("22222")
                .issuedAt(Instant.ofEpochSecond(1584996995))
                .userId("11111")
                .privateKey(keyManager.getPrivateKey())
                .build();


        SecuredJwt expected = new SecuredJwt(
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoxNTg0OTk3MDEwLCJpYXQiOjE1ODQ5OTY5OTUsInVzZXJfZmluZ2VycHJpbnQiOiIyMjIyMiJ9.LVXHUdFxGPNNhdiEX3rqOOn_lMYUmcmOzxPbE2MRzcgpWf-4syrTzkPhd9upKbAhCO-MGu-LC8MqmApAyLDjJL5LOVAOObRADfjwI64lU6UZpUjkIfJiAspHuHx9AP2_ej8yl1Pfx9-UujHmO-D2DMjRNEGzHyNtXRctMNPFwnk",
            "12345"
        );

        SecuredJwt result = creator.generateJwt(request);

        System.out.println(result.getToken());
        Assert.assertEquals(result.getToken(), expected.getToken());
        Assert.assertEquals(result.getFingerprint(), expected.getFingerprint());
    }
}