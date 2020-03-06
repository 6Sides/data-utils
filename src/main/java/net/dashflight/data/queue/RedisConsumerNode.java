package net.dashflight.data.queue;

import net.dashflight.data.redis.RedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Consumes tasks from a queue with Redis. Does NOT scale to more than one consumer.
 */
public class RedisConsumerNode {

    private static final JedisPool redisPool = RedisFactory.withDefaults().getPool();

    private RedisConsumerNodeTask task;

    private static RedisConsumerNode instance;

    public static RedisConsumerNode getInstance(RedisConsumerNodeTask task) {
        if (instance == null || !instance.task.equals(task)) {
            instance = new RedisConsumerNode(task);
        }

        return instance;
    }

    private RedisConsumerNode(RedisConsumerNodeTask task) {
        this.task = task;
    }

    public void start() {
        Thread t = new Thread(() -> {
            while(true) {
                try (Jedis client = redisPool.getResource()) {
                    // Get next task in queue and move it to processing list
                    String data = client.brpoplpush("queue", "processing", 0);

                    try {
                        this.task.execute(data);

                        // Remove task from processing queue once completed
                        client.lrem("processing", -1, data);
                    } catch (Exception e) {
                        // TODO: Implement procedure to requeue tasks that failed to be processed
                        System.out.println("Failed to process: " + data);
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
