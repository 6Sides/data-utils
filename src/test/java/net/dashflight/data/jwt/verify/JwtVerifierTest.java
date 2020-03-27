package net.dashflight.data.jwt.verify;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import net.dashflight.data.jwt.SecuredJwt;
import net.dashflight.data.jwt.verify.request.JwtVerificationRequirements;
import net.dashflight.data.jwt.verify.request.VerifyJwtRequestProvider;
import net.dashflight.data.keys.RSAKeyPairProvider;
import net.dashflight.data.keys.StaticRSAKeyPairProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JwtVerifierTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    public VerifyJwtRequestProvider provider;

    @Before
    public void setup() {
        RSAKeyPairProvider keyManager = new StaticRSAKeyPairProvider();

        provider = ((token, fingerprint) -> JwtVerificationRequirements.builder()
                .issuer("test")
                .token(token)
                .fingerprint(fingerprint)
                .publicKey(keyManager.getPublicKey())
                .build());
    }


    @Test
    public void testVerifyExpiredToken() {
        exceptionRule.expect(JWTVerificationException.class);

        JwtVerifier verifier = new JwtVerifier(provider);

        SecuredJwt input = new SecuredJwt(
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoxNTg0OTk3MDEwLCJpYXQiOjE1ODQ5OTY5OTUsInVzZXJfZmluZ2VycHJpbnQiOiIyMjIyMiJ9.LVXHUdFxGPNNhdiEX3rqOOn_lMYUmcmOzxPbE2MRzcgpWf-4syrTzkPhd9upKbAhCO-MGu-LC8MqmApAyLDjJL5LOVAOObRADfjwI64lU6UZpUjkIfJiAspHuHx9AP2_ej8yl1Pfx9-UujHmO-D2DMjRNEGzHyNtXRctMNPFwnk",
                "12345"
        );

        verifier.verifyToken(input.getToken(), input.getFingerprint());
    }


    @Test
    public void testVerifyValidToken() {
        JwtVerifier verifier = new JwtVerifier(provider);

        SecuredJwt input = new SecuredJwt(
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoyMDM4NTk2OTk1LCJpYXQiOjE1ODQ5OTY5OTUsInVzZXJfZmluZ2VycHJpbnQiOiIyMjIyMiJ9.UHHN0GSkqko3rJnN63yZMT8b38pApvUYE0ENf4GSHbSd5-JzoMjMP680XhFfR2rmRpckClPm0sEnk_NTu4_olnvytlkbzVHgMQh-Nkt6Wo1fJlO20DQX7ydDOk0rGDwqNxkbu6UZ7AiPes-K4tTZJ7KZnJnyBefwAxqNm8gXXJM",
                "12345"
        );

        DecodedJWT result = verifier.verifyToken(input.getToken(), input.getFingerprint());


        Assert.assertEquals(result.getHeader(), "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9");
        Assert.assertEquals(result.getPayload(), "eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoyMDM4NTk2OTk1LCJpYXQiOjE1ODQ5OTY5OTUsInVzZXJfZmluZ2VycHJpbnQiOiIyMjIyMiJ9");
        Assert.assertEquals(result.getSignature(), "UHHN0GSkqko3rJnN63yZMT8b38pApvUYE0ENf4GSHbSd5-JzoMjMP680XhFfR2rmRpckClPm0sEnk_NTu4_olnvytlkbzVHgMQh-Nkt6Wo1fJlO20DQX7ydDOk0rGDwqNxkbu6UZ7AiPes-K4tTZJ7KZnJnyBefwAxqNm8gXXJM");

        Assert.assertEquals(result.getClaim("user_fingerprint").asString(), "22222");
        Assert.assertEquals(result.getClaim("user_id").asString(), "11111");
    }

}