package net.dashflight.data.jwt.create;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.inject.Inject;
import java.util.Date;
import java.util.Map.Entry;
import net.dashflight.data.jwt.create.request.CreateJwtRequest;
import net.dashflight.data.jwt.create.request.CreateJwtRequestProvider;

/**
 * Handles creating JWTs
 */
class BasicJwtCreator implements JwtCreator {

    private final CreateJwtRequestProvider provider;

    @Inject
    public BasicJwtCreator(CreateJwtRequestProvider provider) {
        this.provider = provider;
    }


    /**
     * Creates a secure JWT with the userId encoded in its payload.
     *
     * How it works:
     *      1. Random fingerprint is generated.
     *      2. JWT is created storing any necessary claims and the hash of the fingerprint.
     *      3. The JWT is ciphered to obfuscate any internal data stored in the payload. (Currently omitted)
     */
    public String generateFor(String userId) throws JWTCreationException {
        if (userId == null) {
            throw new IllegalArgumentException("userId must be non-null");
        }

        CreateJwtRequest request = provider.create(userId);

        JWTCreator.Builder tokenBuilder = JWT.create();


        tokenBuilder.withIssuer(request.getIssuer())
                .withIssuedAt(Date.from(request.getIssuedAt()))
                .withExpiresAt(Date.from(request.getIssuedAt().plusSeconds(request.getTtl())));

        for (Entry<String, String> entry : request.getClaims().entrySet()) {
            tokenBuilder.withClaim(entry.getKey(), entry.getValue());
        }

        return tokenBuilder.sign(Algorithm.RSA512(null, request.getPrivateKey()));
    }

}
