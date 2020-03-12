package net.dashflight.data.helpers;

import com.amazonaws.util.Base64;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;
import net.dashflight.data.config.RuntimeEnvironment;
import net.dashflight.data.helpers.Computable.DataFetchException;
import net.dashflight.data.redis.RedisClient;
import net.dashflight.data.redis.RedisFactory;

/**
 * Handles caching of results. Should be used for computationally expensive queries.
 *
 * This class automatically handles object serialization/deserialization at the expense
 * of slightly larger serialization data.
 *
 * @param <K> The data type required to make the query (The `key`)
 * @param <V> The result type (The `value`)
 */
public abstract class CacheableFetcher<K, V> {

    // TODO: Kryo is not thread safe, refactor to use a pool
    protected static final Kryo mapper = new Kryo();

    private static final RedisClient redis = RedisFactory.withDefaults();
    private static final RuntimeEnvironment currentEnvironment = RuntimeEnvironment.getCurrentEnvironment();


    static {
        mapper.register(CacheableResult.class);
        mapper.register(OffsetDateTime.class);
        mapper.register(UUID.class, new UUIDSerializer());
    }


    /** Prefix for each key in the cache */
    private final String keyPrefix = getClass().hashCode() + "";

    private final Memoizer<K, CacheableResult<V>> memoizer;

    protected CacheableFetcher() {
        this.memoizer = new Memoizer<>(key -> this.memoizedGet(key));
    }


    /**
     * Registers a class with an id based on its hashcode. 12 is added to ensure
     * there is no version clash with Kryo's default registered classes.
     */
    protected <T> void registerClass(Class<T> clazz) {
        mapper.register(clazz, Math.abs(clazz.hashCode()) + 12);
    }

    protected <T> void registerClass(Class<T> clazz, Serializer<T> serializer) {
        mapper.register(clazz, serializer, Math.abs(clazz.hashCode()) + 12);
    }


    /**
     * Fetches and returns the result based on the input
     *
     * @throws DataFetchException If a result is unable to be obtained
     */
    protected abstract CacheableResult<V> fetchResult(K input) throws DataFetchException;


    /**
     * Invalidates the result associated with the specified object
     */
    public void invalidate(K key) {
        redis.del(this.generateHash(key));
    }

    /**
     * Returns the remaining ttl of the key,value pair associated with the specified object
     */
    public long getKeyTTL(K key) {
        return redis.getTTL(this.generateHash(key));
    }


    /**
     * Fetches data based on the specified key. Attempts to retrieve the result from the cache first.
     * If no value is found the result is refetched, cached, and then returned.
     *
     * @param key The data being used to make the query
     * @return The value associated with the specified key (can be null)
     *
     * @throws DataFetchException when a result couldn't be obtained
     */
    public CacheableResult<V> get(K key) throws DataFetchException {
        try {
            return this.memoizer.compute(key);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw new DataFetchException(ex.getMessage());
        }
    }

    private CacheableResult<V> memoizedGet(K key) throws DataFetchException {
        String blob = redis.get(this.generateHash(key));

        // Key exists, attempt to deserialize and return it's associated value.
        if (blob != null) {
            try {
                byte[] bytes = Base64.decode(blob.getBytes());
                return (CacheableResult<V>) mapper
                        .readClassAndObject(new Input(new ByteArrayInputStream(bytes)));
            } catch (Exception e) {
                // Key is in invalid format, delete it.
                redis.del(this.generateHash(key));
                e.printStackTrace();
            }
        }

        // Refetch result and cache it
        CacheableResult<V> result = fetchResult(key);
        this.cacheResult(key, result);

        return result;
    }


        /**
         * Serializes and caches fetched result.
         * @param key The key to store the result at.
         * @param result The object to serialize and store.
         */
    protected void cacheResult(K key, CacheableResult<V> result) {
        Output out = new Output(1024, -1);
        mapper.writeClassAndObject(out, result);
        out.close();

        try (Input input = new Input(out.getBuffer(), 0, out.position())) {
            byte[] bytes = ByteStreams.toByteArray(input);
            input.close();
            redis.setWithExpiry(this.generateHash(key), result.ttl, Base64.encode(bytes));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Generates the key to be used in the key,value pair inserted in the cache.
     * @param key The key to hash.
     * @return A hash of the key mixed with any other desired data (e.g. environment)
     */
    private String generateHash(K key) {
        return currentEnvironment.hashCode() + keyPrefix + key.hashCode();
    }


    /**
     * Used by clients to return fetched results.
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
