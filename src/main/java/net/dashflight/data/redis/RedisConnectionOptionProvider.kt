package net.dashflight.data.redis

/**
 * Creates the connection options used by a Redis client
 */
interface RedisConnectionOptionProvider {
    fun get(): RedisConnectionOptions
}