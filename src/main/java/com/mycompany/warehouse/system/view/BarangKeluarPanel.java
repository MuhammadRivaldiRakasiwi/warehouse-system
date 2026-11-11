/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.warehouse.system.view;
import com.mycompany.warehouse.system.DatabaseConfig;
import com.mycompany.warehouse.system.util.Debouncer;
import com.mycompany.warehouse.system.util.TableUtil;
import com.mycompany.warehouse.system.util.UiWorker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
/**
 *
 * @author ndesc
 */
public class BarangKeluarPanel extends javax.swing.JPanel {
    private static final String[] KOLOM =
            {"ID", "Penerima/Requestor", "No Pengiriman", "Tujuan", "Barang", "Satuan", "Quantity"};

    private static final String SELECT_OUTBOUND = """
            SELECT ot.id, ot.penerima_requestor, ot.nomor_pengeluaran, ot.tujuan,
                   i.nama_item, i.satuan, ot.qty
            FROM outbound_transactions ot
            INNER JOIN items i ON ot.item_id = i.id
            """;

    private static final TableUtil.RowMapper MAP_OUTBOUND = rs -> new Object[]{
        rs.getInt("id"),
        rs.getString("penerima_requestor"),
        rs.getString("nomor_pengeluaran"),
        rs.getString("tujuan"),
        rs.getString("nama_item"),
        rs.getString("satuan"),
        rs.getString("qty")
    };

    private final HashMap<String, Integer> itemMap = new HashMap<>();
    private final HashMap<String, Integer> locationMap = new HashMap<>();

    // Search dijalankan 350 ms setelah user berhenti mengetik (bukan per huruf)
    private final Debouncer searchDebouncer = new Debouncer(350, this::cariBarangKeluar);
    // Nomor request terakhir: hasil query lama yang datang terlambat diabaikan
    private int requestTabel = 0;
    private int requestBarang = 0;
    private int requestLokasi = 0;
    private int requestNomor = 0;
    // Tombol Kirim hanya aktif jika nomor pengeluaran & daftar lokasi sudah siap
    private boolean nomorSiap = false;
    private boolean lokasiSiap = false;

    private int currentPage = 1;
    private final int dataPerPage = 10;
    private int totalPage = 0;

    /**
     * Creates new form BarangKeluarPanel
     */
    public BarangKeluarPanel() {
        initComponents();
        inputTanggalKirim.setDateFormatString("yyyy-MM-dd");
        inputTanggalKirim.getDateEditor().setEnabled(false);
        inputTanggalKirim.setDate(new java.util.Date());

        // Style tabel cukup diatur sekali (sebelumnya diulang setiap load)
        TBarangKeluar.setForeground(new java.awt.Color(33, 37, 41));
        TBarangKeluar.setBackground(java.awt.Color.WHITE);
        TBarangKeluar.setSelectionForeground(java.awt.Color.WHITE);
        TBarangKeluar.setSelectionBackground(new java.awt.Color(0, 153, 204));
        TBarangKeluar.setGridColor(new java.awt.Color(230, 230, 230));
        TBarangKeluar.setRowHeight(31);
        TBarangKeluar.getTableHeader().setForeground(java.awt.Color.BLACK);
        TBarangKeluar.getTableHeader().setBackground(new java.awt.Color(245, 245, 245));

        // Search realtime (dengan debounce), termasuk saat paste
        TSearchBarangKeluar.getDocument().addDocumentListener(TableUtil.onChange(searchDebouncer::trigger));
        TableUtil.disablePagination(BPBarangKeluar, BNBarangKeluar);

        refresh();
    }

    /**
     * Memuat ulang semua data panel (nomor pengeluaran, dropdown barang, tabel) secara async.
     * Dipanggil ContentFrame saat menu Barang Keluar dibuka lagi.
     */
    public void refresh() {
        generateNomorPengeluaran();
        loadBarangFromInventory();
        loadDataOutbound();
    }

    private void updateTombolKirim() {
        btnKirim.setEnabled(nomorSiap && lokasiSiap);
    }

