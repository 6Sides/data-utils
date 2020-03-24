package net.dashflight.data.jwt;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import net.dashflight.data.jwt.create.JwtCreator;
import net.dashflight.data.jwt.verify.JwtVerifier;

public class JwtUtilModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Key.get(JwtCreator.class));
        bind(Key.get(JwtVerifier.class));
    }
}
