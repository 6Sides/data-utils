package net.dashflight.data.jwt.create;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.dashflight.data.keys.StaticRSAKeyPairProvider;

public class JwtCreatorModule extends AbstractModule {

    @Provides
    CreateJwtRequestProvider provideCreateJwtRequestProvider() {
        return new BasicCreateJwtRequestProvider(new StaticRSAKeyPairProvider());
    }

}
