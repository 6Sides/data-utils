package net.dashflight.data.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Handles interfacing with Redis cache. All methods in this class
 * interface with database 0.
 */
@Singleton
public class RedisClient {

    /**
     * Redis connection pool
     */
    protected JedisPool pool;


    @Inject
    RedisClient(RedisConnectionOptionProvider optionProvider) {
        RedisConnectionOptions options = optionProvider.get();

        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(options.getMaxPoolSize());

        pool = new JedisPool(
                config,
                options.getHost(),
                options.getPort()
        );

        // Close pool on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> pool.close()));
    }

    public JedisPool getPool() {
        return this.pool;
    }

    /**
     * Sets a key, value pair in the cache
     */
    public boolean set(String key, String value) {
        try (Jedis client = pool.getResource()) {
            return client.set(key, value).equals("OK");
        }
    }

    /**
     * Sets a key, value pair in the cache with a specified expiry length (in seconds).
     */
    public boolean setWithExpiry(String key, int seconds, String value) {
        try (Jedis client = pool.getResource()) {
            return client.setex(key, seconds, value).equals("OK");
        }
    }

    public boolean setWithExpiry(String key, int seconds, byte[] value) {
        try (Jedis client = pool.getResource()) {
            return client.setex(key.getBytes(), seconds, value).equals("OK");
        }
    }

    /**
     * Checks if a key exists in the cache
     */
    public boolean has(String key) {
        try (Jedis client = pool.getResource()) {
            return client.exists(key);
        }
    }

    /**
     * Query for a value with a key
     */
    public String get(String key) {
        try (Jedis client = pool.getResource()) {
            return client.get(key);
        }
    }

    /**
     * Attempts to delete a key from the cache.
     * `this.client.del` returns number of keys removed.
     */
    public boolean del(String key) {
        try (Jedis client = pool.getResource()) {
            return client.del(key) > 0;
        }
    }

    public long getTTL(String key) {
        try (Jedis client = pool.getResource()) {
            return client.ttl(key);
        }
    }

    /**
     * Adds the members to the set at key `key`
     */
    public long sadd(String key, String... members) {
        try (Jedis client = pool.getResource()) {
            return client.sadd(key, members);
        }
    }

    public boolean sismember(String key, String member) {
        try (Jedis client = pool.getResource()) {
            return client.sismember(key, member);
        }
    }

    public boolean setKeyExpire(String key, int seconds) {
        try (Jedis client = pool.getResource()) {
            return client.expire(key, seconds) == 1;
        }
    }
}
