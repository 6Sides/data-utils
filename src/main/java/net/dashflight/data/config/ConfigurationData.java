package net.dashflight.data.config;

/**
 * Holds configuration data.
 */
public interface ConfigurationData<T> {

    T getData();

    Object get(String key);

}
