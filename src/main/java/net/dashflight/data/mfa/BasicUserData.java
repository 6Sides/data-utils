package net.dashflight.data.mfa;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
public class BasicUserData {

    /**
     * The user's unique id
     */
    private UUID userId;

    /**
     * The unique secret associated with the user
     */
    private String userSecret;

    /**
     * The email address of the user
     */
    private String email;

}
