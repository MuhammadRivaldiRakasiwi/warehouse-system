package com.mycompany.warehouse.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardService {

    // method umum
    public static int getCount(String query) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 🔹 method khusus (lebih rapi dipakai di UI)
    public static int getTotalUsers() {
        return getCount("SELECT COUNT(*) FROM users");
    }

    public static int getTotalSuppliers() {
        return getCount("SELECT COUNT(*) FROM suppliers WHERE status_aktif = 1");
    }

    public static int getTotalLocations() {
        return getCount("SELECT COUNT(*) FROM locations WHERE status_aktif = 1");
    }

    public static int getTotalItems() {
        return getCount("SELECT COUNT(*) FROM items WHERE status_aktif = 1");
    }
}