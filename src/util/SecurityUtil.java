package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {

    private SecurityUtil() {}

    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String salted = salt + password;
            byte[] hash = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    public static String createStoredPassword(String password) {
        String salt = generateSalt();
        return salt + ":" + hashPassword(password, salt);
    }

    public static boolean matchesStoredPassword(String plainPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.trim().isEmpty()) {
            return false;
        }

        String[] parts = storedPassword.split(":", 2);
        if (parts.length == 2) {
            return hashPassword(plainPassword, parts[0]).equals(parts[1]);
        }

        return hashPassword(plainPassword, "").equals(storedPassword);
    }
}
