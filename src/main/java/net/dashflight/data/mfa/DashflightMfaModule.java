package net.dashflight.data.mfa;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class DashflightMfaModule extends AbstractModule {

    @Provides
    MfaDataProvider provideMfaDataProvider() {
        return new DashflightMfaDataProvider();
    }

    @Provides
    MfaUriDataProvider provideMfaUriDataProvider() {
        return new DashflightUriDataProvider();
    }

}
