package net.dashflight.data.jwt.create.request;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;


/**
 * A data object containing the necessary information to create a jwt
 */
@Data
@Builder
public class CreateJwtRequest {

    /**
     * The name of the service that issued the token
     */
    private String issuer;

    /**
     * The time the token was issued
     */
    private Instant issuedAt;

    /**
     * How long the token should live for (in seconds)
     */
    private int ttl;

    /**
     * Claims to be added to the jwt payload. Defaults to empty Map.
     */
    @Builder.Default
    private Map<String, String> claims = Collections.emptyMap();

    /**
     * The private key used to sign the token
     */
    private RSAPrivateKey privateKey;

}
