package net.dashflight.data.jwt.create;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import net.dashflight.data.keys.StaticRSAKeyPairProvider;

public class DashflightJwtCreatorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Key.get(BasicCreateJwtRequestProvider.class));
    }

}
