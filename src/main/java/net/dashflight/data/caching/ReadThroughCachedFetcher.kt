package net.dashflight.data.caching;

import net.dashflight.data.caching.Computable.DataFetchException;

/**
 * Basic read-through cache implementation. Attempts to read a value from the
 * cache and if it's absent the result is recomputed and cached.
 */
public abstract class ReadThroughCachedFetcher<K, V> extends CachedFetcher<K, V> {

    @Override
    protected final CacheableResult<V> memoizedGet(K input) throws DataFetchException {
        CacheableResult<V> result = super.getValueFromCache(input);

        if (result == null) {
            // Refetch result and cache it
            result = this.fetchResult(input);
            this.cacheResult(input, result);
        }

        return result;
    }
}
