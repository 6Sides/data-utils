package net.dashflight.data.helpers;

import com.amazonaws.util.Base64;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import net.dashflight.data.redis.RedisClient;
import net.dashflight.data.redis.RedisFactory;

/**
 * Handles caching of results. Should be used for computationally expensive queries.
 *
 * @param <K> The data type required to make the query (The `key`)
 * @param <V> The result type (The `value`)
 */
public abstract class CacheableLookup<K, V> {

    private static RedisClient redis = RedisFactory.withDefaults();
    private static final Kryo mapper = new Kryo();

    private static final Map<Class<?>, Integer> versions = new HashMap<>();

    static {
        mapper.register(CacheableLookupResult.class);
    }

    private final String keyPrefix;

    public CacheableLookup(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    protected <T extends V> void registerClass(Class<T> clazz, int version) {
        if (version <= 8) {
            throw new IllegalArgumentException("Version must be > 8");
        }

        Integer v = versions.get(clazz);
        if (v != null && v != version) {
            throw new IllegalStateException("Class " + clazz + " is already registered!");
        }

        mapper.register(clazz, version);
        versions.put(clazz, version);
    }

    protected abstract CacheableLookupResult<V> fetchResult(K input) throws Exception;


    public void invalidate(K key) {
        redis.del(this.generateHash(key));
    }

    public long getKeyTTL(K key) {
        return redis.getTTL(this.generateHash(key));
    }


    public CacheableLookupResult<V> get(K key) throws RuntimeException {
        CacheableLookupResult<V> result;

        try {
            String blob = redis.get(this.generateHash(key));
            if (blob != null) {
                byte[] bytes = Base64.decode(blob.getBytes());
                try {
                    return mapper.readObject(new Input(new ByteArrayInputStream(bytes)),
                            CacheableLookupResult.class);
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

    private void cacheResult(K key, CacheableLookupResult<V> result) {
        Output out = new Output(new ByteArrayOutputStream());
        mapper.writeObject(out, result);
        out.close();

        Input input = new Input(out.getBuffer());

        byte[] bytes = new byte[4096];
        int counter = 0;
        while (!input.end()) {
            byte b = input.readByte();
            if (b == 0) break;
            bytes[counter++] = b;
        }

        byte[] res = new byte[counter];
        System.arraycopy(bytes, 0, res, 0, counter);

        redis.setWithExpiry(this.generateHash(key), result.cacheTTL, Base64.encode(res));
    }

    private String generateHash(K key) {
        return keyPrefix + key.hashCode();
    }

    public static class CacheableLookupResult<V> {

        private static final int defaultCacheTTL = 3600;

        private V result;

        @JsonIgnore
        private int cacheTTL;

        //private OffsetDateTime lastUpdated = OffsetDateTime.now();


        private CacheableLookupResult(V result, int cacheTTL) {
            this.result = result;
            this.cacheTTL = cacheTTL;
        }

        private CacheableLookupResult() {}


        public V getResult() {
            return result;
        }

        public int getCacheTTL() {
            return cacheTTL;
        }

        public OffsetDateTime getLastUpdated() {
            return null;//lastUpdated;
        }


        public static <V> CacheableLookupResult<V> of(V result, int ttl) {
            return new CacheableLookupResult<>(result, ttl);
        }

        public static <V> CacheableLookupResult<V> of(V result) {
            return new CacheableLookupResult<>(result, defaultCacheTTL);
        }
    }
}
