package net.dashflight.data.redis

import com.google.inject.AbstractModule

class DashflightRedisClientModule : AbstractModule() {
    override fun configure() {
        bind(RedisConnectionOptionProvider::class.java).to(DashflightRedisConnectionOptionProvider::class.java)
    }
}