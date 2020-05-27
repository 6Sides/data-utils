package net.dashflight.data.caching

import net.dashflight.data.logging.logger


class InMemoryCache: CacheStore {

    companion object {
        private val LOG by logger()
    }

    private val cache = ConcurrentExpiryHashMap<String, ByteArray>()

    override fun set(key: String, value: ByteArray): Boolean {
        LOG.debug { "Caching $key -> $value" }
        cache.put(key, value)
        return true
    }

    override fun setWithExpiry(key: String, value: ByteArray, seconds: Long): Boolean {
        LOG.debug { "Caching $key -> $value (Expires in $seconds seconds)" }
        cache.put(key, value, seconds)
        return true
    }

    override fun get(key: String): ByteArray? {
        return cache[key]
    }

    override fun del(key: String): Boolean {
        cache.remove(key)
        return true
    }

    override fun exists(key: String): Boolean {
        return cache.containsKey(key)
    }
}