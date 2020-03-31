package net.dashflight.data.config

import java.util.*

/**
 * Provides a configure method which allows classes to register with a configuration source.
 * Default configuration source is s3.
 */
interface Configurable {
    fun registerWith(applicationName: String?) {
        registerWith(
                RegistrationOptions.builder()
                        .applicationName(applicationName)
                        .environment(RuntimeEnvironment.currentEnvironment)
                        .additionalProperties(null)
                        .configurationSource(S3ConfigFetcher())
                        .build()
        )
    }

    fun registerWith(options: RegistrationOptions) {
        require(!(options.applicationName == null || options.environment == null)) { "Application name and RuntimeEnvironment must both be non-null." }
        var hash = 0
        hash += options.applicationName.hashCode()
        hash += options.environment.hashCode()
        hash += if (options.additionalProperties != null) options.additionalProperties.hashCode() else 0
        if (!cache.containsKey(hash)) {
            cache[hash] = options.configurationSource.getConfig(options.applicationName, options.environment, options.additionalProperties)
        }
        ValueInjector.inject(this, cache[hash])
    }

    class RegistrationOptions(val applicationName: String?,
                              val environment: RuntimeEnvironment?,
                              val additionalProperties: Map<String?, Any?>?,
                              val configurationSource: ConfigurationSource) {

        class Builder {
            private var applicationName: String? = null
            private var environment: RuntimeEnvironment? = null
            private var additionalProperties: Map<String?, Any?>? = null
            var configurationSource: ConfigurationSource? = null
            fun applicationName(applicationName: String?): Builder {
                this.applicationName = applicationName
                return this
            }

            fun environment(environment: RuntimeEnvironment?): Builder {
                this.environment = environment
                return this
            }

            fun additionalProperties(
                    additionalProperties: Map<String?, Any?>?): Builder {
                this.additionalProperties = additionalProperties
                return this
            }

            fun configurationSource(configurationSource: ConfigurationSource?): Builder {
                this.configurationSource = configurationSource
                return this
            }

            fun build(): RegistrationOptions {
                assert(applicationName != null)
                if (environment == null) {
                    environment = RuntimeEnvironment.currentEnvironment
                }
                if (configurationSource == null) {
                    configurationSource = S3ConfigFetcher()
                }
                return RegistrationOptions(applicationName, environment, additionalProperties, configurationSource!!)
            }
        }

        companion object {
            fun builder(): Builder {
                return Builder()
            }
        }

    }

    companion object {
        /**
         * Stores properties of fetched configurations to avoid
         * refetching from configuration source
         */
        val cache: MutableMap<Int, ConfigurationData<*>?> = HashMap()
    }
}