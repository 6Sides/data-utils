package net.dashflight.data.jwt.create;

import com.google.inject.AbstractModule;
import java.util.Random;
import net.dashflight.data.jwt.create.request.DashflightCreateJwtRequestProviderModule;
import net.dashflight.data.random.LavaRandom;

public class DashflightJwtCreatorModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DashflightCreateJwtRequestProviderModule());

        bind(JwtCreator.class);
        bind(Random.class).to(LavaRandom.class);
    }

}
