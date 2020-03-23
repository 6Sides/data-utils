package net.dashflight.data.jwt;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CreateJwtRequest {

    /**
     * The name of the service that issued the token
     */
    private String issuer;

    /**
     * The private key used to sign the token
     */
    private RSAPrivateKey privateKey;

    /**
     * The userId of who the token was generated for
     */
    private String userId;

    /**
     * The fingerprint associated with the `user_fingerprint` claim of the token
     */
    private String fingerprint;

    /**
     * The time the token was issued
     */
    private Instant issuedAt;

    /**
     * How long the token should live for (in seconds)
     */
    private int ttl;
}
