package net.dashflight.data

import net.dashflight.data.jwt.DashflightJwtUtilModule
import net.dashflight.data.keys.DashflightRSAKeyPairModule
import net.dashflight.data.mfa.DashflightMfaModule
import net.dashflight.data.postgres.DashflightPostgresClientModule
import net.dashflight.data.queue.DashflightRedisQueueModule
import net.dashflight.data.redis.DashflightRedisClientModule

/**
 * Convenience class to obtain all dashflight related Guice modules
 */
object DashflightModules {

    val allModules = listOf(
            DashflightJwtUtilModule(),
            DashflightPostgresClientModule(),
            DashflightRSAKeyPairModule(),
            DashflightMfaModule(),
            DashflightRedisClientModule(),
            DashflightRedisQueueModule()
    )
}