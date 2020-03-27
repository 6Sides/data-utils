package net.dashflight.data.redis;

import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;

/**
 * Pulls the required redis connection options from s3
 */
public class DashflightRedisConnectionOptionProvider implements RedisConnectionOptionProvider, Configurable {

    private static final String APP_NAME = "redis";


    @ConfigValue("redis_host")
    private String host;

    @ConfigValue("redis_port")
    private int port;

    @ConfigValue("max_pool_size")
    private int maxPoolSize = 4;


    public DashflightRedisConnectionOptionProvider() {
        registerWith(APP_NAME);
    }


    @Override
    public RedisConnectionOptions get() {
        return RedisConnectionOptions.builder()
                .host(host)
                .port(port)
                .maxPoolSize(maxPoolSize)
                .build();
    }
}
