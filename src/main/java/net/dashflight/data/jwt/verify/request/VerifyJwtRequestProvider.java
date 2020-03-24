package net.dashflight.data.jwt.verify.request;

public interface VerifyJwtRequestProvider {

    JwtVerificationRequirements create(String token, String fingerprint);

}