    private void generateNomorPengeluaran() {
        final int request = ++requestNomor;
        nomorSiap = false;
        updateTombolKirim();
        UiWorker.run(
                () -> TableUtil.count("SELECT COUNT(*) + 1 AS total FROM outbound_transactions"),
                nomor -> {
                    if (request != requestNomor) {
                        return;
                    }
                    labelNoPengeluaran.setText(String.format("DO-2026-%03d", nomor));
                    nomorSiap = true;
                    updateTombolKirim();
                },
                e -> {
                    if (request != requestNomor) {
                        return;
                    }
                    nomorSiap = true;
                    updateTombolKirim();
                    e.printStackTrace();
                });
    }

    /** Memuat dropdown barang yang stoknya > 0 (async). */
    public void loadBarangFromInventory() {
        final int request = ++requestBarang;
        UiWorker.run(
                () -> TableUtil.query("""
                        SELECT DISTINCT items.id, items.nama_item
                        FROM inventory
                        JOIN items ON inventory.item_id = items.id
                        WHERE inventory.stok_terkini > 0
                        ORDER BY items.nama_item
                        """,
                        rs -> new Object[]{rs.getInt("id"), rs.getString("nama_item")}),
                rows -> {
                    if (request != requestBarang) {
                        return;
                    }
                    // Map diisi dulu, karena addItem() memicu inputNamaBarangActionPerformed
                    itemMap.clear();
                    for (Object[] r : rows) {
                        itemMap.put((String) r[1], (Integer) r[0]);
                    }
                    inputNamaBarang.removeAllItems();
                    for (Object[] r : rows) {
                        inputNamaBarang.addItem((String) r[1]);
                    }
                    if (rows.isEmpty()) {
                        inputLokasi.removeAllItems();
                        locationMap.clear();
                        lokasiSiap = true;
                        updateTombolKirim();
                    }
                },
                Exception::printStackTrace);
    }

    /** Memuat dropdown lokasi (beserta stok) untuk barang terpilih (async). */
    private void loadLokasiByItem(int itemId) {
        final int request = ++requestLokasi;
        lokasiSiap = false;
        updateTombolKirim();
        UiWorker.run(
                () -> TableUtil.query("""
                        SELECT inventory.location_id, inventory.stok_terkini, locations.kode_lokasi
                        FROM inventory
                        JOIN locations ON inventory.location_id = locations.id
                        WHERE inventory.item_id = ?
                          AND inventory.stok_terkini > 0
                        """,
                        rs -> new Object[]{rs.getInt("location_id"), rs.getString("kode_lokasi"),
                            rs.getInt("stok_terkini")},
                        itemId),
                rows -> {
                    if (request != requestLokasi) {
                        return;
                    }
                    inputLokasi.removeAllItems();
                    locationMap.clear();
                    for (Object[] r : rows) {
                        // tampilkan stock
                        String display = r[1] + " (Stock: " + r[2] + ")";
                        inputLokasi.addItem(display);
                        locationMap.put(display, (Integer) r[0]);
                    }
                    lokasiSiap = true;
                    updateTombolKirim();
                },
                e -> {
                    if (request != requestLokasi) {
                        return;
                    }
                    lokasiSiap = true;
                    updateTombolKirim();
                    e.printStackTrace();
                });
    }

