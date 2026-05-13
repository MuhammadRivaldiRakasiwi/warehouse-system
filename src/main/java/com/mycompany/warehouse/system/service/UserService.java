package com.mycompany.warehouse.system.service;

import com.mycompany.warehouse.system.DatabaseConfig;
import com.mycompany.warehouse.system.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service untuk autentikasi user
 */
public class UserService {
    
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    
    /**
     * Autentikasi user berdasarkan username dan password
     * @param username username user
     * @param password password user (plain text)
     * @return User object jika autentikasi berhasil, null jika gagal
     */
    public static User authenticate(String username, String password) {
    // Gunakan .trim() untuk membuang spasi yang tidak sengaja terketik
    if (username == null || username.trim().isEmpty() || password == null) {
        return null;
    }

    String cleanUsername = username.trim();
    String query = "SELECT id, username, email, role, password_hash FROM users WHERE username = ?";
    
    try (Connection conn = DatabaseConfig.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setString(1, cleanUsername);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String storedPasswordHash = rs.getString("password_hash");

            // Gunakan .trim() juga pada hash dari DB untuk jaga-jaga ada spasi di DB
            if (PasswordHashGenerator.verifyPassword(password, storedPasswordHash.trim())) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                return user;
            }
        }
    } catch (SQLException e) {
        logger.log(Level.SEVERE, "Database error: " + e.getMessage());
    }
    return null;
}
    /**
     * Registrasi user baru
     * @param username username baru
     * @param password password baru (plain text)
     * @param email email user
     * @param role role user (default: "user")
     * @return true jika registrasi berhasil
     */
    public static boolean register(String username, String password, String email, String role) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            logger.log(Level.WARNING, "Data registrasi tidak lengkap");
            return false;
        }
        
        // Cek apakah username sudah ada
        if (userExists(username)) {
            logger.log(Level.WARNING, "Username sudah terdaftar: " + username);
            return false;
        }
        
        // Hash password
        String hashedPassword = PasswordHashGenerator.hashPassword(password);
        
        String query = "INSERT INTO users (username, password_hash, email, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, email);
            stmt.setString(4, role != null ? role : "user");
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                logger.log(Level.INFO, "User baru terdaftar: " + username);
                return true;
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saat registrasi: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    /**
     * Cek apakah username sudah ada
     * @param username username yang dicek
     * @return true jika username sudah ada
     */
    public static boolean userExists(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error cek user: " + e.getMessage(), e);
        }
        
        return false;
    }
}
