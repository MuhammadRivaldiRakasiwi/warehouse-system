/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.warehouse.system.view;

import com.mycompany.warehouse.system.DatabaseConfig;
import com.mycompany.warehouse.system.service.DashboardService;
import static com.mycompany.warehouse.system.view.DashboardAdmin.instance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ndesc
 */
public class DashboardAdmin extends javax.swing.JPanel {
 public static DashboardAdmin instance;
private int currentPage = 1;
private final int dataPerPage = 10;
private int totalData = 0;
private int totalPage = 0;
    /**
     * Creates new form DashboardAdmin
     */
    public DashboardAdmin() {
        initComponents();
         instance = this;

        // Sembunyikan tombol Cari (search sudah realtime)
        BSearchActivity.setVisible(false);

        // Setup search realtime untuk Inventory
        TSearchInventory.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { cariInventory(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { cariInventory(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { cariInventory(); }
        });
         
        // Load data di background agar UI tidak freeze
        new javax.swing.SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                hitungTotalData();
                loadDataAktifitas();
                loadDataInventory();
                loadDataCount();
                return null;
            }
        }.execute();
    }
    public final void loadDataCount(){
         LUser.setText(String.valueOf(DashboardService.getTotalUsers()));
        LItem.setText(String.valueOf(DashboardService.getTotalItems()));
        LSupplier.setText(String.valueOf(DashboardService.getTotalSuppliers()));
        LLocation.setText(String.valueOf(DashboardService.getTotalLocations()));
        LBarangMasuk.setText(String.valueOf(DashboardService.getTotalItemsMasuk()));
        LBarangKeluar.setText(String.valueOf(DashboardService.getTotalItemsKeluar()));
    }
    public final void loadDataAktifitas() { 
        updatePaginationButton();

            LPActivity.setText(
                "Page " + currentPage + " / " + totalPage
            );
            DefaultTableModel model = new DefaultTableModel() {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            model.addColumn("ID");
            model.addColumn("Jam");
            model.addColumn("Aktifitas");
            model.addColumn("Barang");
            model.addColumn("Lokasi");
            model.addColumn("Stok Sebelum");
            model.addColumn("Stok Sesudah");

            int offset = (currentPage - 1) * dataPerPage;

            String sql = """
                SELECT sh.id,
                       sh.waktu_transaksi,
                       sh.jenis_transaksi,
                       i.nama_item,
                       l.kode_lokasi,
                       sh.stock_sebelum,
                       sh.stock_sesudah
                FROM stock_history sh
                INNER JOIN items i ON sh.item_id = i.id
                INNER JOIN locations l ON sh.location_id = l.id
                ORDER BY sh.id DESC
                LIMIT ? OFFSET ?
                """;

            try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
            ) {

                ps.setInt(1, dataPerPage);
                ps.setInt(2, offset);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("waktu_transaksi"),
                        rs.getString("jenis_transaksi"),
                        rs.getString("nama_item"),
                        rs.getString("kode_lokasi"),
                        rs.getString("stock_sebelum"),
                        rs.getString("stock_sesudah")
                    });
                }

                TAktifitas.setModel(model);

                TAktifitas.getColumnModel().getColumn(0).setMinWidth(0);
                TAktifitas.getColumnModel().getColumn(0).setMaxWidth(0);
                TAktifitas.getColumnModel().getColumn(0).setPreferredWidth(0);

            } catch (SQLException e) {

                JOptionPane.showMessageDialog(
                    null,
                    "Gagal memuat data: " + e.getMessage()
                );
            }
     }
    
    private void updatePaginationButton() {
   BNActivity.setOpaque(true);
        BNActivity.setContentAreaFilled(true);
        BPActivity.setOpaque(true);
        BPActivity.setContentAreaFilled(true);
     // PREVIOUS
           BPActivity.setEnabled(currentPage > 1);

           // NEXT
           BNActivity.setEnabled(currentPage < totalPage);

           // STYLE PREVIOUS
           if (BPActivity.isEnabled()) {

               BPActivity.setBackground(
                   new java.awt.Color(0,153,204)
               );

               BPActivity.setForeground(java.awt.Color.WHITE);

           } else {

               BPActivity.setBackground(
                   new java.awt.Color(220, 220, 220)
               );

               BPActivity.setForeground(
                   new java.awt.Color(120,120,120)
               );
           }

           // STYLE NEXT
           if (BNActivity.isEnabled()) {

               BNActivity.setBackground(
                   new java.awt.Color(0,153,204)
               );

               BNActivity.setForeground(java.awt.Color.WHITE);

           } else {

               BNActivity.setBackground(
                   new java.awt.Color(220,220,220)
               );

               BNActivity.setForeground(
                   new java.awt.Color(120,120,120)
               );
           }
}
    
    private void hitungTotalData() {

    String sql = "SELECT COUNT(*) AS total FROM stock_history";

    try (
        Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {

        if (rs.next()) {

            totalData = rs.getInt("total");

            totalPage = (int) Math.ceil(
                (double) totalData / dataPerPage
            );
        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
            this,
            e.getMessage()
        );
    }
}
         private void cariActivity() {

        String keyword = TSearchActivity.getText();

        DefaultTableModel model =
            (DefaultTableModel) TAktifitas.getModel();

        model.setRowCount(0);

        try {

            Connection conn = DatabaseConfig.getConnection();

            String sql = """
                 SELECT sh.id, sh.waktu_transaksi, sh.jenis_transaksi, i.nama_item,l.kode_lokasi, sh.stock_sebelum, sh.stock_sesudah 
                                  FROM stock_history sh 
                                  INNER JOIN items i ON sh.item_id = i.id
                                  INNER JOIN locations l ON sh.location_id = l.id
                                   WHERE sh.jenis_transaksi LIKE ?
                                     OR i.nama_item LIKE ?
                                     OR l.kode_lokasi LIKE ?
                                  ORDER BY sh.id DESC
            """;

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                model.addRow(new Object[] {
                        rs.getInt("id"), 
                      rs.getString("waktu_transaksi"),
                     rs.getString("jenis_transaksi"), 
                     rs.getString("nama_item"), 
                     rs.getString("kode_lokasi"), 
                     rs.getString("stock_sebelum"), 
                     rs.getString("stock_sesudah") 
                });
            }

        } catch(Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }
    
    private void cariInventory() {
        String keyword = TSearchInventory.getText().trim();
        DefaultTableModel model = (DefaultTableModel) TInventory.getModel();
        model.setRowCount(0);
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = """
                SELECT inv.id, i.nama_item, l.kode_lokasi, inv.stok_terkini
                FROM inventory inv
                INNER JOIN items i ON inv.item_id = i.id
                INNER JOIN locations l ON inv.location_id = l.id
                WHERE inv.stok_terkini > 0
                  AND (i.nama_item LIKE ? OR l.kode_lokasi LIKE ?)
                ORDER BY inv.id DESC
            """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama_item"),
                    rs.getString("kode_lokasi"),
                    rs.getInt("stok_terkini")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

       public final void loadDataInventory() { 
                DefaultTableModel model = new DefaultTableModel() {
                    // Best practice: Membuat sel tabel tidak bisa diedit secara manual oleh user
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        // Tambahkan kolom ID di index 0 
        model.addColumn("ID"); 
        model.addColumn("Nama Barang"); 
        model.addColumn("Lokasi"); 
        model.addColumn("Stok Terkini"); 
  

        String sql = """
            SELECT sh.id,  i.nama_item,l.kode_lokasi, sh.stok_terkini
                        FROM inventory sh 
                        INNER JOIN items i ON sh.item_id = i.id
                        INNER JOIN locations l ON sh.location_id = l.id
                        ORDER BY sh.id DESC
            """;

            // Menggunakan try-with-resources untuk menutup conn, stmt, dan rs secara otomatis dan aman
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) { 

                while (rs.next()) { 
                    model.addRow(new Object[]{ 
                        rs.getInt("id"), 
                        rs.getString("nama_item"),
                        rs.getString("kode_lokasi"), 
                        rs.getString("stok_terkini") 
                    }); 
                } 

                // Atur model ke tabel UI
                TInventory.setModel(model); 

                // Beritahu UI bahwa data telah berubah agar visual langsung ter-render ulang
                model.fireTableDataChanged();

                // Sembunyikan kolom ID (Index 0) secara total agar aman dari resize manual user
                TInventory.getColumnModel().getColumn(0).setMinWidth(0); 
                TInventory.getColumnModel().getColumn(0).setMaxWidth(0); 
                TInventory.getColumnModel().getColumn(0).setPreferredWidth(0);

            } catch (SQLException e) { 
                JOptionPane.showMessageDialog(null, "Gagal memuat data: " + e.getMessage()); 
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

        Body = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        LUser = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        LSupplier = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        LLocation = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        LItem = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        LBarangMasuk = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        LBarangKeluar = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TAktifitas = new javax.swing.JTable();
        TSearchActivity = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        LPActivity = new javax.swing.JLabel();
        BNActivity = new javax.swing.JButton();
        BPActivity = new javax.swing.JButton();
        BSearchActivity = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        TSearchInventory = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TInventory = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(248, 250, 252));

        Body.setBackground(new java.awt.Color(248, 250, 252));
        Body.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        Body.setLayout(new java.awt.BorderLayout(0, 5));

        jLabel2.setFont(new java.awt.Font("Urbanist ExtraBold", 0, 18)); // NOI18N
        jLabel2.setText("Selamat datang di Gudangin.aja");
        Body.add(jLabel2, java.awt.BorderLayout.CENTER);

        jLabel14.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(142, 157, 166));
        jLabel14.setText("Kelola stok barang dan pantau aktivitas gudang dengan lebih mudah.");
        Body.add(jLabel14, java.awt.BorderLayout.PAGE_END);

        jPanel9.setBackground(new java.awt.Color(248, 250, 252));
        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel9.setLayout(new java.awt.GridLayout(1, 0, 15, 0));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel1.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(119, 130, 153));
        jLabel1.setText("Users");

        LUser.setFont(new java.awt.Font("Inter ExtraBold", 0, 24)); // NOI18N
        LUser.setForeground(new java.awt.Color(0, 153, 153));
        LUser.setText("50");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(LUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(5, 5, 5)
                .addComponent(LUser)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel1);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel6.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(119, 130, 153));
        jLabel6.setText("Supplier");

        LSupplier.setFont(new java.awt.Font("Inter ExtraBold", 0, 24)); // NOI18N
        LSupplier.setForeground(new java.awt.Color(0, 102, 204));
        LSupplier.setText("50");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
            .addComponent(LSupplier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(5, 5, 5)
                .addComponent(LSupplier)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel8.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(119, 130, 153));
        jLabel8.setText("Lokasi");

        LLocation.setFont(new java.awt.Font("Inter ExtraBold", 0, 24)); // NOI18N
        LLocation.setForeground(new java.awt.Color(204, 0, 153));
        LLocation.setText("50");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
            .addComponent(LLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(5, 5, 5)
                .addComponent(LLocation)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel4);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel4.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(119, 130, 153));
        jLabel4.setText("Barang");

        LItem.setFont(new java.awt.Font("Inter ExtraBold", 0, 24)); // NOI18N
        LItem.setForeground(new java.awt.Color(102, 0, 153));
        LItem.setText("2.000");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(LItem, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(5, 5, 5)
                .addComponent(LItem)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel2);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel10.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(119, 130, 153));
        jLabel10.setText("Barang Masuk");

        LBarangMasuk.setFont(new java.awt.Font("Inter ExtraBold", 0, 24)); // NOI18N
        LBarangMasuk.setForeground(new java.awt.Color(0, 204, 51));
        LBarangMasuk.setText("1.500");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
            .addComponent(LBarangMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(5, 5, 5)
                .addComponent(LBarangMasuk)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel12.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(119, 130, 153));
        jLabel12.setText("Barang Keluar");

        LBarangKeluar.setFont(new java.awt.Font("Inter ExtraBold", 0, 24)); // NOI18N
        LBarangKeluar.setForeground(new java.awt.Color(204, 0, 0));
        LBarangKeluar.setText("1.000");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
            .addComponent(LBarangKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addGap(5, 5, 5)
                .addComponent(LBarangKeluar)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel6);

        jPanel10.setBackground(new java.awt.Color(248, 250, 252));
        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel10.setLayout(new java.awt.BorderLayout());

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel15.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel15.setText("Aktifitas Terbaru");

        jLabel3.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(142, 157, 166));
        jLabel3.setText("Daftar aktivitas barang.");

        TAktifitas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Jam", "Aktifitas", "Stok Sebelum", "Stok Sesudah"
            }
        ));
        TAktifitas.setShowGrid(true);
        jScrollPane1.setViewportView(TAktifitas);
        if (TAktifitas.getColumnModel().getColumnCount() > 0) {
            TAktifitas.getColumnModel().getColumn(3).setHeaderValue("Stok Sesudah");
        }

        TSearchActivity.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TSearchActivity.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        TSearchActivity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TSearchActivityKeyReleased(evt);
            }
        });

        jLabel5.setText("Cari data:");

        LPActivity.setFont(new java.awt.Font("Inter", 0, 10)); // NOI18N
        LPActivity.setForeground(new java.awt.Color(142, 157, 166));
        LPActivity.setText("5 dari 10 data ditampilkan");

        BNActivity.setBackground(new java.awt.Color(0, 153, 204));
        BNActivity.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        BNActivity.setForeground(new java.awt.Color(255, 255, 255));
        BNActivity.setText("Next");
        BNActivity.setBorderPainted(false);
        BNActivity.addActionListener(this::BNActivityActionPerformed);

        BPActivity.setText("Previous");
        BPActivity.setBorderPainted(false);
        BPActivity.setEnabled(false);
        BPActivity.addActionListener(this::BPActivityActionPerformed);

        BSearchActivity.setBackground(new java.awt.Color(0, 153, 204));
        BSearchActivity.setForeground(new java.awt.Color(255, 255, 255));
        BSearchActivity.setText("Cari");
        BSearchActivity.setBorderPainted(false);
        BSearchActivity.addActionListener(this::BSearchActivityActionPerformed);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TSearchActivity, javax.swing.GroupLayout.PREFERRED_SIZE, 744, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BSearchActivity)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(LPActivity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BPActivity, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(BNActivity, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addGap(2, 2, 2)
                .addComponent(jLabel3)
                .addGap(15, 15, 15)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TSearchActivity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BSearchActivity))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BNActivity)
                        .addComponent(BPActivity))
                    .addComponent(LPActivity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel10.add(jPanel7, java.awt.BorderLayout.PAGE_START);

        jPanel11.setBackground(new java.awt.Color(248, 250, 252));
        jPanel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 20, 20));
        jPanel11.setLayout(new java.awt.BorderLayout());

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel16.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel16.setText("Inventory");

        jLabel9.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(142, 157, 166));
        jLabel9.setText("Daftar barang yang tersedia.");

        TSearchInventory.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TSearchInventory.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));

        jLabel11.setText("Cari data:");

        TInventory.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nama Barang", "Lokasi", "Stok Terkini"
            }
        ));
        TInventory.setShowGrid(true);
        jScrollPane2.setViewportView(TInventory);

        jButton3.setBackground(new java.awt.Color(0, 153, 204));
        jButton3.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Next");
        jButton3.setBorderPainted(false);
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton4.setText("Previous");
        jButton4.setBorderPainted(false);
        jButton4.setEnabled(false);
        jButton4.addActionListener(this::jButton4ActionPerformed);

        jLabel13.setFont(new java.awt.Font("Inter", 0, 10)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(142, 157, 166));
        jLabel13.setText("5 dari 10 data ditampilkan");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TSearchInventory))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 886, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addGap(2, 2, 2)
                .addComponent(jLabel9)
                .addGap(15, 15, 15)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TSearchInventory)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton3)
                        .addComponent(jButton4))
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel8, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Body, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Body, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BNActivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BNActivityActionPerformed
          if (currentPage < totalPage) {

                currentPage++;

                loadDataAktifitas();
            }
    }//GEN-LAST:event_BNActivityActionPerformed

    private void BPActivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BPActivityActionPerformed
        if (currentPage > 1) {

            currentPage--;

            loadDataAktifitas();
        }
    }//GEN-LAST:event_BPActivityActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void TSearchActivityKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TSearchActivityKeyReleased
       cariActivity();
    }//GEN-LAST:event_TSearchActivityKeyReleased

    private void BSearchActivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BSearchActivityActionPerformed
        cariActivity();
    }//GEN-LAST:event_BSearchActivityActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BNActivity;
    private javax.swing.JButton BPActivity;
    private javax.swing.JButton BSearchActivity;
    private javax.swing.JPanel Body;
    private javax.swing.JLabel LBarangKeluar;
    private javax.swing.JLabel LBarangMasuk;
    private javax.swing.JLabel LItem;
    private javax.swing.JLabel LLocation;
    private javax.swing.JLabel LPActivity;
    private javax.swing.JLabel LSupplier;
    private javax.swing.JLabel LUser;
    private javax.swing.JTable TAktifitas;
    private javax.swing.JTable TInventory;
    private javax.swing.JTextField TSearchActivity;
    private javax.swing.JTextField TSearchInventory;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
