package net.dashflight.data.keys;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import java.util.Collections;
import net.dashflight.data.config.RuntimeEnvironment;

public class EnvironmentAwareRSAKeyPairModule extends AbstractModule {

    @Provides
    RSAKeyPairDataProvider provideData() {
        return new DashflightRSAKeyPairDataProvider(RuntimeEnvironment.getCurrentEnvironment(), Collections.emptyMap());
    }

    @Provides
    RSAKeyPairTransformer provideTransformer() throws Exception {
        return new Base64RSAKeyTransformer();
    }
}
