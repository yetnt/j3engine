package com.j3d.utility;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordHasher {

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 256;
    private static final int ITERATIONS = 65536;

    /**
     * Generates a random salt.
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes a password using PBKDF2WithHmacSHA256.
     */
    public static String hashPassword(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, HASH_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        // Return Base64 encoded hash
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verifies a password against a stored hash and salt.
     */
    public static boolean verifyPassword(char[] password, byte[] salt, String expectedHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        String hash = hashPassword(password, salt);
        return hash.equals(expectedHash);
    }
}