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

        provider = ((token, fingerprint) -> new JwtVerificationRequirements(token, fingerprint, "C4F0693C150D245ACCF558402E4BAB0B67B611F0ACF7109912E8A74A931C71AB", "test", keyManager.getPublicKey()));
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
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoyMDU4MDM2OTk1LCJpYXQiOjE1ODQ5OTY5OTUsImZncCI6IkM0RjA2OTNDMTUwRDI0NUFDQ0Y1NTg0MDJFNEJBQjBCNjdCNjExRjBBQ0Y3MTA5OTEyRThBNzRBOTMxQzcxQUIifQ.W9vuHuWwV4nSt8xsAI7zA4XXe030Bv3GnC6ko84FnBtcQyuUiB0O36CbxKqlyGC64bRL1hoD2DfDZx6DBAqA3DO7iEuLLs-K0Df9e4HGnL2znZ-WH3wpl_cpTOaqg5mgp5Q5PiQQ4NX7gzi_TV82zfVChn-GUVMEoN91GgqfrXI",
                "60B420BB3851D9D47ACB933DBE70399BF6C92DA33AF01D4FB770E98C0325F41D3EBAF8986DA712C82BCD4D554BF0B54023C29B624DE9EF9C2F931EFC580F9AFB"
        );

        DecodedJWT result = verifier.verifyToken(input.getToken(), input.getFingerprint());


        Assert.assertEquals(result.getHeader(), "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9");
        Assert.assertEquals(result.getPayload(), "eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoyMDU4MDM2OTk1LCJpYXQiOjE1ODQ5OTY5OTUsImZncCI6IkM0RjA2OTNDMTUwRDI0NUFDQ0Y1NTg0MDJFNEJBQjBCNjdCNjExRjBBQ0Y3MTA5OTEyRThBNzRBOTMxQzcxQUIifQ");
        Assert.assertEquals(result.getSignature(), "W9vuHuWwV4nSt8xsAI7zA4XXe030Bv3GnC6ko84FnBtcQyuUiB0O36CbxKqlyGC64bRL1hoD2DfDZx6DBAqA3DO7iEuLLs-K0Df9e4HGnL2znZ-WH3wpl_cpTOaqg5mgp5Q5PiQQ4NX7gzi_TV82zfVChn-GUVMEoN91GgqfrXI");

        Assert.assertEquals(result.getClaim("fgp").asString(), "C4F0693C150D245ACCF558402E4BAB0B67B611F0ACF7109912E8A74A931C71AB");
        Assert.assertEquals(result.getClaim("user_id").asString(), "11111");
    }

}