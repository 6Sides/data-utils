package net.dashflight.data.redis;

import java.util.HashMap;
import java.util.Map;
import net.dashflight.data.RuntimeEnvironment;

/**
 * Factory to create Redis clients. There can only be one instance of the
 * client for each unique set of parameters.
 */
public class RedisFactory {

    private static Map<Integer, RedisClient> instances = new HashMap<>();


    public static RedisClient withDefaults() {
        return withConfiguration(RuntimeEnvironment.getCurrentEnvironment(), null);
    }

    public static RedisClient withEnvironment(RuntimeEnvironment env) {
        return withConfiguration(env,  null);
    }

    public static RedisClient withConfiguration(RuntimeEnvironment env, Map<String, Object> additionalProperties) {
        Integer hash = env.hashCode() + (additionalProperties != null ? additionalProperties.hashCode() : 0);

        if (!instances.containsKey(hash)) {
            instances.put(hash, new RedisClient(env, additionalProperties));
        }

        return instances.get(hash);
    }
}
