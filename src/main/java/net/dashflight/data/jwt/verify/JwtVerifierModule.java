package net.dashflight.data.jwt.verify;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.dashflight.data.keys.StaticRSAKeyPairProvider;

public class JwtVerifierModule extends AbstractModule {

    @Provides
    VerifyJwtRequestProvider provideVerifyJwtRequestProvider() {
        return new BasicVerifyJwtRequestProvider(new StaticRSAKeyPairProvider());
    }
}
