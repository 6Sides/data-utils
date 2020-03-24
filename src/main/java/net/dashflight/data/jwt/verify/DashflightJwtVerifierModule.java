package net.dashflight.data.jwt.verify;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import net.dashflight.data.jwt.create.BasicCreateJwtRequestProvider;
import net.dashflight.data.keys.StaticRSAKeyPairProvider;

public class DashflightJwtVerifierModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Key.get(BasicVerifyJwtRequestProvider.class));
    }

}
