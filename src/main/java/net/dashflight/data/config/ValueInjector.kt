package net.dashflight.data.config

object ValueInjector {
    fun inject(source: Any, props: ConfigurationData<*>?) {
        val clazz = if (source.javaClass == Class::class.java) source as Class<*> else source.javaClass
        try {
            for (field in clazz.declaredFields) {
                if (field.isAnnotationPresent(ConfigValue::class.java)) {
                    val key: String = field.getAnnotation(ConfigValue::class.java).value
                    field.isAccessible = true
                    val fieldType = field.type
                    var value: Any
                    val configValue = props!![key] as String ?: continue
                    try {
                        value = if (fieldType == Int::class.java || fieldType == Int::class.javaPrimitiveType) {
                            configValue.toInt()
                        } else if (fieldType == Double::class.java || fieldType == Double::class.javaPrimitiveType) {
                            configValue.toDouble()
                        } else if (fieldType == Float::class.java || fieldType == Float::class.javaPrimitiveType) {
                            configValue.toFloat()
                        } else if (fieldType == Short::class.java || fieldType == Short::class.javaPrimitiveType) {
                            configValue.toShort()
                        } else if (fieldType == Boolean::class.java || fieldType == Boolean::class.javaPrimitiveType) {
                            java.lang.Boolean.parseBoolean(configValue)
                        } else {
                            configValue
                        }
                        field[source] = value
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}