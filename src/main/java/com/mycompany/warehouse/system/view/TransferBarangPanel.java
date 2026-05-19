/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.warehouse.system.view;

import com.mycompany.warehouse.system.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import javax.swing.Timer;

/**
 *
 * @author ndesc
 */
public class TransferBarangPanel extends javax.swing.JPanel {
      private javax.swing.Timer searchTimer;
    private final HashMap<String, Integer> itemMap = new HashMap<>();
    private final HashMap<String, Integer> locationMap = new HashMap<>();
//--------INVENTORY------------
private int currentPageInventory = 1;
private final int dataPerPageInventory = 10;

private int totalDataInventory = 0;
private int totalPageInventory = 0;
    /**
     * Creates new form TransferBarang
     */
    public TransferBarangPanel() {
        initComponents();
        
            
        
        generateNomorTransfer();
        loadBarangAndLocationFromInventory();
        BSearchInventory.setVisible(false);   // Sembunyikan tombol Cari (search sudah realtime)
        BNInventory.setOpaque(true);
        BNInventory.setContentAreaFilled(true);
        BPInventory.setOpaque(true);
        BPInventory.setContentAreaFilled(true);
        hitungTotalDataInventory();
        loadDataInventory();
        
       
    }
public javax.swing.JScrollPane asScrollable() {
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(this);
        sp.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return sp;
    }
    public void generateNomorTransfer() {

    try {
           Connection conn = DatabaseConfig.getConnection();
        String sql = """
                SELECT COUNT(*) + 1 as total
                FROM transfer_transactions
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {

                int nomor = rs.getInt("total");

                int tahun = LocalDate.now().getYear();

                String nomorFormat =
                        String.format(
                                "TRF-%d-%03d",
                                tahun,
                                nomor
                        );

                LNoTransfer.setText(nomorFormat);
            }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
  public void loadBarangAndLocationFromInventory() {

    try {

        Connection conn = DatabaseConfig.getConnection();

        String sql = """
                 SELECT DISTINCT items.id,items.nama_item
                                                              FROM inventory
                                                                JOIN items
                                                                    ON inventory.item_id = items.id
                                                                WHERE inventory.stok_terkini > 0
                                                                ORDER BY items.nama_item
                """;

        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();
        inputBarang.removeAllItems();
        itemMap.clear();
        
        while (rs.next()) {
            int itemId = rs.getInt("id");
            String namaItem =  rs.getString("nama_item");
            itemMap.put(namaItem,itemId);
            inputBarang.addItem(namaItem);
         
        }

   
    } catch (Exception e) {

        e.printStackTrace();
    }
}
  
  private void loadLokasiByItem(int itemId) {

    try {
        Connection conn =
                DatabaseConfig.getConnection();

        String sql = """
          SELECT
                     inventory.location_id,
                     inventory.stok_terkini,
                     locations.kode_lokasi 
                     FROM inventory
                     JOIN locations
                     ON inventory.location_id = locations.id
                     WHERE inventory.item_id = ?
                     AND inventory.stok_terkini > 0                                  
                """;

        PreparedStatement ps =
                conn.prepareStatement(sql);

        ps.setInt(1, itemId);

        ResultSet rs =  ps.executeQuery();

        inputLokasiAwal.removeAllItems();
        inputLokasiTujuan.removeAllItems();

        locationMap.clear();

        while (rs.next()) {

            int locationId = rs.getInt("location_id");
            String lokasi = rs.getString("kode_lokasi");
            int stock =  rs.getInt("stok_terkini");
            /*
             tampilkan stock
             */
            String display =
                    lokasi
                    + " (Stock: "
                    + stock
                    + ")";

            inputLokasiAwal.addItem(display);
            inputLokasiTujuan.addItem(display);

            locationMap.put( display,locationId);
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}
private void clearForm() {

    // Reset supplier ke item pertama
    inputBarang.setSelectedIndex(0);
    // Reset lokasi ke item pertama
    inputLokasiAwal.setSelectedIndex(0);
     inputLokasiTujuan.setSelectedIndex(0);
    // Reset qty
    inputQty.setValue(0);
     // Reset Catatan
    inputCatatan.setText("");
    
    generateNomorTransfer();
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
  
    int offset = (currentPageInventory - 1) * dataPerPageInventory;
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

                
                // ===== UPDATE PAGINATION =====
                LPInventory.setText(
                    "Page " + currentPageInventory + " / " + totalPageInventory
                );
                updatePaginationButtonInventory();
            } catch (SQLException e) { 
                JOptionPane.showMessageDialog(null, "Gagal memuat data: " + e.getMessage()); 
            } 
     }
    private void updatePaginationButtonInventory() {
        // PREVIOUS
           BPInventory.setEnabled(currentPageInventory > 1);

           // NEXT
           BNInventory.setEnabled(currentPageInventory < totalPageInventory);

           // STYLE PREVIOUS
           if (BPInventory.isEnabled()) {

               BPInventory.setBackground(
                   new java.awt.Color(0,153,204)
               );

               BPInventory.setForeground(java.awt.Color.WHITE);

           } else {

               BPInventory.setBackground(
                   new java.awt.Color(220, 220, 220)
               );

               BPInventory.setForeground(
                   new java.awt.Color(120,120,120)
               );
           }

           // STYLE NEXT
           if (BNInventory.isEnabled()) {

               BNInventory.setBackground(
                   new java.awt.Color(0,153,204)
               );

               BNInventory.setForeground(java.awt.Color.WHITE);

           } else {

               BNInventory.setBackground(
                   new java.awt.Color(220,220,220)
               );

               BNInventory.setForeground(
                   new java.awt.Color(120,120,120)
               );
           }
       }
    private void cariInventory() {

        String keyword = TSearchInventory.getText();

        DefaultTableModel model =
            (DefaultTableModel) TInventory.getModel();

        model.setRowCount(0);

        try {

            Connection conn = DatabaseConfig.getConnection();

            String sql = """
               SELECT sh.id,  i.nama_item,l.kode_lokasi, sh.stok_terkini
                         FROM inventory sh 
                         INNER JOIN items i ON sh.item_id = i.id
                         INNER JOIN locations l ON sh.location_id = l.id
                         WHERE i.nama_item LIKE ?
                         OR l.kode_lokasi LIKE ?
                         OR sh.stok_terkini LIKE ?
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
                        rs.getString("nama_item"),
                        rs.getString("kode_lokasi"), 
                        rs.getString("stok_terkini") 
                });
            }

        } catch(Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }
    private void hitungTotalDataInventory() {

    String sql = "SELECT COUNT(*) AS total FROM inventory";

    try (
        Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {

        if (rs.next()) {

            totalDataInventory = rs.getInt("total");

            totalPageInventory = (int) Math.ceil(
                (double) totalDataInventory / dataPerPageInventory
            );
        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
            this,
            e.getMessage()
        );
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

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        inputQty = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        LNoTransfer = new javax.swing.JLabel();
        inputBarang = new javax.swing.JComboBox<>();
        inputLokasiTujuan = new javax.swing.JComboBox<>();
        inputLokasiAwal = new javax.swing.JComboBox<>();
        inputCatatan = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        TSearchInventory = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        TInventory = new javax.swing.JTable();
        BPInventory = new javax.swing.JButton();
        BNInventory = new javax.swing.JButton();
        LPInventory = new javax.swing.JLabel();
        BSearchInventory = new javax.swing.JButton();

        jPanel2.setBackground(new java.awt.Color(248, 250, 252));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        jPanel2.setLayout(new java.awt.BorderLayout(0, 2));

        jLabel1.setFont(new java.awt.Font("Urbanist ExtraBold", 0, 18)); // NOI18N
        jLabel1.setText("Transfer Barang");
        jPanel2.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        jLabel10.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(142, 157, 166));
        jLabel10.setText("Kelola dan Pindahkan Barang.");
        jPanel2.add(jLabel10, java.awt.BorderLayout.PAGE_END);

        jPanel3.setBackground(new java.awt.Color(248, 250, 252));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        jLabel11.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel11.setText("Informasi Barang");

        jLabel12.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(142, 157, 166));

        jLabel2.setText("No Transfer");
        jLabel2.setPreferredSize(new java.awt.Dimension(60, 16));

        jLabel3.setText("Dari Lokasi");

        jLabel4.setText("Barang");

        jLabel5.setText("Ke Lokasi");

        jLabel6.setText("Qty");

        inputQty.setModel(new javax.swing.SpinnerNumberModel(0, null, 99999, 1));

        jLabel7.setText("Catatan");

        btnSimpan.setBackground(new java.awt.Color(39, 174, 96));
        btnSimpan.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Transfer");
        btnSimpan.setBorderPainted(false);
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);

        LNoTransfer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LNoTransfer.setText("TRF-2026-001");

        inputBarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        inputBarang.addActionListener(this::inputBarangActionPerformed);

        inputLokasiTujuan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        inputLokasiAwal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(LNoTransfer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(inputBarang, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputQty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(15, 15, 15)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(inputCatatan)
                            .addComponent(inputLokasiAwal, 0, 281, Short.MAX_VALUE)
                            .addComponent(inputLokasiTujuan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(btnSimpan)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(2, 2, 2)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LNoTransfer)
                    .addComponent(inputLokasiAwal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputLokasiTujuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputCatatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnSimpan)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel5.setBackground(new java.awt.Color(248, 250, 252));
        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        jLabel13.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel13.setText("Table Inventory");

        jLabel14.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(142, 157, 166));
        jLabel14.setText("List data inventory.");

        jLabel15.setText("Cari data:");

        TSearchInventory.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TSearchInventory.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TSearchInventory.setToolTipText("");
        TSearchInventory.setActionCommand("<Not Set>");
        TSearchInventory.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(225, 228, 231), 1, true), javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        TSearchInventory.addActionListener(this::TSearchInventoryActionPerformed);
        TSearchInventory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TSearchInventoryKeyReleased(evt);
            }
        });

        TInventory.setModel(new javax.swing.table.DefaultTableModel(
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
        TInventory.setShowGrid(true);
        jScrollPane2.setViewportView(TInventory);

        BPInventory.setText("Previous");
        BPInventory.setBorderPainted(false);
        BPInventory.setEnabled(false);
        BPInventory.addActionListener(this::BPInventoryActionPerformed);

        BNInventory.setBackground(new java.awt.Color(0, 153, 204));
        BNInventory.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        BNInventory.setForeground(new java.awt.Color(255, 255, 255));
        BNInventory.setText("Next");
        BNInventory.setBorderPainted(false);
        BNInventory.addActionListener(this::BNInventoryActionPerformed);

        LPInventory.setFont(new java.awt.Font("Inter", 0, 10)); // NOI18N
        LPInventory.setForeground(new java.awt.Color(142, 157, 166));
        LPInventory.setText("5 dari 10 data ditampilkan");

        BSearchInventory.setBackground(new java.awt.Color(0, 153, 204));
        BSearchInventory.setForeground(new java.awt.Color(255, 255, 255));
        BSearchInventory.setText("Cari");
        BSearchInventory.addActionListener(this::BSearchInventoryActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(LPInventory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(BPInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BNInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TSearchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BSearchInventory)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel14)
                .addGap(15, 15, 15)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TSearchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(BSearchInventory))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LPInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BNInventory)
                    .addComponent(BPInventory))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel6, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 719, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 782, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(25, 25, 25)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(25, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void TSearchInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TSearchInventoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TSearchInventoryActionPerformed

    private void TSearchInventoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TSearchInventoryKeyReleased
/*
               hentikan timer lama
               */
              if (searchTimer != null
                      && searchTimer.isRunning()) {

                  searchTimer.stop();
              }

              /*
               delay 400ms
               */
              searchTimer =
                      new javax.swing.Timer(400, e -> {

                          cariInventory();
                      });

              searchTimer.setRepeats(false);

              searchTimer.start();
    }//GEN-LAST:event_TSearchInventoryKeyReleased

    private void BPInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BPInventoryActionPerformed
        if (currentPageInventory > 1) {

            currentPageInventory--;

            loadDataInventory();
        }
    }//GEN-LAST:event_BPInventoryActionPerformed

    private void BNInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BNInventoryActionPerformed
        if (currentPageInventory < totalPageInventory) {

            currentPageInventory++;

            loadDataInventory();
        }
    }//GEN-LAST:event_BNInventoryActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed

     Connection conn = null;

    try {

        /*
         ============================
         AMBIL DATA FORM
         ============================
         */
        String nomorTransfer =
                LNoTransfer.getText();

        String namaBarang =
                inputBarang
                        .getSelectedItem()
                        .toString();

        String lokasiAwalDisplay =
                inputLokasiAwal
                        .getSelectedItem()
                        .toString();

        String lokasiTujuanDisplay =
                inputLokasiTujuan
                        .getSelectedItem()
                        .toString();

        int itemId =
                itemMap.get(namaBarang);

        int lokasiAwalId =
                locationMap.get(lokasiAwalDisplay);

        int lokasiTujuanId =
                locationMap.get(lokasiTujuanDisplay);

        int qty =
                Integer.parseInt(
                        inputQty.getValue().toString()
                );

        String catatan =
                inputCatatan.getText();

        /*
         ============================
         VALIDASI
         ============================
         */

        if (qty <= 0) {

            JOptionPane.showMessageDialog(
                    null,
                    "Qty tidak boleh kosong / 0"
            );

            return;
        }

        if (catatan.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Catatan tidak boleh kosong"
            );

            return;
        }

        if (lokasiAwalId == lokasiTujuanId) {

            JOptionPane.showMessageDialog(
                    null,
                    "Lokasi awal dan tujuan tidak boleh sama"
            );

            return;
        }

        /*
         ============================
         KONEKSI DATABASE
         ============================
         */
        conn = DatabaseConfig.getConnection();

        conn.setAutoCommit(false);

        /*
         ============================
         CEK STOCK LOKASI AWAL
         ============================
         */
        int stokAwal = 0;

        String sqlCekStock =
                """
                SELECT stok_terkini
                FROM inventory
                WHERE item_id = ?
                AND location_id = ?
                """;

        PreparedStatement psCekStock =
                conn.prepareStatement(sqlCekStock);

        psCekStock.setInt(1, itemId);
        psCekStock.setInt(2, lokasiAwalId);

        ResultSet rsStock =
                psCekStock.executeQuery();

        if (rsStock.next()) {

            stokAwal =
                    rsStock.getInt("stok_terkini");

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "Stock inventory tidak ditemukan"
            );

            return;
        }

        /*
         ============================
         VALIDASI STOCK
         ============================
         */
        if (qty > stokAwal) {

            JOptionPane.showMessageDialog(
                    null,
                    "Qty melebihi stock tersedia"
            );

            return;
        }

        /*
         ============================
         INSERT TRANSFER
         ============================
         */
        String sqlTransfer =
                """
                INSERT INTO transfer_transactions (
                    nomor_transfer,
                    item_id,
                    location_from_id,
                    location_to_id,
                    qty,
                    catatan
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        PreparedStatement psTransfer =
                conn.prepareStatement(sqlTransfer);

        psTransfer.setString(1, nomorTransfer);
        psTransfer.setInt(2, itemId);
        psTransfer.setInt(3, lokasiAwalId);
        psTransfer.setInt(4, lokasiTujuanId);
        psTransfer.setInt(5, qty);
        psTransfer.setString(6, catatan);

        psTransfer.executeUpdate();

        /*
         ============================
         KURANGI STOCK LOKASI AWAL
         ============================
         */
        String sqlKurangStock =
                """
                UPDATE inventory
                SET stok_terkini = stok_terkini - ?
                WHERE item_id = ?
                AND location_id = ?
                """;

        PreparedStatement psKurang =
                conn.prepareStatement(sqlKurangStock);

        psKurang.setInt(1, qty);
        psKurang.setInt(2, itemId);
        psKurang.setInt(3, lokasiAwalId);

        psKurang.executeUpdate();

        /*
         ============================
         AMBIL STOCK TUJUAN SEBELUM
         ============================
         */
        int stokTujuanSebelum = 0;

        String sqlGetStockTujuan =
                """
                SELECT stok_terkini
                FROM inventory
                WHERE item_id = ?
                AND location_id = ?
                """;

        PreparedStatement psGetStockTujuan =
                conn.prepareStatement(sqlGetStockTujuan);

        psGetStockTujuan.setInt(1, itemId);
        psGetStockTujuan.setInt(2, lokasiTujuanId);

        ResultSet rsTujuan =
                psGetStockTujuan.executeQuery();

        if (rsTujuan.next()) {

            stokTujuanSebelum =
                    rsTujuan.getInt("stok_terkini");
        }

        /*
         ============================
         TAMBAH STOCK TUJUAN
         ============================
         */
        String sqlTambahStock =
                """
                UPDATE inventory
                SET stok_terkini = stok_terkini + ?
                WHERE item_id = ?
                AND location_id = ?
                """;

        PreparedStatement psTambah =
                conn.prepareStatement(sqlTambahStock);

        psTambah.setInt(1, qty);
        psTambah.setInt(2, itemId);
        psTambah.setInt(3, lokasiTujuanId);

        psTambah.executeUpdate();

        /*
         ============================
         STOCK HISTORY AWAL
         ============================
         */
        String sqlHistory =
                """
                INSERT INTO stock_history (
                    jenis_transaksi,
                    nomor_referensi,
                    item_id,
                    location_id,
                    qty,
                    stock_sebelum,
                    stock_sesudah
                )
                VALUES (?, ?, ?, ?, ?, ?,?)
                """;

        PreparedStatement psHistoryAwal =
                conn.prepareStatement(sqlHistory);

        psHistoryAwal.setString(1, "transfer");
        psHistoryAwal.setString(2, nomorTransfer);
        psHistoryAwal.setInt(3, itemId);
        psHistoryAwal.setInt(4, lokasiAwalId);
        psHistoryAwal.setInt(5, qty);
        psHistoryAwal.setInt(6, stokAwal);
        psHistoryAwal.setInt(7, stokAwal - qty);

        psHistoryAwal.executeUpdate();

        /*
         ============================
         STOCK HISTORY TUJUAN
         ============================
         */
        PreparedStatement psHistoryTujuan =
                conn.prepareStatement(sqlHistory);

        psHistoryTujuan.setString(1, "transfer");
        psHistoryTujuan.setString(2, nomorTransfer);
        psHistoryTujuan.setInt(3, itemId);
        psHistoryTujuan.setInt(4, lokasiTujuanId);
        psHistoryTujuan.setInt(5, qty);
        psHistoryTujuan.setInt(6, stokTujuanSebelum);
        psHistoryTujuan.setInt(7, stokTujuanSebelum + qty);

        psHistoryTujuan.executeUpdate();

        /*
         ============================
         COMMIT
         ============================
         */
        conn.commit();

        JOptionPane.showMessageDialog(
                null,
                "Transfer barang berhasil"
        );
        clearForm();
        loadDataInventory();

    } catch (Exception e) {

        try {

            if (conn != null) {

                conn.rollback();
            }

        } catch (Exception ex) {

            System.out.println(ex);
        }

        JOptionPane.showMessageDialog(
                null,
                "Error : " + e.getMessage()
        );

    } finally {

        try {

            if (conn != null) {

                conn.setAutoCommit(true);
                conn.close();
            }

        } catch (Exception e) {

            System.out.println(e);
        }
    }
        
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void BSearchInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BSearchInventoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BSearchInventoryActionPerformed

    private void inputBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputBarangActionPerformed
    if (inputBarang.getSelectedItem() == null) {
            return;
        }
        String namaBarang = inputBarang.getSelectedItem().toString();
        int itemId = itemMap.get(namaBarang);
        loadLokasiByItem(itemId);
    }//GEN-LAST:event_inputBarangActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BNInventory;
    private javax.swing.JButton BPInventory;
    private javax.swing.JButton BSearchInventory;
    private javax.swing.JLabel LNoTransfer;
    private javax.swing.JLabel LPInventory;
    private javax.swing.JTable TInventory;
    private javax.swing.JTextField TSearchInventory;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<String> inputBarang;
    private javax.swing.JTextField inputCatatan;
    private javax.swing.JComboBox<String> inputLokasiAwal;
    private javax.swing.JComboBox<String> inputLokasiTujuan;
    private javax.swing.JSpinner inputQty;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
