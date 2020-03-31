package net.dashflight.data.jwt.create;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.inject.Inject;
import java.util.Date;
import java.util.Map.Entry;
import net.dashflight.data.jwt.FingerprintService;
import net.dashflight.data.jwt.SecuredJwt;
import net.dashflight.data.jwt.create.request.CreateJwtRequest;
import net.dashflight.data.jwt.create.request.CreateJwtRequestProvider;

/**
 * Handles creating JWTs
 */
public class JwtCreator {

    private final CreateJwtRequestProvider provider;
    private final FingerprintService fingerprintService;

    @Inject
    public JwtCreator(CreateJwtRequestProvider provider, FingerprintService fingerprintService) {
        this.provider = provider;
        this.fingerprintService = fingerprintService;
    }


    /**
     * Creates a secure JWT with the userId encoded in its payload.
     *
     * How it works:
     *      1. Random fingerprint is generated.
     *      2. JWT is created storing any necessary claims and the hash of the fingerprint.
     *      3. The JWT is ciphered to obfuscate any internal data stored in the payload. (Currently omitted)
     */
    public SecuredJwt generateFor(String userId) throws JWTCreationException {
        if (userId == null) {
            throw new IllegalArgumentException("userId must be non-null");
        }

        CreateJwtRequest request = provider.create(userId);
        String fingerprint = fingerprintService.generateRandomFingerprint();

        JWTCreator.Builder tokenBuilder = JWT.create();


        tokenBuilder.withIssuer(request.getIssuer())
                .withIssuedAt(Date.from(request.getIssuedAt()))
                .withExpiresAt(Date.from(request.getIssuedAt().plusSeconds(request.getTtl())));

        for (Entry<String, String> entry : request.getClaims().entrySet()) {
            tokenBuilder.withClaim(entry.getKey(), entry.getValue());
        }

        tokenBuilder.withClaim("fgp", fingerprintService.hashFingerprint(fingerprint));

        String token = tokenBuilder.sign(Algorithm.RSA512(null, request.getPrivateKey()));


        return new SecuredJwt(token, fingerprint);
    }

}
