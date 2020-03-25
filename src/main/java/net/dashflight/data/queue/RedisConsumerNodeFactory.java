package net.dashflight.data.queue;

import net.dashflight.data.queue.RedisConsumerNode.RedisConsumerNodeTask;

public interface RedisConsumerNodeFactory {

    RedisConsumerNode create(RedisConsumerNodeTask task);

}
