package net.dashflight.data.jwt.create.request;

import com.google.inject.AbstractModule;

public class DashflightCreateJwtRequestProviderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CreateJwtRequestProvider.class).to(DashflightCreateJwtRequestProvider.class);
    }
}
