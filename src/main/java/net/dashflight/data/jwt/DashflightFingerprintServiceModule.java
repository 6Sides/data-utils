package net.dashflight.data.jwt;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import java.security.SecureRandom;
import net.dashflight.data.random.LavaRandom;

public class DashflightFingerprintServiceModule extends AbstractModule {

    @Provides
    SecureRandom provideRandom() {
        return new LavaRandom();
    }

}
