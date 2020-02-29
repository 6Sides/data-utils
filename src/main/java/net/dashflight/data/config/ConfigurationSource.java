package net.dashflight.data.config;

import java.util.Map;

/**
 * @param <T> The java type to parse the config file to.
 */
public interface ConfigurationSource<T> {

    ConfigurationData<T> getConfig(String applicationName, RuntimeEnvironment env, Map<String, Object> additionalData);

}
