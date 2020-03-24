package net.dashflight.data.mfa;

import com.google.inject.AbstractModule;

public class DashflightMfaModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MfaDataProvider.class).toInstance(new DashflightMfaDataProvider());
        bind(MfaUriDataProvider.class).toInstance(new DashflightUriDataProvider());

        bind(MfaService.class).to(BasicMfaService.class);
    }

}
