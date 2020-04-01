package net.dashflight.data.queue

import net.dashflight.data.queue.RedisConsumerNode.RedisConsumerNodeTask

interface RedisConsumerNodeFactory {
    fun create(task: RedisConsumerNodeTask?): RedisConsumerNode?
}