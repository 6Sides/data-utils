package net.dashflight.data.helpers;

import com.amazonaws.util.Base64;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.dashflight.data.redis.RedisClient;
import net.dashflight.data.redis.RedisFactory;

/**
 * Handles caching of results. Should be used for computationally expensive queries.
 *
 * @param <K> The data type required to make the query (The `key`)
 * @param <V> The result type (The `value`)
 */
public abstract class CacheableFetcher<K, V> {

    private static RedisClient redis = RedisFactory.withDefaults();
    private static final Kryo mapper = new Kryo();

    private static final Map<Class<?>, Integer> versions = new HashMap<>();

    static {
        mapper.register(CacheableResult.class);
        mapper.register(UUID.class, new UUIDSerializer());
    }

    private final String keyPrefix;

    public CacheableFetcher(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    protected void registerClass(Class<?> clazz, int version) {
        if (version <= 10) {
            throw new IllegalArgumentException("Version must be > 10");
        }

        Integer v = versions.get(clazz);
        if (v != null && v != version) {
            throw new IllegalStateException("Class " + clazz + " is already registered!");
        }

        mapper.register(clazz, version);
        versions.put(clazz, version);
    }

    protected abstract CacheableResult<V> fetchResult(K input) throws Exception;


    public void invalidate(K key) {
        redis.del(this.generateHash(key));
    }

    public long getKeyTTL(K key) {
        return redis.getTTL(this.generateHash(key));
    }


    public CacheableResult<V> get(K key) throws RuntimeException {
        CacheableResult<V> result;

        try {
            String blob = redis.get(this.generateHash(key));
            if (blob != null) {
                byte[] bytes = Base64.decode(blob.getBytes());
                try {
                    return CacheableResult.of((V) mapper.readClassAndObject(new Input(new ByteArrayInputStream(bytes))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            result = fetchResult(key);
            this.cacheResult(key, result);

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void cacheResult(K key, CacheableResult<V> result) {
        Output out = new Output(new ByteArrayOutputStream());
        mapper.writeClassAndObject(out, result.getResult());
        out.close();

        Input input = new Input(out.getBuffer());

        byte[] bytes = new byte[1024];
        int counter = 0;
        boolean hitZero = false;
        while (!input.end()) {
            byte b = input.readByte();
            if (hitZero) break;
            if (b == 0) hitZero = true;
            bytes[counter++] = b;
        }

        byte[] res = new byte[counter];
        System.arraycopy(bytes, 0, res, 0, counter);

        redis.setWithExpiry(this.generateHash(key), result.cacheTTL, Base64.encode(res));
    }

    private String generateHash(K key) {
        return keyPrefix + key.hashCode();
    }

    public static class CacheableResult<V> {

        private static final int defaultCacheTTL = 3600;

        private V result;

        @JsonIgnore
        private int cacheTTL;

        //private OffsetDateTime lastUpdated = OffsetDateTime.now();


        private CacheableResult(V result, int cacheTTL) {
            this.result = result;
            this.cacheTTL = cacheTTL;
        }

        private CacheableResult() {}


        public V getResult() {
            return result;
        }

        public int getCacheTTL() {
            return cacheTTL;
        }

        public OffsetDateTime getLastUpdated() {
            return null;//lastUpdated;
        }


        public static <V> CacheableResult<V> of(V result, int ttl) {
            return new CacheableResult<>(result, ttl);
        }

        public static <V> CacheableResult<V> of(V result) {
            return new CacheableResult<>(result, defaultCacheTTL);
        }
    }
}
