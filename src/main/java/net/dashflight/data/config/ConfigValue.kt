package net.dashflight.data.config

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ConfigValue(
        /**
         * The name of the property to associate with the field
         * @return
         */
        val value: String)