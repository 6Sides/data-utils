package net.dashflight.data.uuid

import com.fasterxml.uuid.Generators
import java.util.*

class TimeBasedUUIDGenerator internal constructor() : UUIDGenerator {
    override fun next(): UUID? {
        return gen.generate()
    }

    companion object {
        private val gen = Generators.timeBasedGenerator()
    }
}