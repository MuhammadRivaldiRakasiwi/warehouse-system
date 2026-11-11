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
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.List;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
/**
 *
 * @author ndesc
 */
public class BarangMasukPanel extends javax.swing.JPanel {

    private static final String[] KOLOM =
            {"ID", "Supplier", "No Penerimaan", "Lokasi", "Barang", "Satuan", "Quantity"};

    private static final String SELECT_INBOUND = """
            SELECT it.id, s.nama_supplier, it.nomor_penerimaan, l.kode_lokasi,
                   i.nama_item, i.satuan, it.qty
            FROM inbound_transactions it
            INNER JOIN suppliers s ON it.supplier_id = s.id
            INNER JOIN items i ON it.item_id = i.id
            INNER JOIN locations l ON it.location_id = l.id
            """;

    private static final TableUtil.RowMapper MAP_INBOUND = rs -> new Object[]{
        rs.getInt("id"),
        rs.getString("nama_supplier"),
        rs.getString("nomor_penerimaan"),
        rs.getString("kode_lokasi"),
        rs.getString("nama_item"),
        rs.getString("satuan"),
        rs.getString("qty")
    };

    private final HashMap<String, Integer> supplierMap = new HashMap<>();
    private final HashMap<String, Integer> itemMap = new HashMap<>();
    private final HashMap<String, Integer> locationMap = new HashMap<>();
    // kode_item -> {nama_item, satuan}; label diisi dari cache, tanpa query setiap ganti pilihan
    private final HashMap<String, String[]> itemInfoMap = new HashMap<>();

    // Search dijalankan 350 ms setelah user berhenti mengetik (bukan per huruf)
    private final Debouncer searchDebouncer = new Debouncer(350, this::cariBarangMasuk);
    // Nomor request terakhir: hasil query lama yang datang terlambat diabaikan
    private int requestTabel = 0;
    private int requestLookup = 0;
    private int requestNomor = 0;

    private int currentPage = 1;
    private final int dataPerPage = 10;
    private int totalPage = 0;

    /** Data dropdown (supplier, barang, lokasi) yang diambil dalam satu koneksi. */
    private record LookupData(List<Object[]> suppliers, List<Object[]> items, List<Object[]> locations) {}

    /**
     * Creates new form BarangMasukPanel
     */
    public BarangMasukPanel() {
        initComponents();
        setupUI();
        refresh();
    }

    /**
     * Memuat ulang semua data panel (nomor penerimaan, dropdown, tabel) secara async.
     * Dipanggil ContentFrame saat menu Barang Masuk dibuka lagi.
     */
    public void refresh() {
        generateNomorPenerimaan();
        loadLookups();
        loadDataInbound();
    }

    private void setupTableStyle() {
        TBarangMasuk.setForeground(new java.awt.Color(33, 37, 41));
        TBarangMasuk.setBackground(java.awt.Color.WHITE);
        TBarangMasuk.setSelectionForeground(java.awt.Color.WHITE);
        TBarangMasuk.setSelectionBackground(new java.awt.Color(0, 153, 204));
        TBarangMasuk.setGridColor(new java.awt.Color(230, 230, 230));
        TBarangMasuk.setRowHeight(31);
        TBarangMasuk.getTableHeader().setForeground(java.awt.Color.BLACK);
        TBarangMasuk.getTableHeader().setBackground(new java.awt.Color(245, 245, 245));
    }

    private void setupUI() {
        inputTanggalTerima.setDateFormatString("yyyy-MM-dd");
        inputTanggalTerima.getDateEditor().setEnabled(false);
        inputTanggalTerima.setDate(new java.util.Date());

        formatHarga();

        BSearchBarangMasuk.setVisible(false);
        TableUtil.disablePagination(BPBarangMasuk, BNBarangMasuk);

        // Search realtime (dengan debounce), termasuk saat paste
        TSearchBarangMasuk.getDocument().addDocumentListener(TableUtil.onChange(searchDebouncer::trigger));

        setupTableStyle();
    }

