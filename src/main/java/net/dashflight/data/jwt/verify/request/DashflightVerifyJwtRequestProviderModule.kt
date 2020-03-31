package net.dashflight.data.jwt.verify.request;

import com.google.inject.AbstractModule;

public class DashflightVerifyJwtRequestProviderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(VerifyJwtRequestProvider.class).to(DashflightVerifyJwtRequestProvider.class);
    }
}
