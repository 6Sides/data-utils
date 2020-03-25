package net.dashflight.data.queue;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class DashflightRedisQueueModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(RedisConsumerNodeFactory.class));
    }

}
