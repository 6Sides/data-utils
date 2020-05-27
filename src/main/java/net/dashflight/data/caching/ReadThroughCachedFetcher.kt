package net.dashflight.data.caching

import com.google.inject.Inject
import net.dashflight.data.logging.logger
import net.dashflight.data.redis.RedisClient
import net.dashflight.data.serialize.Serializer

/**
 * Basic read-through cache implementation. Attempts to read a value from the
 * cache and if it's absent the result is recomputed and cached.
 */
abstract class ReadThroughCachedFetcher<K, V> @Inject protected constructor(
        redisClient: CacheStore,
        serializer: Serializer
) : CachedFetcher<K, V>(redisClient, serializer) {

    companion object {
        private val LOG by logger()
    }

    override fun fetchResult(input: K): CacheableResult<V>? {
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