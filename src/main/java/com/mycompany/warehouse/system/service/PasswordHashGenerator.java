package com.mycompany.warehouse.system.service;

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
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.severe("Error hashing password: " + e.getMessage());
            return null;
        }

    }
    
    /**
     * Verify password dengan hash
     */
     public static boolean verifyPassword(String inputPassword, String storedHash) {
        if (inputPassword == null || storedHash == null) return false;
        String inputHash = hashPassword(inputPassword);
        return inputHash != null && inputHash.equalsIgnoreCase(storedHash);
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
