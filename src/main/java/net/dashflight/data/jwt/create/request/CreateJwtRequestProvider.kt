package net.dashflight.data.jwt.create.request;

public interface CreateJwtRequestProvider {

    /**
     * Creates a jwt request for the specified userId
     */
    CreateJwtRequest create(String userId);

}
