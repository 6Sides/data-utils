package net.dashflight.data.caching

class InMemoryCache: CacheStore {

    private val cache = mutableMapOf<String, ByteArray>()


    override fun set(key: String, value: ByteArray): Boolean {
        println("Setting $key -> $value")
        cache[key] = value
        return true
    }

    override fun setWithExpiry(key: String, value: ByteArray, seconds: Int): Boolean {
        set(key, value)
        // TODO("Not yet implemented")
        return true
    }

    override fun get(key: String): ByteArray? {
        return cache[key]
    }

    override fun del(key: String): Boolean {
        cache -= key
        return true
    }

    override fun exists(key: String): Boolean {
        return cache.containsKey(key)
    }
}