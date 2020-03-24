package net.dashflight.data.jwt.verify;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import net.dashflight.data.jwt.verify.request.DashflightVerifyJwtRequestProvider;
import net.dashflight.data.jwt.verify.request.DashflightVerifyJwtRequestProviderModule;
import net.dashflight.data.keys.BasicRSAKeyPairProvider;
import net.dashflight.data.keys.DashflightRSAKeyPairDataProvider;
import net.dashflight.data.keys.DashflightRSAKeyPairModule;
import net.dashflight.data.keys.RSAKeyPairProvider;

public class DashflightJwtVerifierModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DashflightVerifyJwtRequestProviderModule());

        bind(JwtVerifier.class).to(BasicJwtVerifier.class);
    }

}
