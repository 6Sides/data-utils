package net.dashflight.data.caching;

import com.amazonaws.util.Base64;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.dashflight.data.caching.Computable.DataFetchException;
import net.dashflight.data.config.RuntimeEnvironment;
import net.dashflight.data.redis.RedisClient;
import net.dashflight.data.serialize.KryoPool;
import net.dashflight.data.serialize.UUIDSerializer;


/**
 * Handles caching of results. Should be used for computationally expensive queries.
 *
 * This class automatically handles object serialization/deserialization at the expense
 * of slightly larger serialization data.
 *
 * @param <K> The data type required to make the query (The `key`)
 * @param <V> The result type (The `value`)
 */
public abstract class CachedFetcher<K, V> {

    // Use cached thread pool since caching tasks are short lived
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    protected static final Pool<Kryo> kryoPool = KryoPool.getPool();

    @Inject
    protected RedisClient redis;

    private static final RuntimeEnvironment currentEnvironment = RuntimeEnvironment.getCurrentEnvironment();


    static {
        KryoPool.registerClass(CacheableResult.class);
        KryoPool.registerClass(OffsetDateTime.class);
        KryoPool.registerClass(UUID.class, new UUIDSerializer());
    }


    private final Memoizer<K, CacheableResult<V>> memoizer;


    protected CachedFetcher() {
        this.memoizer = new Memoizer<>(this::memoizedGet);
    }

    protected abstract CacheableResult<V> memoizedGet(K input) throws DataFetchException;


    /**
     * Fetches and returns the result based on the input
     *
     * @throws DataFetchException If a result is unable to be obtained
     */
    protected abstract CacheableResult<V> fetchResult(K input) throws DataFetchException;


    /**
     * Invalidates the result associated with the specified object
     */
    public final void invalidate(K input) {
        redis.del(this.generateHash(input));
    }

    /**
     * Returns the remaining ttl of the input,value pair associated with the specified object
     */
    public final long getKeyTTL(K input) {
        return redis.getTTL(this.generateHash(input));
    }


    /**
     * Fetches data based on the specified key.
     *
     * @param input The data being used to make the query
     * @return The value associated with the specified key (can be null)
     *
     * @throws DataFetchException when a result couldn't be obtained
     */
    public final CacheableResult<V> get(K input) throws DataFetchException {
        try {
            return this.memoizer.compute(input);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw new DataFetchException(ex.getMessage());
        }
    }

    /**
     * Attempts to get a value from the cache based on the input.
     *
     * @return A CacheableResult containing the computed result, or null if none is found.
     */
    protected final CacheableResult<V> getValueFromCache(K input) {
        String blob = redis.get(this.generateHash(input));

        // Key exists, attempt to deserialize and return it's associated value.
        if (blob != null) {
            try {
                Kryo kryo = kryoPool.obtain();
                byte[] bytes = Base64.decode(blob.getBytes());

                CacheableResult<V> result = (CacheableResult<V>) kryo.readClassAndObject(new Input(new ByteArrayInputStream(bytes)));

                kryoPool.free(kryo);

                return result;
            } catch (Exception e) {
                // Key is in invalid format, delete it.
                redis.del(this.generateHash(input));
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Serializes and caches a fetched result. Runs asynchronously.
     *
     * @param key The key to store the result at.
     * @param result The object to serialize and store.
     */
    protected final void cacheResult(K key, CacheableResult<V> result) {
        threadPool.submit(() -> {
            Kryo kryo = kryoPool.obtain();

            Output out = new Output(1024, -1);
            kryo.writeClassAndObject(out, result);
            kryoPool.free(kryo);
            out.close();

            try (Input input = new Input(out.getBuffer(), 0, out.position())) {
                byte[] bytes = ByteStreams.toByteArray(input);
                input.close();
                redis.setWithExpiry(this.generateHash(key), result.ttl, Base64.encode(bytes));
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Generates the key to be used in the key,value pair inserted in the cache.
     * @param key The key to hash.
     * @return A hash of the key mixed with any other desired data (e.g. environment)
     */
    protected String generateHash(K key) {
        return (currentEnvironment.getName().hashCode() + getClass().hashCode() + key.hashCode()) + "";
    }


    /**
     * Used by clients to return fetched results.
     *
     * @param <V> The type of the result being returned.
     */
    public static class CacheableResult<V> {

        private static final int defaultCacheTTL = 900;

        private V result;
        private int ttl;
        private OffsetDateTime lastUpdated = OffsetDateTime.now();


        private CacheableResult(V result, int ttl) {
            this.result = result;
            this.ttl = ttl;
        }

        private CacheableResult() {}

        /**
         * The result of the fetch
         */
        public V getResult() {
            return result;
        }

        /**
         * The ttl the key was set with. Note this is NOT the remaining ttl of the key.
         */
        public int getTTL() {
            return ttl;
        }

        public OffsetDateTime getLastUpdated() {
            return lastUpdated;
        }


        /**
         * @param result The result of the query.
         * @param cacheTTL The ttl in seconds.
         */
        public static <V> CacheableResult<V> of(V result, int cacheTTL) {
            return new CacheableResult<>(result, cacheTTL);
        }

        public static <V> CacheableResult<V> of(V result) {
            return new CacheableResult<>(result, defaultCacheTTL);
        }
    }
}
