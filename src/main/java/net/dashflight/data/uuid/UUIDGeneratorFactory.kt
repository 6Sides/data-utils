package net.dashflight.data.uuid

import java.util.*

object UUIDGeneratorFactory {
    private val instances: MutableMap<Class<*>, UUIDGenerator> = HashMap()
    val default: UUIDGenerator?
        get() = getInstance(TimeBasedUUIDGenerator::class.java)

    fun <T : UUIDGenerator?> getInstance(clazz: Class<T>): UUIDGenerator? {
        if (!instances.containsKey(clazz)) {
            if (clazz == TimeBasedUUIDGenerator::class.java) {
                instances[clazz] = TimeBasedUUIDGenerator()
            }
        }
        require(instances.containsKey(clazz)) { String.format("There is no generator associated with the class `%s`", clazz) }
        return instances[clazz]
    }
}