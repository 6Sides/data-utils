package net.dashflight.data.caching


interface CacheStore {

    fun set(key: String, value: String): Boolean

    fun setWithExpiry(key: String, seconds: Int, value: String): Boolean

    fun get(key: String): String?

    fun del(key: String): Boolean

}