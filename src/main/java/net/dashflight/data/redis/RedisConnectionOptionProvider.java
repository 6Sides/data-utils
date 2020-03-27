package net.dashflight.data.redis;

/**
 * Creates the connection options used by a Redis client
 */
public interface RedisConnectionOptionProvider {

    RedisConnectionOptions get();

}
