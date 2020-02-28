package net.dashflight.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provides a configure method which allows classes to register with a config file
 * on s3.
 */
public interface Configurable {

    /**
     * Stores properties of fetched configurations to avoid
     * refetching from s3
     */
    Map<Integer, Properties> cache = new HashMap<>();


    default void registerWith(String applicationName) {
        registerWith(applicationName, RuntimeEnvironment.getCurrentEnvironment());
    }

    default void registerWith(String applicationName, RuntimeEnvironment env) {
        registerWith(applicationName, env, null);
    }

    default void registerWith(String applicationName, RuntimeEnvironment env, Map<String, Object> properties) {
        if (applicationName == null || env == null) {
            throw new IllegalArgumentException("Application name and RuntimeEnvironment must both be non-null.");
        }

        int hash = 0;
        hash += applicationName.hashCode();
        hash += env.hashCode();
        hash += properties != null ? properties.hashCode() : 0;

        if (!cache.containsKey(hash)) {
            cache.put(hash, S3ConfigFetcher.getPropertiesForApplication(applicationName, env, properties));
        }

        ValueInjector.inject(this, cache.get(hash));
    }
}
