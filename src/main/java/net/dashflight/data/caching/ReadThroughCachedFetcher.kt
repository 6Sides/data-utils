package net.dashflight.data.caching

import net.dashflight.data.caching.Computable.DataFetchException

/**
 * Basic read-through cache implementation. Attempts to read a value from the
 * cache and if it's absent the result is recomputed and cached.
 */
abstract class ReadThroughCachedFetcher<K, V> : CachedFetcher<K, V?>() {

    @Throws(DataFetchException::class)
    override fun memoizedGet(input: K): CacheableResult<V?> {
        var result = super.getValueFromCache(input)
        if (result == null) {
            // Refetch result and cache it
            result = fetchResult(input)
            cacheResult(input, result)
        }
        return result
    }
}