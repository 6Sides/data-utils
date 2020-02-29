package net.dashflight.data.keys;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import net.dashflight.data.config.ConfigValue;
import net.dashflight.data.config.Configurable;
import net.dashflight.data.config.RuntimeEnvironment;


public class DefaultRSAKeyManager implements Configurable {

    private static final String APP_NAME = "rsa-keypair";

    protected static KeyFactory keyFactory;

    static {
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @ConfigValue("public_key")
    private static String pubKey;

    @ConfigValue("private_key")
    private static String privKey;


    DefaultRSAKeyManager(RuntimeEnvironment env, Map<String, Object> properties) {
        registerWith(RegistrationOptions.builder()
            .applicationName(APP_NAME)
            .environment(env)
            .additionalProperties(properties)
            .build()
        );
    }

    public RSAPublicKey getPublicKey() {
        return jsonToPubKey(new ByteArrayInputStream(Base64.getDecoder().decode(pubKey)));
    }

    public RSAPrivateKey getPrivateKey() {
        return jsonToPrivKey(new ByteArrayInputStream(Base64.getDecoder().decode(privKey)));
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

    private static RSAPrivateKey jsonToPrivKey(InputStream input) {
        try {
            Map<String, String> data = new ObjectMapper().readValue(input, new TypeReference<HashMap<String, String>>(){});

            BigInteger modulus = new BigInteger(Base64.getDecoder().decode(data.get("n")));
            BigInteger exponent = new BigInteger(Base64.getDecoder().decode(data.get("e")));

            RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, exponent);
            return (RSAPrivateKey) keyFactory.generatePrivate(spec);
        } catch (IOException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static RSAPublicKey jsonToPubKey(InputStream input) {
        try {
            Map<String, String> data = new ObjectMapper().readValue(input, new TypeReference<HashMap<String, String>>(){});

            BigInteger modulus = new BigInteger(Base64.getDecoder().decode(data.get("n")));
            BigInteger exponent = new BigInteger(Base64.getDecoder().decode(data.get("e")));

            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            return (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (IOException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
