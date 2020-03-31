package net.dashflight.data.random

import java.security.SecureRandom

class LavaRandom : SecureRandom(LavaRandomSpi(), LavaRandomProvider())