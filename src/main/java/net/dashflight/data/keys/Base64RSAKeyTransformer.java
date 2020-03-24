package net.dashflight.data.keys;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Transforms Base64 json representations of and RSA key pair into java objects
 */
public class Base64RSAKeyTransformer implements RSAKeyPairTransformer {

    private KeyFactory keyFactory;

    private ObjectMapper mapper = new ObjectMapper();

    public Base64RSAKeyTransformer() throws NoSuchAlgorithmException {
        keyFactory = KeyFactory.getInstance("RSA");
    }


    @Override
    public RSAPublicKey transformPublicKey(String rawData) throws InvalidKeyException {
        try {
            KeyComponents components = parseData(rawData);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(components.modulus, components.exponent);
            return (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (IOException | InvalidKeySpecException e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }

    @Override
    public RSAPrivateKey transformPrivateKey(String rawData) throws InvalidKeyException {
        try {
            KeyComponents components = parseData(rawData);

            RSAPrivateKeySpec spec = new RSAPrivateKeySpec(components.modulus, components.exponent);
            return (RSAPrivateKey) keyFactory.generatePrivate(spec);
        } catch (IOException | InvalidKeySpecException e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }

    private KeyComponents parseData(String rawData) throws IOException {
        try (InputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(rawData))) {
            Map<String, String> data = mapper.readValue(input, new TypeReference<HashMap<String, String>>(){});

            BigInteger modulus = new BigInteger(Base64.getDecoder().decode(data.get("n")));
            BigInteger exponent = new BigInteger(Base64.getDecoder().decode(data.get("e")));

            return new KeyComponents(modulus, exponent);
        }
    }


    /**
     * Representation of RSA key components
     */
    private static class KeyComponents {
        private BigInteger modulus, exponent;

        public KeyComponents(BigInteger modulus, BigInteger exponent) {
            this.modulus = modulus;
            this.exponent = exponent;
        }
    }
}
