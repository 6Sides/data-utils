package net.dashflight.data.caching

import com.google.inject.Inject
import net.dashflight.data.logging.logger
import net.dashflight.data.redis.RedisClient
import redis.clients.jedis.JedisPool
import java.util.concurrent.Executors

/**
 * Implementation of the refresh-ahead caching strategy.
 */
abstract class RefreshAheadCachedFetcher<K, V> @Inject protected constructor(private val redisClient: CacheStore): CachedFetcher<K, V>(redisClient) {

    companion object {
        private val LOG by logger()
        // Should be in (0, 1). The higher the value, the longer the value waits to be refreshed.
        private const val REFRESH_AHEAD_FACTOR = 0.5f
        private val threadPool = Executors.newCachedThreadPool()
    }

    override fun fetchResult(input: K): CacheableResult<V> {
        var needsRefresh = false

        needsRefresh = !redisClient.exists(generateHash(input) + "rac")

        if (needsRefresh) {
            // Refetch result and cache it asynchronously
            threadPool.submit {
                val result = calculateResult(input)
                cacheResult(input, result)
                redisClient.setWithExpiry(generateHash(input) + "rac", byteArrayOf(), (result.ttl * REFRESH_AHEAD_FACTOR).toInt())
            }
        }

        var result = super.getValueFromCache(input)

        // Refetch result and cache it
        if (result == null) {
            LOG.debug { "Cache miss for key $input. Recomputing value..." }
            result = calculateResult(input)
            cacheResult(input, result)
        } else {
            LOG.debug { "Cache hit for key $input" }
        }

        return result
    }
}