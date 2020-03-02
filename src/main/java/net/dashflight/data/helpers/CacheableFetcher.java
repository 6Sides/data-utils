package net.dashflight.data.helpers;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.OffsetDateTime;
import net.dashflight.data.redis.RedisClient;
import net.dashflight.data.redis.RedisFactory;

/**
 * Handles caching of results. Should be used for computationally expensive queries.
 *
 * @param <K> The data type required to make the query (The `key`)
 * @param <V> The result type (The `value`). Must be compatible
 *          with Jackson to work properly.
 */
public abstract class CacheableFetcher<K, V> {

    private static final RedisClient redis = RedisFactory.withDefaults();
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

        mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
    }


    private final String keyPrefix;

    public CacheableFetcher(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }


    protected abstract CacheableResult<V> fetchResult(K input) throws Exception;


    public void invalidate(K key) {
        redis.del(this.generateKey(key));
    }

    public long getKeyTTL(K key) {
        return redis.getTTL(this.generateKey(key));
    }



    public CacheableResult<V> get(K key) throws RuntimeException {
        try {
            String blob = redis.get(this.generateKey(key));
            if (blob != null) {
                return mapper.readValue(blob, new TypeReference<CacheableResult<V>>(){});
            }

            CacheableResult<V> result = fetchResult(key);
            this.cacheResult(key, result);

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private void cacheResult(K key, CacheableResult<V> result) throws JsonProcessingException {
        redis.setWithExpiry(this.generateKey(key), result.cacheTTL, mapper.writeValueAsString(result));
    }

    private String generateKey(K key) {
        return keyPrefix + key.hashCode();
    }


    public static class CacheableResult<V> {

        private static final int defaultCacheTTL = 3600;

        private V result;

        @JsonIgnore
        private int cacheTTL;

        private OffsetDateTime lastUpdated = OffsetDateTime.now();


        private CacheableResult(V result, int cacheTTL) {
            this.result = result;
            this.cacheTTL = cacheTTL;
        }

        private CacheableResult() {}


        public V getResult() {
            return result;
        }

        public OffsetDateTime getLastUpdated() {
            return lastUpdated;
        }


        public static <V> CacheableResult<V> of(V result, int ttl) {
            return new CacheableResult<>(result, ttl);
        }

        public static <V> CacheableResult<V> of(V result) {
            return new CacheableResult<>(result, defaultCacheTTL);
        }
    }
}
