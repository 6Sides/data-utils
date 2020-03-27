package net.dashflight.data.redis;

import lombok.Builder;
import lombok.Data;

/**
 * Holds connection data for connecting to Redis instance
 */
@Data
@Builder
public class RedisConnectionOptions {

    /**
     * The host address of the redis instance
     */
    private String host;

    /**
     * The port of the redis instance
     */
    private int port;

    /**
     * The maximum size of the connection pool
     */
    @Builder.Default
    private int maxPoolSize = 4;

}
