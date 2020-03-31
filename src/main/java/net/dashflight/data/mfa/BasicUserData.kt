package net.dashflight.data.mfa

import java.util.*


data class BasicUserData(var userId: UUID? = null, var userSecret: String? = null, var email: String? = null) {
    /**
     * The user's unique id
     */

    /**
     * The unique secret associated with the user
     */

    /**
     * The email address of the user
     */
}