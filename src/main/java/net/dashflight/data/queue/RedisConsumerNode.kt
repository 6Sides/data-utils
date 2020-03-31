package net.dashflight.data.queue

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import net.dashflight.data.redis.RedisClient

/**
 * Consumes tasks from a queue with Redis. Does NOT scale to more than one consumer.
 */
class RedisConsumerNode @Inject internal constructor(private val redis: RedisClient, @param:Assisted private val task: RedisConsumerNodeTask) {
    fun start() {
        val t = Thread(Runnable {
            while (true) {
                redis.pool.resource.use { client ->
                    // Get next task in queue and move it to processing list
                    val data = client.brpoplpush("queue", "processing", 0)
                    try {
                        task.execute(data)

                        // Remove task from processing queue once completed
                        client.lrem("processing", -1, data)
                    } catch (e: Exception) {
                        // TODO: Implement procedure to requeue tasks that failed to be processed
                        System.err.println("Failed to process: $data")
                        e.printStackTrace()
                    }
                }
            }
        })
        t.start()
    }

    interface RedisConsumerNodeTask {
        @Throws(Exception::class)
        fun execute(data: String?)
    }

}