package net.dashflight.data.jwt.verify.request;

public interface VerifyJwtRequestProvider {

    VerifyJwtRequest create(String token, String fingerprint);

}
