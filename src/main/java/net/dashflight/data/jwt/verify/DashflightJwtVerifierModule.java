package net.dashflight.data.jwt.verify;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import net.dashflight.data.jwt.verify.request.DashflightVerifyJwtRequestProvider;
import net.dashflight.data.keys.DashflightRSAKeyPairModule;

public class DashflightJwtVerifierModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DashflightJwtVerifierModule());
        install(new DashflightRSAKeyPairModule());

        bind(JwtVerifier.class).to(BasicJwtVerifier.class);
    }

}
