package net.dashflight.data.jwt;

import com.google.inject.AbstractModule;
import net.dashflight.data.jwt.create.DashflightJwtCreatorModule;
import net.dashflight.data.jwt.verify.DashflightJwtVerifierModule;

public class DashflightJwtUtilModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DashflightJwtCreatorModule());
        install(new DashflightJwtVerifierModule());

        bind(JwtUtil.class);
    }

}
