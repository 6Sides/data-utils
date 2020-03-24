package net.dashflight.data.jwt.verify;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class JwtVerifierModule extends AbstractModule {

    @Provides
    VerifyJwtRequestProvider provideVerifyJwtRequestProvider() {
        return new BasicVerifyJwtRequestProvider();
    }
}
