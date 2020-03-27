package net.dashflight.data.redis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import redis.clients.jedis.Jedis;

public class RedisClientTest {

    @Rule
    public GenericContainer redisContainer = new GenericContainer<>("redis:5.0.3-alpine").withExposedPorts(6379);

    Jedis redis;

    @Before
    public void setup() {
        String address = redisContainer.getContainerIpAddress();
        Integer port = redisContainer.getFirstMappedPort();

        redis = new Jedis(address, port);
    }

    @Test
    public void testSetAndGet() {
        String key = "testKey", value = "testValue";

        redis.set(key, value);

        Assert.assertEquals(value, redis.get(key));
    }


}