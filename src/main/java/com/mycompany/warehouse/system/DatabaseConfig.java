package com.mycompany.warehouse.system;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Konfigurasi koneksi database TiDB.
 *
 * <p>Koneksi diambil dari connection pool (HikariCP). Membuka koneksi baru ke
 * TiDB Cloud (TCP + TLS + login) memakan waktu sekitar 2 detik, sedangkan query
 * lewat koneksi yang sudah terbuka hanya puluhan milidetik. Pool menjaga
 * beberapa koneksi tetap terbuka dan memakainya ulang.</p>
 *
 * <p>Cara pakai tidak berubah. Selalu tutup koneksi dengan try-with-resources;
 * {@code close()} mengembalikan koneksi ke pool (tidak memutus koneksi):</p>
 * <pre>
 * try (Connection conn = DatabaseConfig.getConnection()) { ... }
 * </pre>
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

    /** Pool dibuat sekali saat pertama dibutuhkan (lazy), lalu dipakai seluruh aplikasi. */
    private static volatile HikariDataSource dataSource;

    /**
     * Mendapatkan koneksi database dari pool.
     * Wajib ditutup (try-with-resources) agar kembali ke pool.
     * @return Connection object
     * @throws SQLException jika koneksi gagal
     */
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /**
     * Membuka koneksi pertama di background thread, supaya saat user login
     * koneksi ke TiDB Cloud sudah siap. Aman dipanggil berkali-kali.
     */
    public static void warmUp() {
        Thread warmUpThread = new Thread(() -> {
            try (Connection conn = getConnection()) {
                logger.log(Level.INFO, "Connection pool database siap");
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Warm-up koneksi database gagal: {0}", e.getMessage());
            }
        }, "db-warmup");
        warmUpThread.setDaemon(true);
        warmUpThread.start();
    }

    private static HikariDataSource getDataSource() throws SQLException {
        HikariDataSource ds = dataSource;
        if (ds == null) {
            synchronized (DatabaseConfig.class) {
                ds = dataSource;
                if (ds == null) {
                    try {
                        ds = new HikariDataSource(buildPoolConfig());
                    } catch (RuntimeException e) {
                        logger.log(Level.SEVERE, "Gagal membuat connection pool database", e);
                        throw new SQLException("Gagal membuat connection pool database: " + e.getMessage(), e);
                    }
                    dataSource = ds;
                }
            }
        }
        return ds;
    }

    private static HikariConfig buildPoolConfig() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("WarehousePool");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);

        // Aplikasi desktop (1 user): pool kecil sudah cukup.
        config.setMaximumPoolSize(6);
        config.setMinimumIdle(3);

        // Pool dibuat tanpa menunggu database; koneksi dibuka di background.
        config.setInitializationFailTimeout(-1);
        // Batas waktu menunggu koneksi dari pool sebelum error.
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(15));
        config.setValidationTimeout(TimeUnit.SECONDS.toMillis(5));

        // TiDB Cloud Starter/Essential: koneksi idle diputus jaringan setelah ~340 detik
        // dan koneksi yang terbuka > 30 menit bisa diputus. TiDB Cloud menyarankan
        // umur koneksi maksimum 5 menit; keepalive dikirim tiap 2 menit.
        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(5));
        config.setKeepaliveTime(TimeUnit.MINUTES.toMillis(2));
        config.setIdleTimeout(TimeUnit.MINUTES.toMillis(2));

        // Menulis peringatan ke log jika ada koneksi yang tidak ditutup lebih dari 60 detik.
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(60));

        // Tuning MySQL Connector/J (aman untuk TiDB).
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("connectTimeout", "10000");
        config.addDataSourceProperty("socketTimeout", "120000");
        return config;
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
