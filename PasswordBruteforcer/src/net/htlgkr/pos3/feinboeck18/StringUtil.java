package net.htlgkr.pos3.feinboeck18;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
    /**
     * Applies SHA-256 algorithm to the given String.
     *
     * SHA (Secure Hash Algorithm 2) is a set of cryptographic hash functions
     * designed by the United States National Security Agency (NSA).Cryptographic
     * hash functions are mathematical operations run on digital data; by com-
     * paring the computed "hash" (the output from execution of the algorithm)
     * to a known and expected hash value, a person can determine the data's in-
     * tegrity.
     *
     * @param input Some String.
     * @return  Generated SHA-256 signature.
     **/
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
