package net.dashflight.data.jwt.verify;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Verifies and decodes jwts
 */
public interface JwtVerifier {

    DecodedJWT verifyToken(String token, String fingerprint) throws JWTVerificationException;

}
