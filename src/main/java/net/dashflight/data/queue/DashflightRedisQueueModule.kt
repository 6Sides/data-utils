package net.dashflight.data.queue

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder

class DashflightRedisQueueModule : AbstractModule() {
    override fun configure() {
        install(FactoryModuleBuilder().build(RedisConsumerNodeFactory::class.java))
    }
}