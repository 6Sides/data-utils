package net.dashflight.data.random

import java.security.Provider

/**
 * Constructs a provider with the specified name, version number, and information.
 */
internal class LavaRandomProvider : Provider("Lava Random", 1.0, "Generates Random numbers from the lava lamp")