package com.metapulse.accountsserver;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/*A class used to generate a secretkey for our encryption alorithm, wich is AES, the key is generated using SHA256*/
public class JwtUtils {

    public static final String secretKey = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public static SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);

        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }
}
