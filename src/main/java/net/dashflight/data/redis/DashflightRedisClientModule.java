package net.dashflight.data.redis;

import com.google.inject.AbstractModule;

public class DashflightRedisClientModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RedisConnectionOptionProvider.class).to(DashflightRedisConnectionOptionProvider.class);
    }

}