    private int getCurrentStock(int itemId, int locationId) {
        try {
            return TableUtil.count("""
                    SELECT stok_terkini
                    FROM inventory
                    WHERE item_id = ?
                    AND location_id = ?
                    """, itemId, locationId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public javax.swing.JScrollPane asScrollable() {
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(this);
        sp.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return sp;
    }

    /** Memuat halaman data barang keluar saat ini (async). */
    public final void loadDataOutbound() {
        if (!TSearchBarangKeluar.getText().trim().isEmpty()) {
            cariBarangKeluar();
            return;
        }
        final int request = ++requestTabel;
        final int page = currentPage;
        UiWorker.run(
                () -> TableUtil.queryPage(
                        "SELECT COUNT(*) FROM outbound_transactions",
                        SELECT_OUTBOUND + " ORDER BY ot.id DESC LIMIT ? OFFSET ?",
                        page, dataPerPage, MAP_OUTBOUND),
                result -> {
                    if (request != requestTabel) {
                        return;
                    }
                    currentPage = result.page();
                    totalPage = result.totalPages();
                    tampilkanTabel(result.rows());
                    LPBarangMasuk.setText(TableUtil.pageLabel(currentPage, totalPage));
                    TableUtil.updatePagination(BPBarangKeluar, BNBarangKeluar, currentPage, totalPage);
                },
                e -> tampilkanError(request == requestTabel, "Gagal memuat data: " + e.getMessage()));
    }

    private void cariBarangKeluar() {
        String keyword = TSearchBarangKeluar.getText().trim();
        if (keyword.isEmpty()) {
            loadDataOutbound();
            return;
        }
        final int request = ++requestTabel;
        final String pola = TableUtil.like(keyword);
        UiWorker.run(
                () -> TableUtil.query(
                        SELECT_OUTBOUND + """
                         WHERE ot.penerima_requestor LIKE ?
                            OR ot.nomor_pengeluaran LIKE ?
                            OR ot.tujuan LIKE ?
                            OR i.nama_item LIKE ?
                            OR i.satuan LIKE ?
                            OR CAST(ot.qty AS CHAR) LIKE ?
                         ORDER BY ot.id DESC
                         LIMIT 50
                        """,
                        MAP_OUTBOUND, pola, pola, pola, pola, pola, pola),
                rows -> {
                    if (request != requestTabel) {
                        return;
                    }
                    tampilkanTabel(rows);
                    LPBarangMasuk.setText("Hasil pencarian: " + rows.size());
                    TableUtil.disablePagination(BPBarangKeluar, BNBarangKeluar);
                },
                e -> tampilkanError(request == requestTabel, e.getMessage()));
    }

    private void tampilkanTabel(List<Object[]> rows) {
        TBarangKeluar.setModel(TableUtil.readOnlyModel(KOLOM, rows));
        TableUtil.hideColumn(TBarangKeluar, 0);
    }

    private void tampilkanError(boolean masihRelevan, String pesan) {
        if (masihRelevan) {
            JOptionPane.showMessageDialog(javax.swing.SwingUtilities.getWindowAncestor(this), pesan);
        }
    }
/**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        btnKirim = new javax.swing.JButton();
        labelNoPengeluaran = new javax.swing.JLabel();
        inputTanggalKirim = new com.toedter.calendar.JDateChooser();
        inputNamaBarang = new javax.swing.JComboBox<>();
        labelSatuan = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        inputTujuan = new javax.swing.JTextField();
        Lokasi = new javax.swing.JLabel();
        inputLokasi = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        inputQty = new javax.swing.JSpinner();
        inputPenerima = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        TSearchBarangKeluar = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        TBarangKeluar = new javax.swing.JTable();
        BPBarangKeluar = new javax.swing.JButton();
        BNBarangKeluar = new javax.swing.JButton();
        LPBarangMasuk = new javax.swing.JLabel();
        BSearchBarangKeluar = new javax.swing.JButton();

        setBackground(new java.awt.Color(248, 250, 252));
        setMaximumSize(new java.awt.Dimension(132767, 132767));
        setPreferredSize(new java.awt.Dimension(1000, 800));

        jPanel4.setBackground(new java.awt.Color(248, 250, 252));
        jPanel4.setForeground(new java.awt.Color(0, 0, 0));

        jPanel5.setBackground(new java.awt.Color(248, 250, 252));
        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        jPanel5.setLayout(new java.awt.BorderLayout(0, 2));

        jLabel11.setFont(new java.awt.Font("Urbanist ExtraBold", 0, 18)); // NOI18N
        jLabel11.setText("Barang Keluar");
        jPanel5.add(jLabel11, java.awt.BorderLayout.PAGE_START);

        jLabel20.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(142, 157, 166));
        jLabel20.setText("Kelola dan Input Barang.");
        jPanel5.add(jLabel20, java.awt.BorderLayout.PAGE_END);

        jPanel6.setBackground(new java.awt.Color(248, 250, 252));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        jLabel21.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel21.setText("Informasi Barang");

        jLabel22.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(142, 157, 166));

        jLabel23.setText("Nomor Pengeluaran");
        jLabel23.setPreferredSize(new java.awt.Dimension(60, 16));

        jLabel24.setText("Nama Barang");

        jLabel25.setText("Tanggal Pengiriman");

        btnKirim.setBackground(new java.awt.Color(39, 174, 96));
        btnKirim.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        btnKirim.setForeground(new java.awt.Color(255, 255, 255));
        btnKirim.setText("Simpan");
        btnKirim.setBorderPainted(false);
        btnKirim.addActionListener(this::btnKirimActionPerformed);

        labelNoPengeluaran.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelNoPengeluaran.setText("DO-2026-001");

        inputNamaBarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        inputNamaBarang.addActionListener(this::inputNamaBarangActionPerformed);

        labelSatuan.setText("-");

        jLabel32.setText("Qty");

        jLabel33.setText("Tujuan");

        Lokasi.setText("Lokasi");

        inputLokasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3" }));
        inputLokasi.addActionListener(this::inputLokasiActionPerformed);

        jLabel35.setText(" Penerima Requestor");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelNoPengeluaran, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputTanggalKirim, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(15, 15, 15))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(inputPenerima, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(inputTujuan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                                .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputNamaBarang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(inputLokasi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(inputQty)
                                    .addComponent(Lokasi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))))
                        .addGap(1, 1, 1)
                        .addComponent(labelSatuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnKirim)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addGap(2, 2, 2)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelNoPengeluaran)
                    .addComponent(inputNamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel32))
                .addGap(6, 6, 6)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputTanggalKirim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelSatuan)
                            .addComponent(inputLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33)
                            .addComponent(Lokasi))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputTujuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputPenerima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnKirim)
                .addContainerGap(59, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel8.setBackground(new java.awt.Color(248, 250, 252));
        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        jLabel29.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel29.setText("Table Barang Keluar");

        jLabel30.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(142, 157, 166));
        jLabel30.setText("List data Outbound.");

        jLabel31.setText("Cari data:");

        TSearchBarangKeluar.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TSearchBarangKeluar.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TSearchBarangKeluar.setToolTipText("");
        TSearchBarangKeluar.setActionCommand("<Not Set>");
        TSearchBarangKeluar.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(225, 228, 231), 1, true), javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        TSearchBarangKeluar.addActionListener(this::TSearchBarangKeluarActionPerformed);
        TSearchBarangKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TSearchBarangKeluarKeyReleased(evt);
            }
        });

        TBarangKeluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        TBarangKeluar.setShowGrid(true);
        jScrollPane3.setViewportView(TBarangKeluar);

        BPBarangKeluar.setText("Previous");
        BPBarangKeluar.setBorderPainted(false);
        BPBarangKeluar.setEnabled(false);
        BPBarangKeluar.addActionListener(this::BPBarangKeluarActionPerformed);

        BNBarangKeluar.setBackground(new java.awt.Color(0, 153, 204));
        BNBarangKeluar.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        BNBarangKeluar.setForeground(new java.awt.Color(255, 255, 255));
        BNBarangKeluar.setText("Next");
        BNBarangKeluar.setBorderPainted(false);
        BNBarangKeluar.addActionListener(this::BNBarangKeluarActionPerformed);

        LPBarangMasuk.setFont(new java.awt.Font("Inter", 0, 10)); // NOI18N
        LPBarangMasuk.setForeground(new java.awt.Color(142, 157, 166));
        LPBarangMasuk.setText("5 dari 10 data ditampilkan");

        BSearchBarangKeluar.setBackground(new java.awt.Color(0, 153, 204));
        BSearchBarangKeluar.setForeground(new java.awt.Color(255, 255, 255));
        BSearchBarangKeluar.setText("Cari");
        BSearchBarangKeluar.addActionListener(this::BSearchBarangKeluarActionPerformed);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 908, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(LPBarangMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(BPBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BNBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TSearchBarangKeluar)
                .addGap(11, 11, 11)
                .addComponent(BSearchBarangKeluar)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel30)
                .addGap(15, 15, 15)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TSearchBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(BSearchBarangKeluar))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LPBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BNBarangKeluar)
                    .addComponent(BPBarangKeluar))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel9, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1002, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1427, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(25, 25, 25)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1002, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1427, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void TSearchBarangKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TSearchBarangKeluarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TSearchBarangKeluarActionPerformed

    private void TSearchBarangKeluarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TSearchBarangKeluarKeyReleased
        // Search ditangani DocumentListener + debounce di constructor
    }//GEN-LAST:event_TSearchBarangKeluarKeyReleased

    private void BPBarangKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BPBarangKeluarActionPerformed
        if (currentPage > 1) {

            currentPage--;

            loadDataOutbound();
        }
    }//GEN-LAST:event_BPBarangKeluarActionPerformed

    private void BNBarangKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BNBarangKeluarActionPerformed
        if (currentPage < totalPage) {

            currentPage++;

            loadDataOutbound();
        }
    }//GEN-LAST:event_BNBarangKeluarActionPerformed

    private void BSearchBarangKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BSearchBarangKeluarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BSearchBarangKeluarActionPerformed

    private void inputLokasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputLokasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputLokasiActionPerformed

    private void inputNamaBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputNamaBarangActionPerformed
        if (inputNamaBarang.getSelectedItem() == null) {
            return;
        }

        String namaBarang = inputNamaBarang.getSelectedItem().toString();
        Integer itemId = itemMap.get(namaBarang);
        if (itemId != null) {
            loadLokasiByItem(itemId);
        }
    }//GEN-LAST:event_inputNamaBarangActionPerformed

    private void btnKirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKirimActionPerformed
       Connection conn = null;

    try {

        /*
         ============================
         VALIDASI
         ============================
         */
        if (inputTujuan.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Tujuan wajib diisi"
            );
            return;
        }

        if (inputPenerima.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Penerima requestor wajib diisi"
            );
            return;
        }
        int qty =
                Integer.parseInt(
                        inputQty
                                .getValue()
                                .toString()
                );

        if (qty <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Qty harus lebih dari 0"
            );

            return;
        }

