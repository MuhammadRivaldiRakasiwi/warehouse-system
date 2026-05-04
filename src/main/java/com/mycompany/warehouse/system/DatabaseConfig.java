package com.mycompany.warehouse.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Konfigurasi koneksi database TiDB
 */
public class DatabaseConfig {
    
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    
    // Database Configuration
    private static final String HOST = "gateway01.ap-southeast-1.prod.aws.tidbcloud.com";
    private static final int PORT = 4000;
    private static final String USERNAME = "2bQGoHWjBnMaSNx.root";
    private static final String PASSWORD = "8SglvCDWsgNdYHaB";
    private static final String DATABASE = "warehouse";
    
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE 
            + "?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    
    /**
     * Mendapatkan koneksi database
     * @return Connection object
     * @throws SQLException jika koneksi gagal
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL Driver tidak ditemukan", e);
            throw new SQLException("MySQL Driver tidak ditemukan", e);
        }
    }
    
    /**
     * Test koneksi database
     * @return true jika koneksi berhasil
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            logger.log(Level.INFO, "Koneksi database berhasil!");
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Koneksi database gagal: " + e.getMessage(), e);
            return false;
        }
    }
}
