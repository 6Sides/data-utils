package net.dashflight.data.jwt.verify;

public interface VerifyJwtRequestProvider {

    VerifyJwtRequest create(String token, String fingerprint);

}
