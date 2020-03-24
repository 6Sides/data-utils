package net.dashflight.data.mfa;

import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MFADataProviderTest {

    final UUID USER_ID = UUID.fromString("09334650-6df6-11ea-bc55-0242ac130003");

    MFADataProvider dataProvider;

    @Before
    public void setup() {
        dataProvider = new MFADataProvider() {
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
        };
    }

    @Test
    public void testGetTOTPCode() {
        Assert.assertEquals("12345", dataProvider.getTOTPCode(USER_ID));
    }

    @Test
    public void testGetUserSecret() {
        Assert.assertEquals("54321", dataProvider.getUserSecret(USER_ID));
    }

    @Test
    public void getAuthenticatorURI() {
        BasicUserData data = new BasicUserData();
        data.setUserId(USER_ID);
        data.setUserSecret("54321");
        data.setEmail("test@email.com");

        Assert.assertEquals("test://09334650-6df6-11ea-bc55-0242ac130003:54321:test@email.com", dataProvider.getAuthenticatorURI(data));
    }
}