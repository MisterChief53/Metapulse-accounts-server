package com.metapulse.accountsserver;
import java.util.Base64;
import java.security.SecureRandom;

public class KeyGenerator {
    /*Generates a secret key*/
    public static String generateSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        return  Base64.getEncoder().encodeToString(key);
    }
}