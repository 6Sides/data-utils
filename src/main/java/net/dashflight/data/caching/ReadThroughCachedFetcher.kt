package net.dashflight.data.caching

import com.google.inject.Inject
import net.dashflight.data.logging.logger
import net.dashflight.data.redis.RedisClient

/**
 * Basic read-through cache implementation. Attempts to read a value from the
 * cache and if it's absent the result is recomputed and cached.
 */
abstract class ReadThroughCachedFetcher<K, V> @Inject protected constructor(redisClient: CacheStore) : CachedFetcher<K, V?>(redisClient) {

    companion object {
        private val LOG by logger()
    }

    @Throws(DataFetchException::class)
    override fun fetchResult(input: K): CacheableResult<V?> {
        var result = super.getValueFromCache(input)

        // Refetch result and cache it if it was not found
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