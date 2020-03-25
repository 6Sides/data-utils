package net.dashflight.data.queue;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import net.dashflight.data.redis.RedisClient;
import redis.clients.jedis.Jedis;

/**
 * Consumes tasks from a queue with Redis. Does NOT scale to more than one consumer.
 */
public class RedisConsumerNode {

    private final RedisClient redis;
    private final RedisConsumerNodeTask task;

    @Inject
    public RedisConsumerNode(RedisClient redisClient, @Assisted RedisConsumerNodeTask task) {
        this.redis = redisClient;
        this.task = task;
    }


    public void start() {
        Thread t = new Thread(() -> {
            while(true) {
                try (Jedis client = redis.getPool().getResource()) {
                    // Get next task in queue and move it to processing list
                    String data = client.brpoplpush("queue", "processing", 0);

                    try {
                        this.task.execute(data);

                        // Remove task from processing queue once completed
                        client.lrem("processing", -1, data);
                    } catch (Exception e) {
                        // TODO: Implement procedure to requeue tasks that failed to be processed
                        System.err.println("Failed to process: " + data);
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    public interface RedisConsumerNodeTask {

        void execute(String data) throws Exception;

    }
}
