package com.mycompany.warehouse.system;

import com.mycompany.warehouse.system.service.UserService;
import com.mycompany.warehouse.system.model.User;
import java.util.logging.Logger;

/**
 * Class untuk testing koneksi database dan autentikasi
 * Jalankan class ini untuk test sebelum menjalankan aplikasi
 */
public class DatabaseTest {
    
    private  static final Logger logger = Logger.getLogger(DatabaseTest.class.getName());
    
    public static void main(String[] args) {
        System.out.println("=== Warehouse System - Database Test ===\n");
        
        // Test 1: Koneksi Database
        System.out.println("Test 1: Testing Database Connection...");
        if (DatabaseConfig.testConnection()) {
            System.out.println("✓ Database connection successful!\n");
        } else {
            System.out.println("✗ Database connection failed!\n");
            System.exit(1);
        }
        
        // Test 2: Test Autentikasi dengan User Admin
        System.out.println("Test 2: Testing Authentication (admin user)...");
        User adminUser = UserService.authenticate("admin", "admin123");
        if (adminUser != null) {
            System.out.println("✓ Admin login successful!");
            System.out.println("  User: " + adminUser.getUsername());
            System.out.println("  Email: " + adminUser.getEmail());
            System.out.println("  Role: " + adminUser.getRole() + "\n");
        } else {
            System.out.println("✗ Admin login failed!\n");
        }
        
        // Test 3: Test Autentikasi dengan User Biasa
        System.out.println("Test 3: Testing Authentication (regular user)...");
        User regularUser = UserService.authenticate("user1", "admin123");
        if (regularUser != null) {
            System.out.println("✓ User1 login successful!");
            System.out.println("  User: " + regularUser.getUsername());
            System.out.println("  Email: " + regularUser.getEmail());
            System.out.println("  Role: " + regularUser.getRole() + "\n");
        } else {
            System.out.println("✗ User1 login failed!\n");
        }
        
        // Test 4: Test Login dengan Password Salah
        System.out.println("Test 4: Testing Authentication with wrong password...");
        User wrongPassword = UserService.authenticate("admin", "wrongpassword");
        if (wrongPassword == null) {
            System.out.println("✓ Correctly rejected wrong password\n");
        } else {
            System.out.println("✗ Security issue: wrong password accepted!\n");
        }
        
        // Test 5: Test Login dengan Username yang Tidak Ada
        System.out.println("Test 5: Testing Authentication with non-existent user...");
        User nonExistentUser = UserService.authenticate("nonexistent", "password");
        if (nonExistentUser == null) {
            System.out.println("✓ Correctly rejected non-existent user\n");
        } else {
            System.out.println("✗ Security issue: non-existent user accepted!\n");
        }
        
        System.out.println("=== All Tests Completed ===");
    }
}
