package net.dashflight.data.mfa;

import java.util.UUID;

public interface MFA_URIDataProvider {

    /**
     * Returns a map of data necessary to build the authenticator URI
     * based on the specified userId. Use this to embed a user's email, username, etc.
     * in the URI.
     */
    BasicUserData getData(UUID userId);

}
