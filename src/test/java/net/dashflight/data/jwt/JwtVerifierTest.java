package net.dashflight.data.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import net.dashflight.data.jwt.verify.JwtVerifier;
import net.dashflight.data.jwt.verify.VerifyJwtRequest;
import net.dashflight.data.keys.RSAKeyManager;
import net.dashflight.data.keys.StaticRSAKeyManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JwtVerifierTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    @Test
    public void testVerifyExpiredToken() {
        exceptionRule.expect(JWTVerificationException.class);

        JwtVerifier verifier = new JwtVerifier();
        RSAKeyManager keyManager = new StaticRSAKeyManager();

        SecuredJwt input = new SecuredJwt(
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoxNTg0OTk3MDEwLCJpYXQiOjE1ODQ5OTY5OTUsInVzZXJfZmluZ2VycHJpbnQiOiIyMjIyMiJ9.LVXHUdFxGPNNhdiEX3rqOOn_lMYUmcmOzxPbE2MRzcgpWf-4syrTzkPhd9upKbAhCO-MGu-LC8MqmApAyLDjJL5LOVAOObRADfjwI64lU6UZpUjkIfJiAspHuHx9AP2_ej8yl1Pfx9-UujHmO-D2DMjRNEGzHyNtXRctMNPFwnk",
                "12345"
        );

        VerifyJwtRequest request = VerifyJwtRequest.builder()
                .issuer("test")
                .token(input.getToken())
                .fingerprint(input.getFingerprint())
                .publicKey(keyManager.getPublicKey())
                .build();

        verifier.decodeJwtToken(request);
    }


    @Test
    public void testVerifyValidToken() {
        JwtVerifier verifier = new JwtVerifier();
        RSAKeyManager keyManager = new StaticRSAKeyManager();

        SecuredJwt input = new SecuredJwt(
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoyMDM4NTk2OTk1LCJpYXQiOjE1ODQ5OTY5OTUsInVzZXJfZmluZ2VycHJpbnQiOiIyMjIyMiJ9.UHHN0GSkqko3rJnN63yZMT8b38pApvUYE0ENf4GSHbSd5-JzoMjMP680XhFfR2rmRpckClPm0sEnk_NTu4_olnvytlkbzVHgMQh-Nkt6Wo1fJlO20DQX7ydDOk0rGDwqNxkbu6UZ7AiPes-K4tTZJ7KZnJnyBefwAxqNm8gXXJM",
                "12345"
        );

        VerifyJwtRequest request = VerifyJwtRequest.builder()
                .issuer("test")
                .token(input.getToken())
                .fingerprint(input.getFingerprint())
                .fingerprintHash("22222")
                .publicKey(keyManager.getPublicKey())
                .build();

        DecodedJWT result = verifier.decodeJwtToken(request);


        Assert.assertEquals(result.getHeader(), "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9");
        Assert.assertEquals(result.getPayload(), "eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoyMDM4NTk2OTk1LCJpYXQiOjE1ODQ5OTY5OTUsInVzZXJfZmluZ2VycHJpbnQiOiIyMjIyMiJ9");
        Assert.assertEquals(result.getSignature(), "UHHN0GSkqko3rJnN63yZMT8b38pApvUYE0ENf4GSHbSd5-JzoMjMP680XhFfR2rmRpckClPm0sEnk_NTu4_olnvytlkbzVHgMQh-Nkt6Wo1fJlO20DQX7ydDOk0rGDwqNxkbu6UZ7AiPes-K4tTZJ7KZnJnyBefwAxqNm8gXXJM");

        Assert.assertEquals(result.getClaim("user_fingerprint").asString(), "22222");
        Assert.assertEquals(result.getClaim("user_id").asString(), "11111");
    }


}