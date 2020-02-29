package net.dashflight.data.config;

public interface ConfigurationData<T> {

    T getData();

    Object get(String key);
}
