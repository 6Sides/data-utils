package net.dashflight.data.config

import java.util.*

class PropertiesData(override val data: Properties) : ConfigurationData<Properties> {

    override fun get(key: String): Any? {
        return data[key]
    }

}