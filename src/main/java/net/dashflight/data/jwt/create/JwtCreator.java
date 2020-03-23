package net.dashflight.data.jwt.create;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import java.util.Date;
import net.dashflight.data.jwt.SecuredJwt;

/**
 * Handles creating JWTs
 */
public class JwtCreator {

    /**
     * Creates a secure JWT with the userId encoded in its payload.
     *
     * How it works:
     *      1. Random fingerprint is generated.
     *      2. JWT is created storing any necessary claims and the hash of the fingerprint.
     *      3. The JWT is ciphered to obfuscate any internal data stored in the payload. (Currently omitted)
     */
    public SecuredJwt generateJwt(CreateJwtRequest request) throws JWTCreationException {
        String token = JWT.create()
                        .withIssuer(request.getIssuer())
                        .withIssuedAt(Date.from(request.getIssuedAt()))
                        .withExpiresAt(Date.from(request.getIssuedAt().plusSeconds(request.getTtl())))
                        .withClaim("user_id", request.getUserId())
                        .withClaim("user_fingerprint", request.getFingerprintHash())
                        .sign(Algorithm.RSA512(null, request.getPrivateKey()));

        return new SecuredJwt(token, request.getFingerprint());
    }

}
