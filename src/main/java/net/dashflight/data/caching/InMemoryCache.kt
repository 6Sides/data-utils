package net.dashflight.data.caching

class InMemoryCache: CacheStore {

    private val cache = mutableMapOf<String, String>()


    override fun set(key: String, value: String): Boolean {
        println("Setting $key -> $value")
        cache[key] = value
        return true
    }

    override fun setWithExpiry(key: String, seconds: Int, value: String): Boolean {
        set(key, value)
        TODO("Not yet implemented")
    }

    override fun get(key: String): String? {
        return cache[key]
    }

    override fun del(key: String): Boolean {
        cache -= key
        return true
    }
}