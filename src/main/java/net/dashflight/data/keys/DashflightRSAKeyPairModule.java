package net.dashflight.data.keys;

import com.google.inject.AbstractModule;

/**
 * Provides environment specific implementations used for Dashflight project.
 */
public class DashflightRSAKeyPairModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RSAKeyPairDataProvider.class).toInstance(new DashflightRSAKeyPairDataProvider());
        bind(RSAKeyPairTransformer.class).toInstance(new Base64RSAKeyTransformer());

        bind(RSAKeyPairProvider.class).to(DynamicRSAKeyPairProvider.class);
    }

}
