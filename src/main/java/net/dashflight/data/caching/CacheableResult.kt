package net.dashflight.data.caching

import java.time.OffsetDateTime

/**
 * Used by clients to return fetched results.
 *
 * @param <V> The type of the result being returned.
 */
class CacheableResult<V> private constructor(val result: V?, val ttl: Int) {
    /**
     * The result of the fetch
     */

    /**
     * The last time the result was calculated.
     */

    val lastUpdated = OffsetDateTime.now()


    companion object {
        private const val defaultCacheTtl = 900

        /**
         * @param result The result of the query.
         * @param cacheTTL The ttl in seconds.
         */
        fun <V> of(result: V?, cacheTTL: Int): CacheableResult<V> {
            return CacheableResult(result, cacheTTL)
        }

        fun <V> of(result: V?): CacheableResult<V> {
            return CacheableResult(result, defaultCacheTtl)
        }
    }
}