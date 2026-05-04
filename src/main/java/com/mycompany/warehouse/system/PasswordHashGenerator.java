package com.mycompany.warehouse.system;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Utility class untuk generate dan verify password hash menggunakan PBKDF2
 */
public class PasswordHashGenerator {
    
    private static final Logger logger = Logger.getLogger(PasswordHashGenerator.class.getName());
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    
    /**
     * Generate password hash menggunakan PBKDF2
     */
    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Repeat hashing untuk security
            for (int i = 0; i < ITERATIONS; i++) {
                md.reset();
                md.update(hashedPassword);
                hashedPassword = md.digest();
            }
            
            // Combine salt + hash
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            logger.severe("Error hashing password: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verify password dengan hash
     */
    public static boolean verifyPassword(String password, String hash) {
        try {
            byte[] saltAndHash = Base64.getDecoder().decode(hash);
            byte[] salt = new byte[16];
            System.arraycopy(saltAndHash, 0, salt, 0, 16);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            for (int i = 0; i < ITERATIONS; i++) {
                md.reset();
                md.update(hashedPassword);
                hashedPassword = md.digest();
            }
            
            byte[] storedHash = new byte[saltAndHash.length - 16];
            System.arraycopy(saltAndHash, 16, storedHash, 0, storedHash.length);
            
            return MessageDigest.isEqual(hashedPassword, storedHash);
        } catch (Exception e) {
            logger.severe("Error verifying password: " + e.getMessage());
            return false;
        }
    }
    
    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        System.out.println("=== Password Hash Generator ===\n");
        
        while (true) {
            System.out.print("Masukkan password (atau 'exit' untuk keluar): ");
            String password = scanner.nextLine().trim();
            
            if (password.equalsIgnoreCase("exit")) {
                System.out.println("Terima kasih!");
                break;
            }
            
            if (password.isEmpty()) {
                System.out.println("Password tidak boleh kosong!\n");
                continue;
            }
            
            String hash = hashPassword(password);
            
            System.out.println("\nPassword: " + password);
            System.out.println("Hash: " + hash);
            System.out.println("\nGunakan hash di atas untuk INSERT ke database:");
            System.out.println("INSERT INTO users (username, password, email, role) VALUES");
            System.out.println("('username', '" + hash + "', 'email@example.com', 'user');\n");
            
            boolean verified = verifyPassword(password, hash);
            System.out.println("Verifikasi: " + (verified ? "✓ OK" : "✗ FAILED"));
            System.out.println();
        }
        
        scanner.close();
    }
}
