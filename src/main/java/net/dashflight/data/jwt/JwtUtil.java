package net.dashflight.data.jwt;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * An interface allow clients to both create and verify jwts
 */
public interface JwtUtil {

    String generateFor(String userId) throws JWTCreationException;

    DecodedJWT verifyToken(String token, String fingerprint) throws JWTVerificationException;

}
