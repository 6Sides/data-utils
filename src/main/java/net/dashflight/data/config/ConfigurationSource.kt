package net.dashflight.data.config;

import java.util.Map;

/**
 * Fetches configuration data from a source.
 */
public interface ConfigurationSource {

    ConfigurationData getConfig(String applicationName, RuntimeEnvironment env, Map<String, Object> additionalData);

}
