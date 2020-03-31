package net.dashflight.data.jwt;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.inject.Inject;
import net.dashflight.data.jwt.create.JwtCreator;
import net.dashflight.data.jwt.verify.JwtVerifier;

public class JwtUtil {

    private final JwtCreator creator;
    private final JwtVerifier verifier;

    @Inject
    public JwtUtil(JwtCreator creator, JwtVerifier verifier) {
        this.creator = creator;
        this.verifier = verifier;
    }


    public SecuredJwt generateFor(String userId) throws JWTCreationException {
        return creator.generateFor(userId);
    }

    public DecodedJWT verifyToken(String token, String fingerprint) throws JWTVerificationException {
        return verifier.verifyToken(token, fingerprint);
    }
}
