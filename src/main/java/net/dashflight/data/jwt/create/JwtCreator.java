package net.dashflight.data.jwt.create;

import com.auth0.jwt.exceptions.JWTCreationException;

/**
 * Generates a jwt based on a userId
 */
public interface JwtCreator {

    String generateFor(String userId) throws JWTCreationException;

}
