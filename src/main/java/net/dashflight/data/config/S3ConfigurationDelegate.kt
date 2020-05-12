package net.dashflight.data.config

import java.util.*
import kotlin.reflect.KProperty

class S3ConfigurationDelegate(
        application: String,
        private val property: String,
        runtimeEnvironment: RuntimeEnvironment = RuntimeEnvironment.currentEnvironment
) {

    private val data: ConfigurationData<Properties> = cache.computeIfAbsent(application) {
        configFetcher.getConfig(application, runtimeEnvironment, null)
    }

    operator fun <T> getValue(thisRef: Any?, property: KProperty<*>): T {
        val fieldType = property.returnType.classifier
        val configValue = data[this.property] as String?

        return if (fieldType == Int::class || fieldType == Int::class.javaPrimitiveType) {
            configValue?.toInt() as T
        } else if (fieldType == Double::class || fieldType == Double::class.javaPrimitiveType) {
            configValue?.toDouble() as T
        } else if (fieldType == Float::class || fieldType == Float::class.javaPrimitiveType) {
            configValue?.toFloat() as T
        } else if (fieldType == Short::class || fieldType == Short::class.javaPrimitiveType) {
            configValue?.toShort() as T
        } else if (fieldType == Boolean::class || fieldType == Boolean::class.javaPrimitiveType) {
            java.lang.Boolean.parseBoolean(configValue) as T
        } else {
            configValue as T
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        error("Cannot set the value of a configuration value!")
    }

    companion object {
        private val configFetcher = S3ConfigFetcher()

        private val cache = mutableMapOf<String, ConfigurationData<Properties>>()
    }

}