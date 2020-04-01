package net.dashflight.data.uuid

import java.util.*

interface UUIDGenerator {
    operator fun next(): UUID?
}