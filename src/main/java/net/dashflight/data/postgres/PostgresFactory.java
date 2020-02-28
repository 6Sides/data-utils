package net.dashflight.data.postgres;

import java.util.HashMap;
import java.util.Map;
import net.dashflight.data.RuntimeEnvironment;

/**
 * Factory to create Postgres clients. There can only be one instance of the
 * client for each unique set of parameters.
 */
public class PostgresFactory {

    private static Map<Integer, PostgresClient> instances = new HashMap<>();


    public static PostgresClient withDefaults() {
        return withConfiguration(RuntimeEnvironment.getCurrentEnvironment(), null);
    }

    public static PostgresClient withEnvironment(RuntimeEnvironment env) {
        return withConfiguration(env,  null);
    }

    public static PostgresClient withConfiguration(RuntimeEnvironment env, Map<String, Object> additionalProperties) {
        Integer hash = env.hashCode() + (additionalProperties != null ? additionalProperties.hashCode() : 0);

        if (!instances.containsKey(hash)) {
            instances.put(hash, new PostgresClient(env, additionalProperties));
        }

        return instances.get(hash);
    }
}
