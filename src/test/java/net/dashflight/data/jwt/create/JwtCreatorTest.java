package net.dashflight.data.jwt.create;

import java.time.Instant;
import net.dashflight.data.jwt.SecuredJwt;
import net.dashflight.data.keys.RSAKeyPairProvider;
import net.dashflight.data.keys.StaticRSAKeyPairProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JwtCreatorTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    private CreateJwtRequestProvider provider;

    @Before
    public void setup() {
        RSAKeyPairProvider keyManager = new StaticRSAKeyPairProvider();

        provider = userId -> CreateJwtRequest.builder()
                .issuer("test")
                .ttl(15)
                .fingerprint("12345")
                .fingerprintHash("22222")
                .issuedAt(Instant.ofEpochSecond(1584996995))
                .userId(userId)
                .privateKey(keyManager.getPrivateKey())
                .build();
    }

    @Test
    public void testGenerateJwt() {
        JwtCreator creator = new JwtCreator(provider);

        SecuredJwt expected = new SecuredJwt(
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoxNTg0OTk3MDEwLCJpYXQiOjE1ODQ5OTY5OTUsInVzZXJfZmluZ2VycHJpbnQiOiIyMjIyMiJ9.LVXHUdFxGPNNhdiEX3rqOOn_lMYUmcmOzxPbE2MRzcgpWf-4syrTzkPhd9upKbAhCO-MGu-LC8MqmApAyLDjJL5LOVAOObRADfjwI64lU6UZpUjkIfJiAspHuHx9AP2_ej8yl1Pfx9-UujHmO-D2DMjRNEGzHyNtXRctMNPFwnk",
            "12345"
        );

        SecuredJwt result = creator.generateJwt("11111");

        Assert.assertEquals(result.getToken(), expected.getToken());
        Assert.assertEquals(result.getFingerprint(), expected.getFingerprint());
    }

    @Test
    public void testGenerateJwtWithNullUserId() {
        exceptionRule.expect(IllegalArgumentException.class);

        JwtCreator creator = new JwtCreator(provider);

        creator.generateJwt(null);
    }

}