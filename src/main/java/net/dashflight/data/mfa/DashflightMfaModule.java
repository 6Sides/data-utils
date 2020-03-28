package net.dashflight.data.mfa;

import com.google.inject.AbstractModule;

public class DashflightMfaModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MfaUriDataProvider.class).to(DashflightUriDataProvider.class);

        bind(MfaService.class).to(BasicMfaService.class);
    }

}
