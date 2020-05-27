package net.dashflight.data.caching

import com.google.inject.Inject
import net.dashflight.data.logging.logger
import java.util.concurrent.Executors
import net.dashflight.data.serialize.Serializer


/**
 * Implementation of the refresh-ahead caching strategy.
 */
abstract class RefreshAheadCachedFetcher<K, V> @Inject protected constructor(
        private val redisClient: CacheStore,
        serializer: Serializer
): CachedFetcher<K, V>(redisClient, serializer) {

    companion object {
        private val LOG by logger()
        // Should be in (0, 1). The higher the value, the longer the value waits to be refreshed.
        private const val REFRESH_AHEAD_FACTOR = 0.5f
        private val threadPool = Executors.newCachedThreadPool()
    }

    override fun fetchResult(input: K): CacheableResult<V>? {
        val needsRefresh: Boolean = !redisClient.exists(generateHash(input) + "rac")

        if (needsRefresh) {
            // Refetch result and cache it asynchronously
            threadPool.submit {
                val result = calculateResult(input)
                cacheResult(input, result)
                redisClient.setWithExpiry(generateHash(input) + "rac", byteArrayOf(), (result.ttl * REFRESH_AHEAD_FACTOR).toLong())
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