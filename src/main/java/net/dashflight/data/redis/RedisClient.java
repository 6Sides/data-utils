package net.dashflight.data.redis;

import java.util.Map;
import net.dashflight.data.ConfigValue;
import net.dashflight.data.Configurable;
import net.dashflight.data.RuntimeEnvironment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Handles interfacing with Redis cache. All methods in this class
 * interface with database 0 by default.
 */
public class RedisClient implements Configurable {

    private static final String APP_NAME = "redis";


    @ConfigValue("redis_host")
    private String host;

    @ConfigValue("redis_port")
    private int port;


    /**
     * Redis connection pool
     */
    protected JedisPool pool;

    RedisClient(RuntimeEnvironment env, Map<String, Object> properties) {
        registerWith(APP_NAME, env, properties);

        pool = new JedisPool(host, port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> pool.close()));
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
