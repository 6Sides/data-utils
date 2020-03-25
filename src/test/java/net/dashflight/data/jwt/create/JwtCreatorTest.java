package net.dashflight.data.jwt.create;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import net.dashflight.data.jwt.create.request.CreateJwtRequest;
import net.dashflight.data.jwt.create.request.CreateJwtRequestProvider;
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

        provider = userId -> {
            Map<String, String> claims = new HashMap<>();

            claims.put("user_id", userId);
            claims.put("user_fingerprint", "22222");

            return CreateJwtRequest.builder()
                    .issuer("test")
                    .ttl(15)
                    .issuedAt(Instant.ofEpochSecond(1584996995))
                    .claims(claims)
                    .privateKey(keyManager.getPrivateKey())
                    .build();
            };
    }

    @Test
    public void testGenerateJwt() {
        JwtCreator creator = new JwtCreator(provider);
        String expected = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoxNTg0OTk3MDEwLCJpYXQiOjE1ODQ5OTY5OTUsInVzZXJfZmluZ2VycHJpbnQiOiIyMjIyMiJ9.LVXHUdFxGPNNhdiEX3rqOOn_lMYUmcmOzxPbE2MRzcgpWf-4syrTzkPhd9upKbAhCO-MGu-LC8MqmApAyLDjJL5LOVAOObRADfjwI64lU6UZpUjkIfJiAspHuHx9AP2_ej8yl1Pfx9-UujHmO-D2DMjRNEGzHyNtXRctMNPFwnk";

        String result = creator.generateFor("11111");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGenerateJwtWithNullUserId() {
        exceptionRule.expect(IllegalArgumentException.class);

        JwtCreator creator = new JwtCreator(provider);

        creator.generateFor(null);
    }

}