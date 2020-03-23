package net.dashflight.data.jwt;

import java.time.Instant;
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
                .issuedAt(Instant.ofEpochSecond(1584996995))
                .userId("11111")
                .privateKey(keyManager.getPrivateKey())
                .build();


        SecuredJwt expected = new SecuredJwt(
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ1c2VyX2lkIjoiMTExMTEiLCJpc3MiOiJ0ZXN0IiwiZXhwIjoxNTg0OTk3MDEwLCJpYXQiOjE1ODQ5OTY5OTUsInVzZXJfZmluZ2VycHJpbnQiOiI1OTk0NDcxQUJCMDExMTJBRkNDMTgxNTlGNkNDNzRCNEY1MTFCOTk4MDZEQTU5QjNDQUY1QTlDMTczQ0FDRkM1In0.AM7WG2kFtsUtqUBO1fQcwbbSckBogsndqdgpuTw9xiVnAELVQTrGF4EGdC-0Gj4Or9RlUTV9jixdHVMTwqnTMdoBzilNfP_v316AetCsGmsOH_GOqwvu8ig69A4DNtLmXHKY2hoDK6ZYneGdl0_OyStY3sqaTFd_sWkr8_K3bmtgXFBW9QgBf0TPEtWmWArFfZc_N0vA_Loyvno",
            "12345"
        );

        SecuredJwt result = creator.generateJwt(request);

        Assert.assertEquals(result.getToken(), expected.getToken());
        Assert.assertEquals(result.getFingerprint(), expected.getFingerprint());
    }
}