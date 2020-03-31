package net.dashflight.data.redis

/**
 * Holds connection data for connecting to Redis instance
 */
data class RedisConnectionOptions(val host: String, val port: Int, val maxPoolSize: Int = 4) {
    /**
     * The host address of the redis instance
     */

    /**
     * The port of the redis instance
     */

    /**
     * The maximum size of the connection pool
     */
}