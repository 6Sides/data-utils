package net.dashflight.data.jwt;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.inject.Inject;
import net.dashflight.data.jwt.create.JwtCreator;
import net.dashflight.data.jwt.verify.JwtVerifier;

public class JwtUtil {

    private JwtCreator creator;
    private JwtVerifier verifier;


    @Inject
    public JwtUtil(JwtCreator creator, JwtVerifier verifier) {
        this.creator = creator;
        this.verifier = verifier;
    }


    public SecuredJwt generateJwt(String userId) throws JWTCreationException {
        return creator.generateJwt(userId);
    }

    public DecodedJWT decodeJwtToken(String token, String fingerprint) throws JWTVerificationException {
        return verifier.decodeJwtToken(token, fingerprint);
    }
}
