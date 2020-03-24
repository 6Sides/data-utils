package net.dashflight.data.passwords;

import com.google.inject.AbstractModule;

public class DashflightPasswordServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PasswordService.class).to(BasicPasswordService.class);
    }
}
