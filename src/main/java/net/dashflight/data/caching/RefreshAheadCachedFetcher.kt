package net.dashflight.data.caching

import net.dashflight.data.caching.Computable.DataFetchException
import redis.clients.jedis.JedisPool
import java.util.concurrent.Executors

/**
 * Implementation of the refresh-ahead caching strategy.
 */
abstract class RefreshAheadCachedFetcher<K, V> : CachedFetcher<K, V?>() {
    private val redisPool: JedisPool?

    @Throws(DataFetchException::class)
    override fun memoizedGet(input: K): CacheableResult<V?> {
        var needsRefresh: Boolean = false
        redisPool!!.resource.use { client -> needsRefresh = !client.exists(generateHash(input) + "rac") }
        if (needsRefresh) {
            // Refetch result and cache it asynchronously
            threadPool.submit {
                try {
                    val result = fetchResult(input)
                    cacheResult(input, result!!)
                    redis!!.setWithExpiry(generateHash(input) + "rac", (result.TTL * REFRESH_AHEAD_FACTOR) as Int, "")
                } catch (e: DataFetchException) {
                    e.printStackTrace()
                }
            }
        }
        var result = super.getValueFromCache(input)
        if (result == null) {
            // Refetch result and cache it
            result = fetchResult(input)
            cacheResult(input, result)
        }
        return result
    }

    companion object {
        // Should be in (0, 1). The higher the value, the longer the value waits to be refreshed.
        private const val REFRESH_AHEAD_FACTOR = 0.5f
        private val threadPool = Executors.newCachedThreadPool()
    }

    // Initializes the redis pool with the parents redis instance
    init {
        redisPool = super.redis?.pool
    }
}