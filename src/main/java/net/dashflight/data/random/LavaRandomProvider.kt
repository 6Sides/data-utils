package net.dashflight.data.random;

import java.security.Provider;

class LavaRandomProvider extends Provider {

    /**
     * Constructs a provider with the specified name, version number, and information.
     */
    public LavaRandomProvider() {
        super("Lava Random", 1.0, "Generates Random numbers from the lava lamp");
    }
}
