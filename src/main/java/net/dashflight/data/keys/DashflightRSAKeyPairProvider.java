package net.dashflight.data.keys;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;
import net.dashflight.data.config.RuntimeEnvironment;


public class DashflightRSAKeyPairProvider implements RSAKeyPairDataProvider, Configurable {

    private static final String APP_NAME = "rsa-keypair";

    @ConfigValue("public_key")
    private static String publicKey;

    @ConfigValue("private_key")
    private static String privateKey;


    DashflightRSAKeyPairProvider(RuntimeEnvironment env, Map<String, Object> properties) {
        registerWith(RegistrationOptions.builder()
            .applicationName(APP_NAME)
            .environment(env)
            .additionalProperties(properties)
            .build()
        );
    }

    @Override
    public String getPublicKeyData() {
        return publicKey;
    }

    @Override
    public String getPrivateKeyData() {
        return privateKey;
    }


    private String keyToJson(RSAKey key, String kid) {
        Map<String, String> data = new HashMap<>();

        data.put("kty", "RSA");
        data.put("alg", "RS512");
        data.put("use", "sig");
        data.put("kid", kid);
        data.put("n", new String(Base64.getEncoder().encode(key.getModulus().toByteArray())));

        if (key instanceof RSAPublicKey) {
            RSAPublicKey pubKey = ((RSAPublicKey) key);
            data.put("e", new String(Base64.getEncoder().encode(pubKey.getPublicExponent().toByteArray())));

        } else if (key instanceof RSAPrivateKey) {
            RSAPrivateKey privKey = ((RSAPrivateKey) key);
            data.put("e", new String(Base64.getEncoder().encode(privKey.getPrivateExponent().toByteArray())));
        }

        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch(IOException e) {
            // This should never happen
            e.printStackTrace();
        }
        return null;
    }
}
