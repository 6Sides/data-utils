package net.dashflight.data.redis

import com.google.inject.Inject
import com.google.inject.Singleton
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.JedisPool

/**
 * Handles interfacing with Redis cache. All methods in this class
 * interface with database 0.
 */
@Singleton
class RedisClient @Inject internal constructor(optionProvider: RedisConnectionOptionProvider) {
    /**
     * Redis connection pool
     */
    var pool: JedisPool
        protected set

    /**
     * Sets a key, value pair in the cache
     */
    operator fun set(key: String?, value: String?): Boolean {
        pool.resource.use { client -> return client.set(key, value) == "OK" }
    }

    /**
     * Sets a key, value pair in the cache with a specified expiry length (in seconds).
     */
    fun setWithExpiry(key: String?, seconds: Int, value: String?): Boolean {
        pool.resource.use { client -> return client.setex(key, seconds, value) == "OK" }
    }

    fun setWithExpiry(key: String, seconds: Int, value: ByteArray?): Boolean {
        pool.resource.use { client -> return client.setex(key.toByteArray(), seconds, value) == "OK" }
    }

    /**
     * Checks if a key exists in the cache
     */
    fun has(key: String?): Boolean {
        pool.resource.use { client -> return client.exists(key) }
    }

    /**
     * Query for a value with a key
     */
    operator fun get(key: String?): String {
        pool.resource.use { client -> return client[key] }
    }

    /**
     * Attempts to delete a key from the cache.
     * `this.client.del` returns number of keys removed.
     */
    fun del(key: String?): Boolean {
        pool.resource.use { client -> return client.del(key) > 0 }
    }

    fun getTTL(key: String?): Long {
        pool.resource.use { client -> return client.ttl(key) }
    }

    /**
     * Adds the members to the set at key `key`
     */
    fun sadd(key: String?, vararg members: String?): Long {
        pool.resource.use { client -> return client.sadd(key, *members) }
    }

    fun sismember(key: String?, member: String?): Boolean {
        pool.resource.use { client -> return client.sismember(key, member) }
    }

    fun setKeyExpire(key: String?, seconds: Int): Boolean {
        pool.resource.use { client -> return client.expire(key, seconds) == 1L }
    }

    init {
        val options = optionProvider.get()
        val config = GenericObjectPoolConfig()
        config.maxTotal = options.maxPoolSize
        pool = JedisPool(
                config,
                options.host,
                options.port
        )

        // Close pool on shutdown
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { pool.close() }))
    }
}