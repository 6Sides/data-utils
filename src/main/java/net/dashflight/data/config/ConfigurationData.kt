package net.dashflight.data.config

/**
 * Holds configuration data.
 */
interface ConfigurationData<T> {
    val data: T
    operator fun get(key: String): Any?
}