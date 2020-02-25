package net.dashflight.data.redis;

import config.parser.ConfigValue;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Handles interfacing with Redis cache. All methods in this class
 * interface with database 0 by default.
 */
public class BasicRedisCache {

    @ConfigValue("redis_host")
    private static String host;

    @ConfigValue("redis_port")
    private static int port;

    /**
     * Redis connection pool
     */
    private static JedisPool pool;

    /**
     * The database to interface with in redis
     */
    protected int database = 0;


    protected BasicRedisCache() {
        if (pool == null) {
            pool = new JedisPool(host, port);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> pool.close()));
        }
    }

    /**
     * Sets a key, value pair in the cache
     */
    public boolean set(String key, String value) {
        try (Jedis client = pool.getResource()) {
            client.select(this.database);
            return client.set(key, value).equals("OK");
        }
    }

    /**
     * Sets a key, value pair in the cache with a specified expiry length (in seconds).
     */
    public boolean setWithExpiry(String key, int seconds, String value) {
        try (Jedis client = pool.getResource()) {
            client.select(this.database);
            return client.setex(key, seconds, value).equals("OK");
        }
    }

    /**
     * Checks if a key exists in the cache
     */
    public boolean has(String key) {
        try (Jedis client = pool.getResource()) {
            client.select(this.database);
            return client.exists(key);
        }
    }

    /**
     * Query for a value with a key
     */
    public String get(String key) {
        try (Jedis client = pool.getResource()) {
            client.select(this.database);
            return client.get(key);
        }
    }

    /**
     * Attempts to delete a key from the cache.
     * `this.client.del` returns number of keys removed.
     */
    public boolean del(String key) {
        try (Jedis client = pool.getResource()) {
            client.select(this.database);
            return client.del(key) > 0;
        }
    }

}
