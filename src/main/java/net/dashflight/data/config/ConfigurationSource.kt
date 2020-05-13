package net.dashflight.data.config

/**
 * Fetches configuration data from a source.
 */
interface ConfigurationSource {

    fun getConfig(applicationName: String?, env: RuntimeEnvironment?, additionalData: Map<String?, Any?>?): ConfigurationData<*>

}