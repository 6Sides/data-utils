package net.dashflight.data.jwt.verify;

import com.google.inject.AbstractModule;
import net.dashflight.data.jwt.verify.request.DashflightVerifyJwtRequestProviderModule;

public class DashflightJwtVerifierModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DashflightVerifyJwtRequestProviderModule());

        bind(JwtVerifier.class);
    }

}
