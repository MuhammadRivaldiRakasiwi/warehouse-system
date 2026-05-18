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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ndesc
 */
public class TransferBarang extends javax.swing.JPanel {
//--------INVENTORY------------
private int currentPageInventory = 1;
private final int dataPerPageInventory = 10;

private int totalDataInventory = 0;
private int totalPageInventory = 0;
    /**
     * Creates new form TransferBarang
     */
    public TransferBarang() {
        initComponents();
        
        BNInventory.setOpaque(true);
        BNInventory.setContentAreaFilled(true);
        BPInventory.setOpaque(true);
        BPInventory.setContentAreaFilled(true);
        hitungTotalDataInventory();
        loadDataInventory();
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

        jPanel1 = new javax.swing.JPanel();
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

        jPanel1.setBackground(new java.awt.Color(248, 250, 252));

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
                            .addComponent(inputQty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
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
                            .addComponent(inputLokasiAwal, 0, 274, Short.MAX_VALUE)
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
            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(LPInventory, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(BPInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BNInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TSearchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(BSearchInventory)
                .addContainerGap())
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 693, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 782, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void TSearchInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TSearchInventoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TSearchInventoryActionPerformed

    private void TSearchInventoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TSearchInventoryKeyReleased

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
       
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void BSearchInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BSearchInventoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BSearchInventoryActionPerformed


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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
