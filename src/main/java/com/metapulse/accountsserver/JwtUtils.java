package com.metapulse.accountsserver;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class JwtUtils {

    public static SecretKey getSecretKey(String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);

        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");

        return key;
    }
}
