package net.dashflight.data.jwt.create;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.dashflight.data.jwt.FingerprintService;
import net.dashflight.data.jwt.SecuredJwt;
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
        JwtCreator creator = new JwtCreator(provider, new FingerprintService(new Random(0)));
        String expectedToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoxNTg0OTk3MDEwLCJpYXQiOjE1ODQ5OTY5OTUsImZncCI6IkM0RjA2OTNDMTUwRDI0NUFDQ0Y1NTg0MDJFNEJBQjBCNjdCNjExRjBBQ0Y3MTA5OTEyRThBNzRBOTMxQzcxQUIifQ.B_QRI3uk0qo_or5owq5D7c5yDNI3f2Nz5mwj2IBqKTVpwI2NFZT3T1--N2VH0xMm0diBChmOYVV-wC4UsGOW9OW6evUzDHQFCTT4Jtrpc-uMsyx-gMU5NjrJRnhobdhFgXSr5DDhYHaMlDYsQPgFDiD9idx2gqptx3cOszAqa5Q";
        String expectedFingerprint = "60B420BB3851D9D47ACB933DBE70399BF6C92DA33AF01D4FB770E98C0325F41D3EBAF8986DA712C82BCD4D554BF0B54023C29B624DE9EF9C2F931EFC580F9AFB";

        SecuredJwt result = creator.generateFor("11111");

        Assert.assertEquals(expectedToken, result.getToken());
        Assert.assertEquals(expectedFingerprint, result.getFingerprint());
    }

    @Test
    public void testGenerateJwtWithNullUserId() {
        exceptionRule.expect(IllegalArgumentException.class);

        JwtCreator creator = new JwtCreator(provider, new FingerprintService(new Random(0)));

        creator.generateFor(null);
    }

}