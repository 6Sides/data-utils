package net.dashflight.data.caching

import com.google.inject.Inject
import net.dashflight.data.logging.logger
import net.dashflight.data.redis.RedisClient
import redis.clients.jedis.JedisPool
import java.util.concurrent.Executors

/**
 * Implementation of the refresh-ahead caching strategy.
 */
abstract class RefreshAheadCachedFetcher<K, V> @Inject protected constructor(redisClient: RedisClient): CachedFetcher<K, V?>(redisClient) {

    companion object {
        private val LOG by logger()
        // Should be in (0, 1). The higher the value, the longer the value waits to be refreshed.
        private const val REFRESH_AHEAD_FACTOR = 0.5f
        private val threadPool = Executors.newCachedThreadPool()
    }

    private val redisPool: JedisPool = redisClient.pool

    @Throws(DataFetchException::class)
    override fun fetchResult(input: K): CacheableResult<V?> {
        var needsRefresh = false

        redisPool.resource.use { client ->
            needsRefresh = !client.exists(generateHash(input) + "rac")
        }

        if (needsRefresh) {
            // Refetch result and cache it asynchronously
            threadPool.submit {
                try {
                    val result = calculateResult(input)
                    cacheResult(input, result)
                    redis.setWithExpiry(generateHash(input) + "rac", (result.ttl * REFRESH_AHEAD_FACTOR).toInt(), "")
                } catch (e: DataFetchException) {
                    e.printStackTrace()
                }
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