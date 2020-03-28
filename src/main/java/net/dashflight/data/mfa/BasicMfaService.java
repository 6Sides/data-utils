package net.dashflight.data.mfa;

import com.google.inject.Inject;
import java.util.UUID;

/**
 * Basic implementation of an mfa service.
 */
class BasicMfaService implements MfaService {

    private final MfaDataProvider mfaDataProvider;
    private final MfaUriDataProvider uriDataProvider;


    @Inject
    public BasicMfaService(MfaDataProvider dataProvider, MfaUriDataProvider uriDataProvider) {
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

    @Override
    public String generateOneTimePassword() {
        return mfaDataProvider.generateOneTimePassword();
    }

}
