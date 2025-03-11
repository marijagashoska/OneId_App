package com.example.oneid.config;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.util.Base64;

public class KeyGenerationUtil {
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public static String privateKeyToPem(PrivateKey privateKey) throws IOException {
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        return privateKeyBase64;
    }

    public static String publicKeyToPem(PublicKey publicKey) throws IOException {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return publicKeyBase64;
    }
}
