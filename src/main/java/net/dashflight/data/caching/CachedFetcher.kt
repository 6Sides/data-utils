package net.dashflight.data.caching

import com.google.inject.Inject
import net.dashflight.data.config.RuntimeEnvironment
import net.dashflight.data.logging.logger
import net.dashflight.data.serialize.KryoSerializer
import net.dashflight.data.serialize.Serializer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.Executors

/**
 * Handles caching of results. Should be used for computationally expensive queries.
 *
 * This class automatically handles object serialization/deserialization at the expense
 * of slightly larger serialization data.
 *
 * @param <K> The data type required to make the query (The `key`)
 * @param <V> The result type (The `value`)
 */
abstract class CachedFetcher<K, V> @Inject protected constructor(private val cache: CacheStore) {

    companion object {
        private val LOG by logger()

        // Use cached thread pool since caching tasks are short lived
        private val threadPool = Executors.newCachedThreadPool()
        private val currentEnvironment: RuntimeEnvironment = RuntimeEnvironment.currentEnvironment
    }

    private val serializer: Serializer = KryoSerializer()

    private val collisionCache: ConcurrentMap<K, CacheableResult<V>> = ConcurrentHashMap()

    private val memoizer: Memoizer<K, CacheableResult<V>> = Memoizer { this.calculateResult(it) }

    protected abstract fun calculateResult(key: K): CacheableResult<V>

    /**
     * Fetches and returns the result based on the input
     */
    protected abstract fun fetchResult(input: K): CacheableResult<V>

    /**
     * Invalidates the result associated with the specified object
     */
    /*fun invalidate(key: K) {
        redis.del(generateHash(key))
    }*/

    /**
     * Returns the remaining ttl of the input,value pair associated with the specified object
     */
    /*fun getKeyTTL(input: K): Long {
        return redis.getTTL(generateHash(input)) ?: 0
    }*/

    /**
     * Fetches data based on the specified key.
     *
     * @param key The data being used to make the query
     * @return The value associated with the specified key (can be null)
     */
    operator fun get(key: K): CacheableResult<V>? {
        return fetchResult(key)
    }

    /**
     * Attempts to get a value from the cache based on the input.
     *
     * @return A CacheableResult containing the computed result, or null if none is found.
     */
    protected fun getValueFromCache(key: K): CacheableResult<V>? {
        val objectHash = generateHash(key)
        val blob = cache.get(objectHash)

        // Key exists, attempt to deserialize and return it's associated value.
        return blob?.let {
            return serializer.readObject(blob) as? CacheableResult<V>
        } ?: collisionCache[key]
    }

    /**
     * Serializes and caches a fetched result. Runs asynchronously.
     *
     * @param key The key to store the result at.
     * @param result The object to serialize and store.
     */
    protected fun cacheResult(key: K, result: CacheableResult<V>) {
        LOG.debug { "Caching key $key -> $result" }

        val res = collisionCache.putIfAbsent(key, result)

        // If cache doesn't contain value begin process of caching it
        if (res == null) {
            threadPool.submit {
                val resultBytes = serializer.writeObject(result)
                cache.setWithExpiry(generateHash(key), resultBytes, result.ttl.toLong())
                collisionCache -= key
            }
        }
    }

    /**
     * Generates the key to be used in the key,value pair inserted in the cache.
     * @param key The key to hash.
     * @return A hash of the key mixed with any other desired data (e.g. environment)
     */
    protected open fun generateHash(key: K): String {
        return (currentEnvironment.name.hashCode() + javaClass.hashCode() + key.hashCode()).toString() + ""
    }
}