        /*
         ============================
         AMBIL DATA FORM
         ============================
         */
        String namaBarang =
                inputNamaBarang
                        .getSelectedItem()
                        .toString();

        String lokasiDisplay =
                inputLokasi
                        .getSelectedItem()
                        .toString();

        int itemId =
                itemMap.get(namaBarang);

        int locationId =
                locationMap.get(lokasiDisplay);

        int currentStock =
                getCurrentStock(
                        itemId,
                        locationId
                );

        /*
         VALIDASI STOCK
         */
        if (qty > currentStock) {

            JOptionPane.showMessageDialog(
                    this,
                    "Qty melebihi stock tersedia"
            );

            return;
        }

        String nomorPengeluaran =
                labelNoPengeluaran.getText();

        String tujuan =
                inputTujuan.getText();

        String penerima =inputPenerima.getText();
        String noOrder = generateNoOrder();
        String noSuratJalan =generateNoSuratJalan();
        String catatan = null;
        String tanggalKeluar =
                new SimpleDateFormat(
                        "yyyy-MM-dd"
                ).format(
                        inputTanggalKirim.getDate()
                );

        /*
         ============================
         DATABASE
         ============================
         */
        conn =
                DatabaseConfig.getConnection();

        conn.setAutoCommit(false);

        /*
         ============================
         INSERT OUTBOUND
         ============================
         */
        String outboundSql = """
                INSERT INTO outbound_transactions
                (
                    nomor_pengeluaran,
                    no_order,
                    tanggal_keluar,
                    tujuan,
                    penerima_requestor,
                    item_id,
                    qty,
                    no_surat_jalan,
                    catatan
                )
                VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        PreparedStatement psOutbound =
                conn.prepareStatement(outboundSql);

        psOutbound.setString(
                1,
                nomorPengeluaran
        );

        psOutbound.setString(
                2,
                noOrder
        );

        psOutbound.setString(
                3,
                tanggalKeluar
        );

        psOutbound.setString(
                4,
                tujuan
        );

        psOutbound.setString(
                5,
                penerima
        );

        psOutbound.setInt(
                6,
                itemId
        );

        psOutbound.setInt(
                7,
                qty
        );

        psOutbound.setString(
                8,
                noSuratJalan
        );

        psOutbound.setNull(
                9,
                java.sql.Types.VARCHAR
        );

        psOutbound.executeUpdate();

        /*
         ============================
         UPDATE INVENTORY
         ============================
         */
        int stockSesudah =
                currentStock - qty;

        String updateInventorySql = """
                UPDATE inventory
                SET stok_terkini = ?
                WHERE item_id = ?
                AND location_id = ?
                """;

        PreparedStatement psUpdate =
                conn.prepareStatement(
                        updateInventorySql
                );

        psUpdate.setInt(
                1,
                stockSesudah
        );

        psUpdate.setInt(
                2,
                itemId
        );

        psUpdate.setInt(
                3,
                locationId
        );

        psUpdate.executeUpdate();

        /*
         ============================
         INSERT STOCK HISTORY
         ============================
         */
        String historySql = """
                INSERT INTO stock_history
                (
                    jenis_transaksi,
                    nomor_referensi,
                    item_id,
                    location_id,
                    qty,
                    stock_sebelum,
                    stock_sesudah
                )
                VALUES
                (?, ?, ?, ?, ?, ?, ?)
                """;

        PreparedStatement psHistory =
                conn.prepareStatement(historySql);

        psHistory.setString(
                1,
                "outbound"
        );

        psHistory.setString(
                2,
                nomorPengeluaran
        );

        psHistory.setInt(
                3,
                itemId
        );

        psHistory.setInt(
                4,
                locationId
        );

        psHistory.setInt(
                5,
                qty
        );

        psHistory.setInt(
                6,
                currentStock
        );

        psHistory.setInt(
                7,
                stockSesudah
        );

        psHistory.executeUpdate();

        /*
         ============================
         COMMIT
         ============================
         */
        conn.commit();
        clearForm();
        JOptionPane.showMessageDialog(
                this,
                "Barang keluar berhasil"
        );
        
        // Nomor baru, dropdown barang/lokasi (stok berubah), dan tabel dimuat ulang async
        refresh();

    } catch (Exception e) {

        e.printStackTrace();

        try {

            if (conn != null) {

                conn.rollback();
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }

        JOptionPane.showMessageDialog(
                this,
               "Transaction gagal"
        );
    } finally {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }//GEN-LAST:event_btnKirimActionPerformed
private String generateNoOrder() {
    return generateNomorDokumen("REQ-", "REQ-ERROR");
}

    private String generateNoSuratJalan() {
        return generateNomorDokumen("SJ-", "SJ-ERROR");
    }

    /** Format: PREFIX + tahun + "-" + (jumlah outbound + 1), contoh REQ-2026-001. */
    private String generateNomorDokumen(String prefix, String fallback) {
        try {
            int nomor = TableUtil.count("SELECT COUNT(*) + 1 AS total FROM outbound_transactions");
            String tahun = new SimpleDateFormat("yyyy").format(new java.util.Date());
            return prefix + tahun + "-" + String.format("%03d", nomor);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fallback;
    }

    private void clearForm() {
        /*
         ============================
         CLEAR TEXTFIELD
         ============================
         */
        inputTujuan.setText("");
        inputPenerima.setText("");
        /*
         ============================
         RESET QTY
         ============================
         */
        inputQty.setValue(0);

        /*
         ============================
         RESET DATE
         ============================
         */
        inputTanggalKirim.setDate(
                new java.util.Date()
        );

        /*
         ============================
         RESET COMBOBOX
         ============================
         */
        if (inputNamaBarang.getItemCount() > 0) {
            inputNamaBarang.setSelectedIndex(0);
        }
        if (inputLokasi.getItemCount() > 0) {
            inputLokasi.setSelectedIndex(0);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BNBarangKeluar;
    private javax.swing.JButton BPBarangKeluar;
    private javax.swing.JButton BSearchBarangKeluar;
    private javax.swing.JLabel LPBarangMasuk;
    private javax.swing.JLabel Lokasi;
    private javax.swing.JTable TBarangKeluar;
    private javax.swing.JTextField TSearchBarangKeluar;
    private javax.swing.JButton btnKirim;
    private javax.swing.JComboBox<String> inputLokasi;
    private javax.swing.JComboBox<String> inputNamaBarang;
    private javax.swing.JTextField inputPenerima;
    private javax.swing.JSpinner inputQty;
    private com.toedter.calendar.JDateChooser inputTanggalKirim;
    private javax.swing.JTextField inputTujuan;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labelNoPengeluaran;
    private javax.swing.JLabel labelSatuan;
    // End of variables declaration//GEN-END:variables
}