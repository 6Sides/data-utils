package net.dashflight.data.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a configure method which allows classes to register with a config file
 * on s3.
 */
public interface Configurable {

    /**
     * Stores properties of fetched configurations to avoid
     * refetching from configuration source
     */
    Map<Integer, ConfigurationData<?>> cache = new HashMap<>();


    default void registerWith(String applicationName) {
        registerWith(
                RegistrationOptions.builder()
                        .applicationName(applicationName)
                        .environment(RuntimeEnvironment.getCurrentEnvironment())
                        .additionalProperties(null)
                        .configurationSource(new S3ConfigFetcher())
                        .build()
        );
    }

    default void registerWith(RegistrationOptions options) {
        if (options.applicationName == null || options.environment == null) {
            throw new IllegalArgumentException("Application name and RuntimeEnvironment must both be non-null.");
        }

        int hash = 0;
        hash += options.applicationName.hashCode();
        hash += options.environment.hashCode();
        hash += options.additionalProperties != null ? options.additionalProperties.hashCode() : 0;

        if (!cache.containsKey(hash)) {
            cache.put(hash, options.configurationSource.getConfig(options.applicationName, options.environment, options.additionalProperties));
        }

        ValueInjector.inject(this, cache.get(hash));
    }


    class RegistrationOptions {
        private String applicationName;
        private RuntimeEnvironment environment;
        private Map<String, Object> additionalProperties;
        private ConfigurationSource<?> configurationSource;


        public RegistrationOptions(String applicationName,
                RuntimeEnvironment environment,
                Map<String, Object> additionalProperties,
                ConfigurationSource<?> configurationSource) {
            this.applicationName = applicationName;
            this.environment = environment;
            this.additionalProperties = additionalProperties;
            this.configurationSource = configurationSource;
        }

        public String getApplicationName() {
            return applicationName;
        }

        public RuntimeEnvironment getEnvironment() {
            return environment;
        }

        public Map<String, Object> getAdditionalProperties() {
            return additionalProperties;
        }

        public ConfigurationSource<?> getConfigurationSource() {
            return configurationSource;
        }

        public static Builder builder() {
            return new Builder();
        }


        public static class Builder {
            private String applicationName;
            private RuntimeEnvironment environment;
            private Map<String, Object> additionalProperties;
            ConfigurationSource<?> configurationSource;

            public Builder applicationName(String applicationName) {
                this.applicationName = applicationName;
                return this;
            }

            public Builder environment(RuntimeEnvironment environment) {
                this.environment = environment;
                return this;
            }

            public Builder additionalProperties(
                    Map<String, Object> additionalProperties) {
                this.additionalProperties = additionalProperties;
                return this;
            }

            public Builder configurationSource(ConfigurationSource<?> configurationSource) {
                this.configurationSource = configurationSource;
                return this;
            }

            public RegistrationOptions build() {
                assert (applicationName != null);

                if (environment == null) {
                    environment = RuntimeEnvironment.getCurrentEnvironment();
                }
                if (configurationSource == null) {
                    configurationSource = new S3ConfigFetcher();
                }

                return new RegistrationOptions(applicationName, environment, additionalProperties, configurationSource);
            }
        }
    }
}
