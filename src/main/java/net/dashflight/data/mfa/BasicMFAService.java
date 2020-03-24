package net.dashflight.data.mfa;

import com.google.inject.Inject;
import java.util.UUID;

/**
 * Basic implementation of an mfa service.
 */
public class BasicMFAService {

    private MFADataProvider mfaDataProvider;
    private MFA_URIDataProvider uriDataProvider;


    @Inject
    public BasicMFAService(MFADataProvider dataProvider, MFA_URIDataProvider uriDataProvider) {
        this.mfaDataProvider = dataProvider;
        this.uriDataProvider = uriDataProvider;
    }


    /**
     * Returns user's temporary password
     */
    public String getTOTPCode(UUID userId) {
        return mfaDataProvider.getTOTPCode(userId);
    }

    /**
     * Returns user's secret
     */
    public String getUserSecret(UUID userId) {
        return mfaDataProvider.getUserSecret(userId);
    }

    /**
     * Returns URI associated with user
     */
    public String getAuthenticatorURI(UUID userId) {
        BasicUserData data = uriDataProvider.getData(userId);
        data.setUserId(userId);
        data.setUserSecret(getUserSecret(userId));

        return mfaDataProvider.getAuthenticatorURI(data);
    }

}