    public javax.swing.JScrollPane asScrollable() {
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(this);
        sp.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return sp;
    }

    /** Memuat halaman data barang masuk saat ini (async). */
    public final void loadDataInbound() {
        if (!TSearchBarangMasuk.getText().trim().isEmpty()) {
            cariBarangMasuk();
            return;
        }
        final int request = ++requestTabel;
        final int page = currentPage;
        UiWorker.run(
                () -> TableUtil.queryPage(
                        "SELECT COUNT(*) FROM inbound_transactions",
                        SELECT_INBOUND + " ORDER BY it.id DESC LIMIT ? OFFSET ?",
                        page, dataPerPage, MAP_INBOUND),
                result -> {
                    if (request != requestTabel) {
                        return;
                    }
                    currentPage = result.page();
                    totalPage = result.totalPages();
                    tampilkanTabel(result.rows());
                    LPBarangMasuk.setText(TableUtil.pageLabel(currentPage, totalPage));
                    TableUtil.updatePagination(BPBarangMasuk, BNBarangMasuk, currentPage, totalPage);
                },
                e -> tampilkanError(request == requestTabel, "Gagal memuat data: " + e.getMessage()));
    }

    private void cariBarangMasuk() {
        String keyword = TSearchBarangMasuk.getText().trim();
        if (keyword.isEmpty()) {
            loadDataInbound();
            return;
        }
        final int request = ++requestTabel;
        final String pola = TableUtil.like(keyword);
        UiWorker.run(
                () -> TableUtil.query(
                        SELECT_INBOUND + """
                         WHERE s.nama_supplier LIKE ?
                            OR it.nomor_penerimaan LIKE ?
                            OR i.nama_item LIKE ?
                            OR l.kode_lokasi LIKE ?
                            OR i.satuan LIKE ?
                            OR CAST(it.qty AS CHAR) LIKE ?
                         ORDER BY it.id DESC
                         LIMIT 50
                        """,
                        MAP_INBOUND, pola, pola, pola, pola, pola, pola),
                rows -> {
                    if (request != requestTabel) {
                        return;
                    }
                    tampilkanTabel(rows);
                    LPBarangMasuk.setText("Hasil pencarian: " + rows.size());
                    TableUtil.disablePagination(BPBarangMasuk, BNBarangMasuk);
                },
                e -> tampilkanError(request == requestTabel, e.getMessage()));
    }

    private void tampilkanTabel(List<Object[]> rows) {
        TBarangMasuk.setModel(TableUtil.readOnlyModel(KOLOM, rows));
        TableUtil.hideColumn(TBarangMasuk, 0);
    }

    private void tampilkanError(boolean masihRelevan, String pesan) {
        if (masihRelevan) {
            JOptionPane.showMessageDialog(javax.swing.SwingUtilities.getWindowAncestor(this), pesan);
        }
    }

