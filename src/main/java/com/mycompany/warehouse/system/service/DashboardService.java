package com.mycompany.warehouse.system.service;

import com.mycompany.warehouse.system.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardService {

    private static final Logger logger = Logger.getLogger(DashboardService.class.getName());

    // Sub-query COUNT yang dipakai dashboard (konstanta, tanpa input user)
    private static final String COUNT_USERS = "SELECT COUNT(*) FROM users";
    private static final String COUNT_SUPPLIERS = "SELECT COUNT(*) FROM suppliers WHERE status_aktif = 1";
    private static final String COUNT_LOCATIONS = "SELECT COUNT(*) FROM locations WHERE status_aktif = 1";
    private static final String COUNT_ITEMS = "SELECT COUNT(*) FROM items WHERE status_aktif = 1";
    private static final String COUNT_MASUK = "SELECT COUNT(*) FROM inbound_transactions";
    private static final String COUNT_KELUAR = "SELECT COUNT(*) FROM outbound_transactions";
    private static final String COUNT_HAMPIR_HABIS = "SELECT COUNT(*) FROM inventory WHERE stok_terkini <= 10";
    // Rentang tanggal (bukan DATE(created_at)) agar index pada created_at bisa dipakai; hasilnya sama.
    private static final String COUNT_MASUK_HARI_INI = "SELECT COUNT(*) FROM inbound_transactions "
            + "WHERE created_at >= CURDATE() AND created_at < CURDATE() + INTERVAL 1 DAY";
    private static final String COUNT_KELUAR_HARI_INI = "SELECT COUNT(*) FROM outbound_transactions "
            + "WHERE created_at >= CURDATE() AND created_at < CURDATE() + INTERVAL 1 DAY";
    private static final String COUNT_INVENTORY = "SELECT COUNT(*) FROM inventory WHERE stok_terkini > 0";

    /** Ringkasan kartu DashboardAdmin. */
    public record AdminCounts(int users, int items, int suppliers, int locations,
            int barangMasuk, int barangKeluar) {}

    /** Ringkasan kartu DashboardManager. */
    public record ManagerCounts(int inventory, int barangMasuk, int barangKeluar) {}

    /** Ringkasan kartu DashboardStaff. */
    public record StaffCounts(int inventory, int barangMasukHariIni, int barangKeluarHariIni,
            int hampirHabis) {}

    // method umum
    public static int getCount(String query) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal menghitung data dashboard", e);
        }

        return 0;
    }

    /**
     * Menjalankan beberapa COUNT dalam SATU query (satu round trip ke TiDB Cloud),
     * bukan satu query/koneksi per angka. Jika gagal, semua nilai 0 (sama seperti getCount).
     */
    private static int[] getCounts(String... countQueries) {
        StringBuilder sql = new StringBuilder("SELECT ");
        for (int i = 0; i < countQueries.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append('(').append(countQueries[i]).append(')');
        }

        int[] result = new int[countQueries.length];
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString());
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                for (int i = 0; i < result.length; i++) {
                    result[i] = rs.getInt(i + 1);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal menghitung data dashboard", e);
        }
        return result;
    }

    /** Semua angka DashboardAdmin dalam satu query. */
    public static AdminCounts getAdminCounts() {
        int[] c = getCounts(COUNT_USERS, COUNT_ITEMS, COUNT_SUPPLIERS, COUNT_LOCATIONS,
                COUNT_MASUK, COUNT_KELUAR);
        return new AdminCounts(c[0], c[1], c[2], c[3], c[4], c[5]);
    }

    /** Semua angka DashboardManager dalam satu query. */
    public static ManagerCounts getManagerCounts() {
        int[] c = getCounts(COUNT_INVENTORY, COUNT_MASUK, COUNT_KELUAR);
        return new ManagerCounts(c[0], c[1], c[2]);
    }

    /** Semua angka DashboardStaff dalam satu query. */
    public static StaffCounts getStaffCounts() {
        int[] c = getCounts(COUNT_INVENTORY, COUNT_MASUK_HARI_INI, COUNT_KELUAR_HARI_INI,
                COUNT_HAMPIR_HABIS);
        return new StaffCounts(c[0], c[1], c[2], c[3]);
    }

    // 🔹 method khusus (lebih rapi dipakai di UI)
    public static int getTotalUsers() {
        return getCount(COUNT_USERS);
    }

    public static int getTotalSuppliers() {
        return getCount(COUNT_SUPPLIERS);
    }

    public static int getTotalLocations() {
        return getCount(COUNT_LOCATIONS);
    }

    public static int getTotalItems() {
        return getCount(COUNT_ITEMS);
    }

    public static int getTotalItemsMasuk() {
        return getCount(COUNT_MASUK);
    }

    public static int getTotalItemsKeluar() {
        return getCount(COUNT_KELUAR);
    }

    public static int getTotalItemsHampirHabis() {
        return getCount(COUNT_HAMPIR_HABIS);
    }

    public static int getTotalItemsMasukHariIni() {
        return getCount(COUNT_MASUK_HARI_INI);
    }

    public static int getTotalItemsKeluarHariIni() {
        return getCount(COUNT_KELUAR_HARI_INI);
    }

    public static int getTotalItemsInventory() {
        return getCount(COUNT_INVENTORY);
    }
}
