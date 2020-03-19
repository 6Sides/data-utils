package net.dashflight.data.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import net.dashflight.data.redis.RedisClient;
import net.dashflight.data.redis.RedisFactory;

/**
 * Handles verifying and decoding JWTs
 */
public class JwtVerifier extends JwtOperator {

    private RedisClient jwtCache = RedisFactory.withDefaults();

    /**
     * Decodes a JWT and returns it if it is valid
     *
     * How it works:
     *      1. token is deciphered.
     *      2. JWT is verified using the signing algorithm. Any time expiration checks are performed here.
     *      3. The hash of the provided fingerprint is matched against the user_fingerprint claim in the payload.
     *          If they match, the token is valid. Otherwise the JWT is rejected.
     *
     * @throws JWTVerificationException if token is unable to be decoded or verified
     */
    public DecodedJWT decodeJwtToken(String token, String fingerprint) {
        if (token == null || fingerprint == null) {
            throw new JWTVerificationException("The token and fingerprint must both be non-null.");
        }

        String fgpHash = fgpService.hashFingerprint(fingerprint);
        // String decipheredToken = tokenCipher.decipherToken(token);

        if (jwtCache.has(fgpHash)) {
            throw new JWTVerificationException("That token has been revoked");
        }

        JWTVerifier jwtVerifier = JWT.require(Algorithm.RSA512(keyManager.getPublicKey(), null))
                .withIssuer(ISSUER)
                .withClaim("user_fingerprint", fgpHash)
                .build();

        return jwtVerifier.verify(token);
    }
}
