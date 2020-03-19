package net.dashflight.data.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {

    private JwtVerifier jwtVerifier = new JwtVerifier();

    public DecodedJWT verifySession(String token, String tokenFgp) {
        DecodedJWT result = null;
        try {
            result = jwtVerifier.decodeJwtToken(token, tokenFgp);
        } catch (JWTVerificationException e) {
            // e.printStackTrace();
        }

        return result;
    }
}