    private void formatHarga() {
        inputHarga.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        AbstractDocument document = (AbstractDocument) inputHarga.getDocument();

        document.setDocumentFilter(new DocumentFilter() {

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                replace(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String oldText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = oldText.substring(0, offset)
                        + (text == null ? "" : text)
                        + oldText.substring(offset + length);

                /*
                 hanya angka + titik desimal
                 contoh valid: 100, 1000, 1000.50
                 */
                if (newText.matches("^\\d*(\\.\\d{0,2})?$")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
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
        jLabel10 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        labelNomorPenerimaan = new javax.swing.JLabel();
        inputSupplier = new javax.swing.JComboBox<>();
        inputTanggalTerima = new com.toedter.calendar.JDateChooser();
        inputKodeBarang = new javax.swing.JComboBox<>();
        labelSatuan = new javax.swing.JLabel();
        labelNamaBarang = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        inputHarga = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        inputKondisiBarang = new javax.swing.JComboBox<>();
        inputLokasi = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        inputQty = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        TSearchBarangMasuk = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        TBarangMasuk = new javax.swing.JTable();
        BPBarangMasuk = new javax.swing.JButton();
        BNBarangMasuk = new javax.swing.JButton();
        LPBarangMasuk = new javax.swing.JLabel();
        BSearchBarangMasuk = new javax.swing.JButton();

        setBackground(new java.awt.Color(248, 250, 252));
        setForeground(new java.awt.Color(0, 0, 0));

        jPanel4.setBackground(new java.awt.Color(248, 250, 252));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        jPanel4.setLayout(new java.awt.BorderLayout(0, 2));

        jLabel10.setFont(new java.awt.Font("Urbanist ExtraBold", 0, 18)); // NOI18N
        jLabel10.setText("Barang Masuk");
        jPanel4.add(jLabel10, java.awt.BorderLayout.PAGE_START);

        jLabel18.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(142, 157, 166));
        jLabel18.setText("Kelola dan Input Barang.");
        jPanel4.add(jLabel18, java.awt.BorderLayout.PAGE_END);

        jPanel5.setBackground(new java.awt.Color(248, 250, 252));
        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        jLabel19.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel19.setText("Informasi Barang");

        jLabel20.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(142, 157, 166));

        jLabel21.setText("Nomor Penerimaan");
        jLabel21.setPreferredSize(new java.awt.Dimension(60, 16));

        jLabel22.setText("Kode Barang");

        jLabel23.setText("Tanggal Penerimaan");

        jLabel24.setText("Nama Barang");

        jLabel25.setText("Supplier");

        jLabel26.setText("Satuan");

        btnSimpan.setBackground(new java.awt.Color(39, 174, 96));
        btnSimpan.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");
        btnSimpan.setBorderPainted(false);
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);

        labelNomorPenerimaan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelNomorPenerimaan.setText("GR-2026-001");

        inputSupplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        inputKodeBarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        inputKodeBarang.addActionListener(this::inputKodeBarangActionPerformed);

        labelSatuan.setText("-");

        labelNamaBarang.setText("-");

        jLabel32.setText("Qty");

        jLabel33.setText("Harga");

        jLabel34.setText("Kondisi Barang");

        inputKondisiBarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baik", "Cacat", "Rusak", " " }));
        inputKondisiBarang.addActionListener(this::inputKondisiBarangActionPerformed);

        inputLokasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        inputLokasi.addActionListener(this::inputLokasiActionPerformed);

        jLabel35.setText("Lokasi");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(labelNomorPenerimaan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(inputTanggalTerima, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(inputHarga, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputSupplier, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(3, 3, 3))
                            .addComponent(inputLokasi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelSatuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(inputKondisiBarang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
                            .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputKodeBarang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelNamaBarang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputQty))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSimpan)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addGap(2, 2, 2)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelNomorPenerimaan)
                    .addComponent(inputKodeBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(inputTanggalTerima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelSatuan)))
                    .addComponent(labelNamaBarang, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inputHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputKondisiBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(btnSimpan)
                .addContainerGap())
        );

        jPanel5.add(jPanel6, java.awt.BorderLayout.CENTER);

        jPanel8.setBackground(new java.awt.Color(248, 250, 252));
        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        jLabel27.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel27.setText("Table Barang Masuk");

