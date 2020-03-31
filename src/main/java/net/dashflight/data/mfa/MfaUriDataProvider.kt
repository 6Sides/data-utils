package net.dashflight.data.mfa

import java.util.*

interface MfaUriDataProvider {
    /**
     * Returns a map of data necessary to build the authenticator URI
     * based on the specified userId. Use this to embed a user's email, username, etc.
     * in the URI.
     */
    fun getData(userId: UUID): BasicUserData
}