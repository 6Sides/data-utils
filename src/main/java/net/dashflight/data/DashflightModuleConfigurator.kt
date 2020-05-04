package net.dashflight.data

import com.google.inject.AbstractModule
import com.google.inject.Guice
import net.dashflight.data.jwt.DashflightJwtUtilModule
import net.dashflight.data.keys.DashflightRSAKeyPairModule
import net.dashflight.data.mfa.DashflightMfaModule
import net.dashflight.data.postgres.DashflightPostgresClientModule
import net.dashflight.data.queue.DashflightRedisQueueModule
import net.dashflight.data.redis.DashflightRedisClientModule
import kotlin.reflect.KClass

/**
 * Convenience class to obtain all dashflight related Guice modules
 */
class DashflightModuleConfigurator(
    f: (DashflightModuleConfigurator.() -> Unit)? = null
) {

    private val baseModules = mutableSetOf(
            DashflightJwtUtilModule(),
            DashflightPostgresClientModule(),
            DashflightRSAKeyPairModule(),
            DashflightMfaModule(),
            DashflightRedisClientModule(),
            DashflightRedisQueueModule()
    )

    init {
        f?.invoke(this)
    }

    fun override(module: KClass<out AbstractModule>, with: AbstractModule) {
        val itr = baseModules.iterator()
        var found = false

        while (itr.hasNext()) {
            val next = itr.next()

            if (module == next::class) {
                itr.remove()
                found = true
                break
            }
        }

        if (found) {
            baseModules.add(with)
        }
    }

    fun addModule(module: AbstractModule) = baseModules.add(module)

    fun createInjector() = Guice.createInjector(baseModules)

}