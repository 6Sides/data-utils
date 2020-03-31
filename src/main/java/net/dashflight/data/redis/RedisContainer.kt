package net.dashflight.data.redis

import org.testcontainers.containers.GenericContainer

class RedisContainer : GenericContainer<RedisContainer>("redis:5.0.3-alpine") {

    init {
        withExposedPorts(6379)
    }

}