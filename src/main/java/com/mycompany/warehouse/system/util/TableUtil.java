package com.mycompany.warehouse.system.util;

import com.mycompany.warehouse.system.DatabaseConfig;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Helper bersama untuk panel tabel: query database (dipanggil dari background
 * thread) dan pengaturan JTable/pagination (dipanggil dari EDT).
 */
public final class TableUtil {

    private static final Color PAGE_ACTIVE_BG = new Color(0, 153, 204);
    private static final Color PAGE_DISABLED_BG = new Color(220, 220, 220);
    private static final Color PAGE_DISABLED_FG = new Color(120, 120, 120);

    private TableUtil() {
    }

    /** Mengubah satu baris ResultSet menjadi satu baris tabel. */
    @FunctionalInterface
    public interface RowMapper {
        Object[] map(ResultSet rs) throws SQLException;
    }

    /** Hasil query satu halaman: total baris, halaman yang dipakai (sudah dibatasi), dan isi halaman. */
    public record Page(int totalRows, int totalPages, int page, List<Object[]> rows) {}

    // ------------------------------------------------------------------
    // DATABASE (panggil dari background thread, BUKAN dari EDT)
    // ------------------------------------------------------------------

    /** Menjalankan SELECT dengan parameter dan mengembalikan semua baris. */
    public static List<Object[]> query(String sql, RowMapper mapper, Object... params) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return query(conn, sql, mapper, params);
        }
    }

    /** Menjalankan SELECT COUNT(*) (atau query lain yang mengembalikan satu angka). */
    public static int count(String sql, Object... params) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return count(conn, sql, params);
        }
    }

    /**
     * Menghitung total data lalu mengambil satu halaman, memakai SATU koneksi.
     * Jika halaman yang diminta melebihi jumlah halaman (mis. setelah data dihapus),
     * halaman terakhir yang dipakai.
     *
     * @param pageSql query dengan dua parameter terakhir {@code LIMIT ? OFFSET ?}
     */
    public static Page queryPage(String countSql, String pageSql, int page, int pageSize, RowMapper mapper)
            throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            int total = count(conn, countSql);
            int totalPages = (int) Math.ceil((double) total / pageSize);
            int safePage = Math.max(1, Math.min(page, Math.max(1, totalPages)));
            int offset = (safePage - 1) * pageSize;
            List<Object[]> rows = query(conn, pageSql, mapper, pageSize, offset);
            return new Page(total, totalPages, safePage, rows);
        }
    }

    private static List<Object[]> query(Connection conn, String sql, RowMapper mapper, Object... params)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<Object[]> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(mapper.map(rs));
                }
                return rows;
            }
        }
    }

    private static int count(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private static void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    /** Pola LIKE "%keyword%" dengan karakter wildcard (% _ \) di-escape. */
    public static String like(String keyword) {
        String k = keyword == null ? "" : keyword.trim();
        k = k.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + k + "%";
    }

    // ------------------------------------------------------------------
    // UI (panggil dari EDT)
    // ------------------------------------------------------------------

    /** Model tabel read-only berisi kolom dan baris yang diberikan. */
    public static DefaultTableModel readOnlyModel(String[] columns, List<Object[]> rows) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Object[] row : rows) {
            model.addRow(row);
        }
        return model;
    }

    /** Menyembunyikan kolom (mis. kolom ID) tanpa menghapusnya dari model. */
    public static void hideColumn(JTable table, int index) {
        if (index < table.getColumnModel().getColumnCount()) {
            table.getColumnModel().getColumn(index).setMinWidth(0);
            table.getColumnModel().getColumn(index).setMaxWidth(0);
            table.getColumnModel().getColumn(index).setPreferredWidth(0);
        }
    }

    /** Mengaktifkan/menonaktifkan tombol Prev/Next beserta warnanya (gaya yang sama seperti sebelumnya). */
    public static void updatePagination(JButton prev, JButton next, int page, int totalPages) {
        stylePageButton(prev, page > 1);
        stylePageButton(next, page < totalPages);
    }

    /** Menonaktifkan tombol Prev/Next (dipakai saat menampilkan hasil pencarian). */
    public static void disablePagination(JButton prev, JButton next) {
        stylePageButton(prev, false);
        stylePageButton(next, false);
    }

    private static void stylePageButton(JButton button, boolean enabled) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setEnabled(enabled);
        button.setBackground(enabled ? PAGE_ACTIVE_BG : PAGE_DISABLED_BG);
        button.setForeground(enabled ? Color.WHITE : PAGE_DISABLED_FG);
    }

    /** Teks label halaman, contoh "Page 1 / 3". */
    public static String pageLabel(int page, int totalPages) {
        return "Page " + page + " / " + Math.max(1, totalPages);
    }

    /** DocumentListener yang menjalankan aksi setiap isi text field berubah (ketik, hapus, paste). */
    public static DocumentListener onChange(Runnable action) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        };
    }
}
