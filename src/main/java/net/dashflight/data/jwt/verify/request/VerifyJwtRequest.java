package net.dashflight.data.jwt.verify.request;

import java.security.interfaces.RSAPublicKey;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyJwtRequest {

    /**
     * The jwt to verify and decode
     */
    private String token;

    /**
     * The fingerprint associated with the token
     */
    private String fingerprint;

    /**
     * The hash of the fingerprint
     */
    private String fingerprintHash;

    /**
     * The issuer (Who created the token)
     */
    private String issuer;

    /**
     * The public key needed to decode the jwt
     */
    private RSAPublicKey publicKey;

}
