package net.dashflight.data.mfa;

import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BasicMfaServiceTest {

    final UUID USER_ID = UUID.fromString("b107f4cc-6df9-11ea-bc55-0242ac130003");

    BasicMfaService service;

    @Before
    public void setup() {
        service = new BasicMfaService(
                new MfaDataProvider() {
                    @Override
                    public String getTOTPCode(UUID userId) {
                        return "12345";
                    }

                    @Override
                    public String getUserSecret(UUID userId) {
                        return "54321";
                    }

                    @Override
                    public String getAuthenticatorURI(BasicUserData data) {
                        return String.format("test://%s:%s:%s", data.getUserId(), data.getUserSecret(), data.getEmail());
                    }
                },
                userId -> {
                    BasicUserData data = new BasicUserData();
                    data.setEmail("test@email.com");
                    return data;
                }
        );
    }

    @Test
    public void getTOTPCode() {
        Assert.assertEquals("12345", service.getTOTPCode(USER_ID));
    }

    @Test
    public void getUserSecret() {
        Assert.assertEquals("54321", service.getUserSecret(USER_ID));
    }

    @Test
    public void getAuthenticatorURI() {
        Assert.assertEquals("test://b107f4cc-6df9-11ea-bc55-0242ac130003:54321:test@email.com", service.getAuthenticatorURI(USER_ID));
    }
}