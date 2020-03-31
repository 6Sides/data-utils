package net.dashflight.data.mfa

import com.google.inject.Inject
import java.util.*

/**
 * Basic implementation of an mfa service.
 */
class BasicMfaService @Inject constructor(private val mfaDataProvider: MfaDataProvider, private val uriDataProvider: MfaUriDataProvider) : MfaService {

    /**
     * Returns user's temporary password
     */
    override fun getTOTPCode(userId: UUID): String? {
        return mfaDataProvider.getTOTPCode(userId)
    }

    /**
     * Returns user's secret
     */
    override fun getUserSecret(userId: UUID): String? {
        return mfaDataProvider.getUserSecret(userId)
    }

    /**
     * Returns URI associated with user
     */
    override fun getAuthenticatorURI(userId: UUID): String? {
        val data = uriDataProvider.getData(userId)
        data.userId = userId
        data.userSecret = getUserSecret(userId)
        return mfaDataProvider.getAuthenticatorURI(data)
    }

    override fun generateOneTimePassword(): String? {
        return mfaDataProvider.generateOneTimePassword()
    }

}