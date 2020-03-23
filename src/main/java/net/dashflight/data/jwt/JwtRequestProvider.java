package net.dashflight.data.jwt;

public interface JwtRequestProvider {

    /**
     * Creates a jwt request for the specified userId
     */
    CreateJwtRequest create(String userId);

}
