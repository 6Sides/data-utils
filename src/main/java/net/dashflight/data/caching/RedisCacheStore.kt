package net.dashflight.data.caching

import com.google.crypto.tink.subtle.Base64
import com.google.inject.Inject
import net.dashflight.data.redis.RedisClient

class RedisCacheStore @Inject constructor(private val redis: RedisClient): CacheStore {

    override fun set(key: String, value: ByteArray): Boolean {
        return redis.set(key, Base64.encode(value))
    }

    override fun setWithExpiry(key: String, value: ByteArray, seconds: Long): Boolean {
        return redis.setWithExpiry(key, seconds.toInt(), Base64.encode(value))
    }

    override fun get(key: String): ByteArray? {
        return Base64.decode(redis.get(key))
    }

    override fun del(key: String): Boolean {
        return redis.del(key)
    }

    override fun exists(key: String): Boolean {
        return redis.has(key)
    }
}