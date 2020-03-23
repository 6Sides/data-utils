package net.dashflight.data.jwt.create;

public interface CreateJwtRequestProvider {

    /**
     * Creates a jwt request for the specified userId
     */
    CreateJwtRequest create(String userId);

}
