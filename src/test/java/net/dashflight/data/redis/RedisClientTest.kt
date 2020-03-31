package net.dashflight.data.redis

import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import redis.clients.jedis.Jedis

class RedisClientTest {

    @get:Rule
    var redisContainer = RedisContainer()
    lateinit var redis: Jedis

    @Before
    fun setup() {
        val address = redisContainer.containerIpAddress
        val port = redisContainer.firstMappedPort
        redis = Jedis(address, port)
    }

    @Test
    fun testSetAndGet() {
        val key = "testKey"
        val value = "testValue"
        redis[key] = value
        Assert.assertEquals(value, redis[key])
    }
}