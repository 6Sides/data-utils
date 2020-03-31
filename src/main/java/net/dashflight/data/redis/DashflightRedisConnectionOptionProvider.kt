package net.dashflight.data.redis

import net.dashflight.data.config.ConfigValue
import net.dashflight.data.config.Configurable

/**
 * Pulls the required redis connection options from s3
 */
class DashflightRedisConnectionOptionProvider : RedisConnectionOptionProvider, Configurable {

    init {
        registerWith(APP_NAME)
    }

    @ConfigValue("redis_host")
    private val host: String? = null

    @ConfigValue("redis_port")
    private val port = 0

    @ConfigValue("max_pool_size")
    private val maxPoolSize = 4

    override fun get(): RedisConnectionOptions {
        return RedisConnectionOptions(host!!, port, maxPoolSize)
    }

    companion object {
        private const val APP_NAME = "redis"
    }
}