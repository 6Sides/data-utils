package net.dashflight.data.jwt.verify;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

/**
 * TODO: Add back check to redis to ensure token hasn't been revoked
 *
 * Handles verifying and decoding JWTs
 */
public class JwtVerifier {

    /**
     * Decodes a JWT and returns it if it is valid
     *
     * How it works:
     *      1. Token is deciphered. (Currently omitted as tokens are not ciphered)
     *      2. JWT is verified using the signing algorithm. Any time expiration checks are performed here.
     *      3. The hash of the provided fingerprint is matched against the user_fingerprint claim in the payload.
     *          If they match, the token is valid. Otherwise the JWT is rejected.
     *
     * @throws JWTVerificationException if token is unable to be decoded or verified
     */
    public DecodedJWT decodeJwtToken(VerifyJwtRequest request) {
        if (request.getToken() == null || request.getFingerprint() == null) {
            throw new JWTVerificationException("The token and fingerprint must both be non-null.");
        }

        JWTVerifier jwtVerifier = JWT.require(Algorithm.RSA512(request.getPublicKey(), null))
                .withIssuer(request.getIssuer())
                .withClaim("user_fingerprint", request.getFingerprintHash())
                .build();

        return jwtVerifier.verify(request.getToken());
    }
}
