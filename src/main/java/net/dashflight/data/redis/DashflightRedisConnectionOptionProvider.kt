package net.dashflight.data.redis

import hydro.engine.Hydro.hydrate

/**
 * Pulls the required redis connection options from s3
 */
class DashflightRedisConnectionOptionProvider : RedisConnectionOptionProvider {

    private val host: String by hydrate("redis_host")

    private val port: Int by hydrate("redis_port")

    private val maxPoolSize: Int by hydrate("max_pool_size", 4)

    override fun get(): RedisConnectionOptions {
        return RedisConnectionOptions(host, port, maxPoolSize)
    }
}