        jLabel28.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(142, 157, 166));
        jLabel28.setText("List data Inbound.");

        jLabel29.setText("Cari data:");

        TSearchBarangMasuk.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TSearchBarangMasuk.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TSearchBarangMasuk.setToolTipText("");
        TSearchBarangMasuk.setActionCommand("<Not Set>");
        TSearchBarangMasuk.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(225, 228, 231), 1, true), javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        TSearchBarangMasuk.addActionListener(this::TSearchBarangMasukActionPerformed);
        TSearchBarangMasuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TSearchBarangMasukKeyReleased(evt);
            }
        });

        TBarangMasuk.setModel(new javax.swing.table.DefaultTableModel(
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
        TBarangMasuk.setShowGrid(true);
        jScrollPane3.setViewportView(TBarangMasuk);

        BPBarangMasuk.setText("Previous");
        BPBarangMasuk.setBorderPainted(false);
        BPBarangMasuk.setEnabled(false);
        BPBarangMasuk.addActionListener(this::BPBarangMasukActionPerformed);

        BNBarangMasuk.setBackground(new java.awt.Color(0, 153, 204));
        BNBarangMasuk.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        BNBarangMasuk.setForeground(new java.awt.Color(255, 255, 255));
        BNBarangMasuk.setText("Next");
        BNBarangMasuk.setBorderPainted(false);
        BNBarangMasuk.addActionListener(this::BNBarangMasukActionPerformed);

        LPBarangMasuk.setFont(new java.awt.Font("Inter", 0, 10)); // NOI18N
        LPBarangMasuk.setForeground(new java.awt.Color(142, 157, 166));
        LPBarangMasuk.setText("5 dari 10 data ditampilkan");

        BSearchBarangMasuk.setBackground(new java.awt.Color(0, 153, 204));
        BSearchBarangMasuk.setForeground(new java.awt.Color(255, 255, 255));
        BSearchBarangMasuk.setText("Cari");
        BSearchBarangMasuk.addActionListener(this::BSearchBarangMasukActionPerformed);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 908, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(LPBarangMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(BPBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BNBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TSearchBarangMasuk)
                .addGap(11, 11, 11)
                .addComponent(BSearchBarangMasuk)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel28)
                .addGap(15, 15, 15)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TSearchBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(BSearchBarangMasuk))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LPBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BNBarangMasuk)
                    .addComponent(BPBarangMasuk))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel9, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1002, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1427, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(25, 25, 25)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
 Connection conn = null;

          try {

              /*
               =====================================
               VALIDASI
               =====================================
               */
              int qty = Integer.parseInt(
                      inputQty.getValue().toString()
              );
              if (qty <= 0) {
                   JOptionPane.showMessageDialog(
                            javax.swing.SwingUtilities.getWindowAncestor(this),
                            "Qty harus lebih dari 0"
                    );
                  return;
              }

              if (inputHarga.getText().trim().isEmpty()) {
                  JOptionPane.showMessageDialog(
                          javax.swing.SwingUtilities.getWindowAncestor(this),
                          "Harga wajib diisi"
                  );
                  return;
              }

              /*
               =====================================
               AMBIL DATA FORM
               =====================================
               */
              double harga =
                      Double.parseDouble(inputHarga.getText());

              String kondisiBarang =
                      inputKondisiBarang
                              .getSelectedItem()
                              .toString();
              String supplierName =
                      inputSupplier.getSelectedItem().toString();
              String kodeBarang =
                      inputKodeBarang.getSelectedItem().toString();
              String lokasiName =
                      inputLokasi.getSelectedItem().toString();
              String catatan = null;
//                      inputCatatan.getText().trim();
              int supplierId =
                      supplierMap.get(supplierName);
              int itemId =
                      itemMap.get(kodeBarang);
              int locationId =
                      locationMap.get(lokasiName);
              String nomorPenerimaan =
                      labelNomorPenerimaan.getText();
              SimpleDateFormat sdf =
                      new SimpleDateFormat("yyyy-MM-dd");
              String tanggalTerima =
                      sdf.format(inputTanggalTerima.getDate());
              String satuan =
                      labelSatuan.getText();
              String status = "RECEIVED";

              /*
               =====================================
               DATABASE CONNECTION
               =====================================
               */
              conn = DatabaseConfig.getConnection();

              /*
               =====================================
               MATIKAN AUTO COMMIT
               =====================================
               */
              conn.setAutoCommit(false);

              /*
               =====================================
               GENERATE BATCH LOT
               =====================================
               */
              String noBatchLot =
                      generateBatchLot(conn);

              /*
               =====================================
               1. INSERT INBOUND TRANSACTION
               =====================================
               */
              String inboundSql = """
                      INSERT INTO inbound_transactions
                      (
                          nomor_penerimaan,
                          tanggal_terima,
                          supplier_id,
                          item_id,
                          qty,
                          satuan,
                          harga_satuan,
                          no_batch_lot,
                          kondisi_barang,
                          location_id,
                          catatan,
                          status
                      )
                      VALUES
                      (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                      """;

              PreparedStatement psInbound =
                      conn.prepareStatement(inboundSql);
              psInbound.setString(1, nomorPenerimaan);
              psInbound.setString(2, tanggalTerima);
              psInbound.setInt(3, supplierId);
              psInbound.setInt(4, itemId);
              psInbound.setInt(5, qty);
              psInbound.setString(6, satuan);
              psInbound.setDouble(7, harga);
              psInbound.setString(8, noBatchLot);
              psInbound.setString(9, kondisiBarang);
              psInbound.setInt(10, locationId);

              /*
               jika kosong -> NULL
               */
              if (catatan == null) {
                  psInbound.setNull(11, java.sql.Types.VARCHAR);
              } else {
                  psInbound.setString(11, catatan);
              }
              psInbound.setString(12, status);
              psInbound.executeUpdate();
              /*
               =====================================
               2. CEK INVENTORY
               =====================================
               */
              String checkInventorySql = """
                      SELECT id, stok_terkini
                      FROM inventory
                      WHERE item_id = ?
                      AND location_id = ?
                      """;

              PreparedStatement psCheck =
                      conn.prepareStatement(checkInventorySql);

              psCheck.setInt(1, itemId);

              psCheck.setInt(2, locationId);

              ResultSet rs =
                      psCheck.executeQuery();

              int stockSebelum = 0;

              int stockSesudah = 0;

              /*
               =====================================
               JIKA INVENTORY SUDAH ADA
               =====================================
               */
              if (rs.next()) {

                  stockSebelum =
                          rs.getInt("stok_terkini");

                  stockSesudah =
                          stockSebelum + qty;

                  String updateInventorySql = """
                          UPDATE inventory
                          SET stok_terkini = ?
                          WHERE item_id = ?
                          AND location_id = ?
                          """;

                  PreparedStatement psUpdateInventory =
                          conn.prepareStatement(updateInventorySql);

                  psUpdateInventory.setInt(1, stockSesudah);
                  psUpdateInventory.setInt(2, itemId);
                  psUpdateInventory.setInt(3, locationId);
                  psUpdateInventory.executeUpdate();

              } else {

                  /*
                   =====================================
                   JIKA INVENTORY BELUM ADA
                   =====================================
                   */
                  stockSebelum = 0;

                  stockSesudah = qty;

                  String insertInventorySql = """
                          INSERT INTO inventory
                          (
                              item_id,
                              location_id,
                              stok_terkini
                          )
                          VALUES
                          (?, ?, ?)
                          """;

                  PreparedStatement psInsertInventory =
                          conn.prepareStatement(insertInventorySql);

                  psInsertInventory.setInt(1, itemId);

                  psInsertInventory.setInt(2, locationId);

                  psInsertInventory.setInt(3, stockSesudah);

                  psInsertInventory.executeUpdate();
              }

              /*
               =====================================
               3. INSERT STOCK HISTORY
               =====================================
               */
              String stockHistorySql = """
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
                      conn.prepareStatement(stockHistorySql);

              psHistory.setString(1, "inbound");
              psHistory.setString(2, nomorPenerimaan);
              psHistory.setInt(3, itemId);
              psHistory.setInt(4, locationId);
              psHistory.setInt(5, qty);
              psHistory.setInt(6, stockSebelum);
              psHistory.setInt(7, stockSesudah);
              psHistory.executeUpdate();

              /*
               =====================================
               COMMIT TRANSACTION
               =====================================
               */
              conn.commit();

              JOptionPane.showMessageDialog(
                      javax.swing.SwingUtilities.getWindowAncestor(this),
                      "Barang masuk berhasil disimpan\n"
                      + "Batch : " + noBatchLot
              );
              clearForm(); // termasuk generate ulang nomor penerimaan
              loadDataInbound();

          } catch (Exception e) {

              e.printStackTrace();

              /*
               =====================================
               ROLLBACK
               =====================================
               */
              try {
                  if (conn != null) {
                      conn.rollback();
                  }
              } catch (SQLException ex) {
                  ex.printStackTrace();
              }
              JOptionPane.showMessageDialog(
                      javax.swing.SwingUtilities.getWindowAncestor(this),
                      "Transaction gagal"
              );
          } finally {
              /*
               =====================================
               AKTIFKAN AUTO COMMIT
               =====================================
               */
              try {
                  if (conn != null) {
                      conn.setAutoCommit(true);
                      conn.close();
                  }
              } catch (SQLException e) {
                  e.printStackTrace();
              }
          }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void TSearchBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TSearchBarangMasukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TSearchBarangMasukActionPerformed

    private void TSearchBarangMasukKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TSearchBarangMasukKeyReleased
        // Search ditangani DocumentListener + debounce di setupUI()
    }//GEN-LAST:event_TSearchBarangMasukKeyReleased

    private void BPBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BPBarangMasukActionPerformed
        if (currentPage > 1) {

            currentPage--;

            loadDataInbound();
        }
    }//GEN-LAST:event_BPBarangMasukActionPerformed

    private void BNBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BNBarangMasukActionPerformed
        if (currentPage < totalPage) {

            currentPage++;

            loadDataInbound();
        }
    }//GEN-LAST:event_BNBarangMasukActionPerformed

    private void BSearchBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BSearchBarangMasukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BSearchBarangMasukActionPerformed

    private void inputKondisiBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKondisiBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputKondisiBarangActionPerformed

    private void inputLokasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputLokasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputLokasiActionPerformed

    private void inputKodeBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKodeBarangActionPerformed
        // Cegah null saat pertama load / saat dropdown dikosongkan
        if (inputKodeBarang.getSelectedItem() == null) {
            return;
        }
        // Nama & satuan diambil dari cache (dimuat bersama dropdown), tanpa query ke database
        String[] info = itemInfoMap.get(inputKodeBarang.getSelectedItem().toString());
        if (info != null) {
            labelNamaBarang.setText(info[0]);
            labelSatuan.setText(info[1]);
        }
    }//GEN-LAST:event_inputKodeBarangActionPerformed
/** Membuat nomor penerimaan berikutnya (async). Tombol Simpan dinonaktifkan sampai nomor siap. */
public void generateNomorPenerimaan() {
    final int request = ++requestNomor;
    btnSimpan.setEnabled(false);
    UiWorker.run(
            () -> TableUtil.count("SELECT COUNT(*) + 1 AS total FROM inbound_transactions"),
            nomor -> {
                if (request != requestNomor) {
                    return;
                }
                labelNomorPenerimaan.setText(String.format("GR-2026-%03d", nomor));
                btnSimpan.setEnabled(true);
            },
            e -> {
                if (request != requestNomor) {
                    return;
                }
                btnSimpan.setEnabled(true);
                e.printStackTrace();
            });
}

/** Memuat ulang dropdown supplier, barang, dan lokasi (satu koneksi, async). */
public void loadSupplier() {
    loadLookups();
}

/** Memuat ulang dropdown supplier, barang, dan lokasi (satu koneksi, async). */
public void loadItem() {
    loadLookups();
}

/** Memuat ulang dropdown supplier, barang, dan lokasi (satu koneksi, async). */
public void loadLocation() {
    loadLookups();
}

private void loadLookups() {
    final int request = ++requestLookup;
    UiWorker.run(() -> {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return new LookupData(
                    queryRows(conn, "SELECT id, nama_supplier FROM suppliers",
                            rs -> new Object[]{rs.getInt("id"), rs.getString("nama_supplier")}),
                    queryRows(conn, "SELECT id, kode_item, nama_item, satuan FROM items",
                            rs -> new Object[]{rs.getInt("id"), rs.getString("kode_item"),
                                rs.getString("nama_item"), rs.getString("satuan")}),
                    queryRows(conn, "SELECT id, kode_lokasi FROM locations",
                            rs -> new Object[]{rs.getInt("id"), rs.getString("kode_lokasi")}));
        }
    }, data -> {
        if (request != requestLookup) {
            return;
        }
        supplierMap.clear();
        inputSupplier.removeAllItems();
        for (Object[] r : data.suppliers()) {
            supplierMap.put((String) r[1], (Integer) r[0]);
            inputSupplier.addItem((String) r[1]);
        }

        // Cache diisi dulu, karena addItem() memicu inputKodeBarangActionPerformed
        itemMap.clear();
        itemInfoMap.clear();
        for (Object[] r : data.items()) {
            itemMap.put((String) r[1], (Integer) r[0]);
            itemInfoMap.put((String) r[1], new String[]{(String) r[2], (String) r[3]});
        }
        inputKodeBarang.removeAllItems();
        for (Object[] r : data.items()) {
            inputKodeBarang.addItem((String) r[1]);
        }

        locationMap.clear();
        inputLokasi.removeAllItems();
        for (Object[] r : data.locations()) {
            locationMap.put((String) r[1], (Integer) r[0]);
            inputLokasi.addItem((String) r[1]);
        }
    }, Exception::printStackTrace);
}

private static List<Object[]> queryRows(Connection conn, String sql, TableUtil.RowMapper mapper)
        throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        List<Object[]> rows = new java.util.ArrayList<>();
        while (rs.next()) {
            rows.add(mapper.map(rs));
        }
        return rows;
    }
}

private void clearForm() {

    // Reset supplier, kode barang, lokasi ke item pertama (jika ada)
    if (inputSupplier.getItemCount() > 0) {
        inputSupplier.setSelectedIndex(0);
    }
    if (inputKodeBarang.getItemCount() > 0) {
        inputKodeBarang.setSelectedIndex(0);
    }
    if (inputLokasi.getItemCount() > 0) {
        inputLokasi.setSelectedIndex(0);
    }
    // Reset qty
    inputQty.setValue(0);
     // Reset Harga
    inputHarga.setText("");
    // Reset tanggal ke hari ini
    inputTanggalTerima.setDate(new java.util.Date());
    // Reset label nama barang & satuan
    labelNamaBarang.setText("-");
    labelSatuan.setText("-");
    // Generate ulang nomor penerimaan
    generateNomorPenerimaan();
}

private String generateBatchLot(Connection conn)
        throws SQLException {

    /*
     =====================================
     AMBIL 2 DIGIT TAHUN SEKARANG
     =====================================
     */
    String tahun =
            new SimpleDateFormat("yy")
                    .format(new java.util.Date());

    /*
     =====================================
     HITUNG JUMLAH BATCH TAHUN INI
     =====================================
     */
    String sql = """
            SELECT COUNT(*) + 1 as total
            FROM inbound_transactions
            WHERE no_batch_lot LIKE ?
            """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "BATCH-" + tahun + "%");
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int nomor = rs.getInt("total");
                /*
                 FORMAT: 01, 02, 03
                 */
                String urutan = String.format("%02d", nomor);
                return "BATCH-" + tahun + urutan;
            }
        }
    }

    return "BATCH-" + tahun + "01";
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BNBarangMasuk;
    private javax.swing.JButton BPBarangMasuk;
    private javax.swing.JButton BSearchBarangMasuk;
    private javax.swing.JLabel LPBarangMasuk;
    private javax.swing.JTable TBarangMasuk;
    private javax.swing.JTextField TSearchBarangMasuk;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JTextField inputHarga;
    private javax.swing.JComboBox<String> inputKodeBarang;
    private javax.swing.JComboBox<String> inputKondisiBarang;
    private javax.swing.JComboBox<String> inputLokasi;
    private javax.swing.JSpinner inputQty;
    private javax.swing.JComboBox<String> inputSupplier;
    private com.toedter.calendar.JDateChooser inputTanggalTerima;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labelNamaBarang;
    private javax.swing.JLabel labelNomorPenerimaan;
    private javax.swing.JLabel labelSatuan;
    // End of variables declaration//GEN-END:variables
}