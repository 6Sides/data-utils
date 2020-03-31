package net.dashflight.data.caching

import com.amazonaws.util.Base64
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.google.common.io.ByteStreams
import com.google.inject.Inject
import net.dashflight.data.caching.Computable.DataFetchException
import net.dashflight.data.config.RuntimeEnvironment
import net.dashflight.data.redis.RedisClient
import net.dashflight.data.serialize.KryoPool
import net.dashflight.data.serialize.UUIDSerializer
import java.io.ByteArrayInputStream
import java.io.IOException
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
</V></K> */
abstract class CachedFetcher<K, V> protected constructor() {
    @Inject
    protected var redis: RedisClient? = null

    companion object {
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

    private val memoizer: Memoizer<K, CacheableResult<V?>>

    @Throws(DataFetchException::class)
    protected abstract fun memoizedGet(input: K): CacheableResult<V?>

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
        redis!!.del(generateHash(input))
    }

    /**
     * Returns the remaining ttl of the input,value pair associated with the specified object
     */
    fun getKeyTTL(input: K): Long {
        return redis!!.getTTL(generateHash(input))
    }

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
            memoizer.compute(input)!!
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
    protected fun getValueFromCache(input: K): CacheableResult<V>? {
        val blob = redis!![generateHash(input)]

        // Key exists, attempt to deserialize and return it's associated value.
        if (blob != null) {
            try {
                val kryo = kryoPool!!.obtain()
                val bytes = Base64.decode(blob.toByteArray())
                val result = kryo!!.readClassAndObject(Input(ByteArrayInputStream(bytes))) as CacheableResult<V>
                kryoPool!!.free(kryo)
                return result
            } catch (e: Exception) {
                // Key is in invalid format, delete it.
                redis!!.del(generateHash(input))
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * Serializes and caches a fetched result. Runs asynchronously.
     *
     * @param key The key to store the result at.
     * @param result The object to serialize and store.
     */
    protected fun cacheResult(key: K, result: CacheableResult<V>) {
        threadPool.submit {
            val kryo = kryoPool!!.obtain()
            val out = Output(1024, -1)
            kryo!!.writeClassAndObject(out, result)
            kryoPool!!.free(kryo)
            out.close()
            try {
                Input(out.buffer, 0, out.position()).use { input ->
                    val bytes = ByteStreams.toByteArray(input)
                    input.close()
                    redis!!.setWithExpiry(generateHash(key), result.TTL, Base64.encode(bytes))
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
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

    /**
     * Used by clients to return fetched results.
     *
     * @param <V> The type of the result being returned.
    </V> */
    class CacheableResult<V> {
        /**
         * The result of the fetch
         */
        var result: V? = null
            private set

        /**
         * The ttl the key was set with. Note this is NOT the remaining ttl of the key.
         */
        var TTL = 0
            private set
        val lastUpdated = OffsetDateTime.now()

        private constructor(result: V, ttl: Int) {
            this.result = result
            TTL = ttl
        }

        private constructor() {}

        companion object {
            private const val defaultCacheTTL = 900

            /**
             * @param result The result of the query.
             * @param cacheTTL The ttl in seconds.
             */
            fun <V> of(result: V, cacheTTL: Int): CacheableResult<V> {
                return CacheableResult(result, cacheTTL)
            }

            fun <V> of(result: V): CacheableResult<V> {
                return CacheableResult(result, defaultCacheTTL)
            }
        }
    }

    init {
        memoizer = Memoizer(
            object : Computable<K, CacheableResult<V?>> {
                override fun compute(key: K): CacheableResult<V?> {
                    return memoizedGet(key)
                }
            }
        )
    }
}