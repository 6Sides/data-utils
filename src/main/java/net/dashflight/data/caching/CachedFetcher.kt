package net.dashflight.data.caching

import com.amazonaws.util.Base64
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.google.common.io.ByteStreams
import com.google.inject.Inject
import net.dashflight.data.config.RuntimeEnvironment
import net.dashflight.data.logging.logger
import net.dashflight.data.redis.RedisClient
import net.dashflight.data.serialize.KryoPool
import net.dashflight.data.serialize.UUIDSerializer
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.time.OffsetDateTime
import java.util.*
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
abstract class CachedFetcher<K, V> @Inject protected constructor(protected val redis: CacheStore) {

    companion object {
        private val LOG by logger()

        // Use cached thread pool since caching tasks are short lived
        private val threadPool = Executors.newCachedThreadPool()
        protected val kryoPool = KryoPool.pool
        private val currentEnvironment: RuntimeEnvironment = RuntimeEnvironment.currentEnvironment

        init {
            KryoPool.registerClass(CacheableResult::class.java)
            KryoPool.registerClass(OffsetDateTime::class.java)
            KryoPool.registerClass(UUID::class.java, UUIDSerializer())
        }
    }

    private val memoizer: Memoizer<K, CacheableResult<V?>> = Memoizer { this.calculateResult(it) }

    protected abstract fun calculateResult(key: K): CacheableResult<V?>

    /**
     * Fetches and returns the result based on the input
     *
     * @throws DataFetchException If a result is unable to be obtained
     */
    @Throws(DataFetchException::class)
    protected abstract fun fetchResult(input: K): CacheableResult<V?>

    /**
     * Invalidates the result associated with the specified object
     */
    fun invalidate(input: K) {
        redis.del(generateHash(input))
    }

    /**
     * Returns the remaining ttl of the input,value pair associated with the specified object
     */
    /*fun getKeyTTL(input: K): Long {
        return redis.getTTL(generateHash(input)) ?: 0
    }*/

    /**
     * Fetches data based on the specified key.
     *
     * @param input The data being used to make the query
     * @return The value associated with the specified key (can be null)
     *
     * @throws DataFetchException when a result couldn't be obtained
     */
    @Throws(DataFetchException::class)
    operator fun get(input: K): CacheableResult<V?> {
        return try {
            fetchResult(input)
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
            throw DataFetchException(ex.message!!)
        }
    }

    /**
     * Attempts to get a value from the cache based on the input.
     *
     * @return A CacheableResult containing the computed result, or null if none is found.
     */
    protected fun getValueFromCache(input: K): CacheableResult<V?>? {
        val objectHash = generateHash(input)
        val blob = redis.get(objectHash)

        // Key exists, attempt to deserialize and return it's associated value.
        return blob?.let {
            return try {
                val kryo = kryoPool.obtain()
                val bytes = Base64.decode(blob.toByteArray())
                val result = kryo?.readClassAndObject(Input(ByteArrayInputStream(bytes))) as? CacheableResult<V?>

                kryoPool.free(kryo)
                result
            } catch (ex: Exception) {
                // Key is in invalid format, delete it.
                redis.del(objectHash)
                LOG.error { ex.message }
                null
            }
        }
    }

    /**
     * Serializes and caches a fetched result. Runs asynchronously.
     *
     * @param key The key to store the result at.
     * @param result The object to serialize and store.
     */
    protected fun cacheResult(key: K, result: CacheableResult<V>) {
        LOG.debug { "Caching key $key -> $result" }

        threadPool.submit {
            val kryo = kryoPool.obtain()

            Output(1024, -1).use { output ->
                try {
                    kryo?.writeClassAndObject(output, result)
                    kryoPool.free(kryo)

                    val resultBytes = Input(output.buffer, 0, output.position()).use { input ->
                        ByteStreams.toByteArray(input)
                    }

                    redis.setWithExpiry(generateHash(key), result.ttl, Base64.encode(resultBytes).toString(Charset.defaultCharset()))
                } catch (ex: Exception) {
                    LOG.warn { ex.message }
                }
            }
        }
    }

    /**
     * Generates the key to be used in the key,value pair inserted in the cache.
     * @param key The key to hash.
     * @return A hash of the key mixed with any other desired data (e.g. environment)
     */
    protected fun generateHash(key: K): String {
        return (currentEnvironment.name.hashCode() + javaClass.hashCode() + key.hashCode()).toString() + ""
    }
}