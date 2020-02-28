package net.dashflight.data.keys;

import java.util.HashMap;
import java.util.Map;
import net.dashflight.data.RuntimeEnvironment;

/**
 * Factory to create Redis clients. There can only be one instance of the
 * client for each unique set of parameters.
 */
public class RSAKeyManagerFactory {

    private static Map<Integer, DefaultRSAKeyManager> instances = new HashMap<>();


    public static DefaultRSAKeyManager withDefaults() {
        return withConfiguration(RuntimeEnvironment.getCurrentEnvironment(), null);
    }

    public static DefaultRSAKeyManager withEnvironment(RuntimeEnvironment env) {
        return withConfiguration(env,  null);
    }

    public static DefaultRSAKeyManager withConfiguration(RuntimeEnvironment env, Map<String, Object> additionalProperties) {
        Integer hash = env.hashCode() + (additionalProperties != null ? additionalProperties.hashCode() : 0);

        instances.putIfAbsent(hash, new DefaultRSAKeyManager(env, additionalProperties));

        return instances.get(hash);
    }
}
