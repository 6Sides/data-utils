package net.dashflight.data.jwt.create;

import com.google.inject.AbstractModule;
import net.dashflight.data.jwt.create.request.DashflightCreateJwtRequestProviderModule;

public class DashflightJwtCreatorModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DashflightCreateJwtRequestProviderModule());

        bind(JwtCreator.class);
    }

}
