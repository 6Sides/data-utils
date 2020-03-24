package net.dashflight.data.jwt.create;

import com.google.inject.AbstractModule;
import net.dashflight.data.jwt.create.request.DashflightCreateJwtRequestProviderModule;
import net.dashflight.data.keys.BasicRSAKeyPairProvider;
import net.dashflight.data.keys.DashflightRSAKeyPairModule;
import net.dashflight.data.keys.RSAKeyPairProvider;

public class DashflightJwtCreatorModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DashflightCreateJwtRequestProviderModule());
        install(new DashflightRSAKeyPairModule());

        bind(JwtCreator.class).to(BasicJwtCreator.class);
    }

}
