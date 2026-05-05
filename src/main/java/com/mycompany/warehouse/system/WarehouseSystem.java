/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.warehouse.system;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class untuk Warehouse System
 * @author Rivaldi
 */
public class WarehouseSystem {
    
    private static final Logger logger = Logger.getLogger(WarehouseSystem.class.getName());

    public static void main(String[] args) {
        
        
        logger.log(Level.INFO, "Starting Warehouse System...");

        // Test database connection
        logger.log(Level.INFO, "Testing database connection...");
        if (!DatabaseConfig.testConnection()) {
            logger.log(Level.SEVERE, "Database connection failed!");
            System.exit(1);
        }
        
        logger.log(Level.INFO, "Database connection successful!");
        
        // Show login form
        java.awt.EventQueue.invokeLater(() -> {
            form_logins loginForm = new form_logins();
            loginForm.setVisible(true);
            logger.log(Level.INFO, "Login form displayed");
        });
    }
}
