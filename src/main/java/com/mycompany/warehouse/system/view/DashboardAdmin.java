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
    /**
     * Creates new form DashboardAdmin
     */
    public DashboardAdmin() {
        initComponents();
          loadDataAktifitas();
          loadDataInventory();
         instance = this;
         
         loadDataCount();
       
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
             DefaultTableModel model = new DefaultTableModel() {
                 // Best practice: Membuat sel tabel tidak bisa diedit secara manual oleh user
                 @Override
                 public boolean isCellEditable(int row, int column) {
                     return false;
                 }
             };

     // Tambahkan kolom ID di index 0 
     model.addColumn("ID"); 
     model.addColumn("Jam"); 
     model.addColumn("Aktifitas"); 
     model.addColumn("Barang"); 
      model.addColumn("Lokasi"); 
     model.addColumn("Stok Sebelum"); 
     model.addColumn("Stok Sesudah"); 

     String sql = """
         SELECT sh.id, sh.waktu_transaksi, sh.jenis_transaksi, i.nama_item,l.kode_lokasi, sh.stock_sebelum, sh.stock_sesudah 
         FROM stock_history sh 
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
                     rs.getString("waktu_transaksi"),
                     rs.getString("jenis_transaksi"), 
                     rs.getString("nama_item"), 
                     rs.getString("kode_lokasi"), 
                     rs.getString("stock_sebelum"), 
                     rs.getString("stock_sesudah") 
                 }); 
             } 

             // Atur model ke tabel UI
             TAktifitas.setModel(model); 

             // Beritahu UI bahwa data telah berubah agar visual langsung ter-render ulang
             model.fireTableDataChanged();

             // Sembunyikan kolom ID (Index 0) secara total agar aman dari resize manual user
             TAktifitas.getColumnModel().getColumn(0).setMinWidth(0); 
             TAktifitas.getColumnModel().getColumn(0).setMaxWidth(0); 
             TAktifitas.getColumnModel().getColumn(0).setPreferredWidth(0);

         } catch (SQLException e) { 
             JOptionPane.showMessageDialog(null, "Gagal memuat data: " + e.getMessage()); 
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
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        LUser = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        LItem = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        LSupplier = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        LLocation = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        LBarangMasuk = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        LBarangKeluar = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TAktifitas = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TInventory = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Dashboard Admin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 19, 0, 0);
        add(jLabel2, gridBagConstraints);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Users");

        LUser.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        LUser.setText("50");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(LUser)))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LUser)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.ipady = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 19, 0, 0);
        add(jPanel1, gridBagConstraints);

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Barang");

        LItem.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        LItem.setText("50");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(LItem)))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LItem)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.ipady = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(jPanel2, gridBagConstraints);

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Supplier");

        LSupplier.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        LSupplier.setText("50");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(LSupplier)))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LSupplier)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 41;
        gridBagConstraints.ipady = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 38);
        add(jPanel3, gridBagConstraints);

        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Lokasi");

        LLocation.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        LLocation.setText("50");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(LLocation)))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LLocation)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.ipady = 42;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 19, 0, 0);
        add(jPanel4, gridBagConstraints);

        jPanel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Barang Masuk");

        LBarangMasuk.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        LBarangMasuk.setText("50");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(LBarangMasuk)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LBarangMasuk)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.ipady = 42;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        add(jPanel5, gridBagConstraints);

        jPanel6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Barang Keluar");

        LBarangKeluar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        LBarangKeluar.setText("50");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(LBarangKeluar)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LBarangKeluar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 7;
        gridBagConstraints.ipady = 42;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 38);
        add(jPanel6, gridBagConstraints);

        TAktifitas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Jam", "Aktifitas", "Stok Sebelum", "Stok Sesudah"
            }
        ));
        jScrollPane1.setViewportView(TAktifitas);
        if (TAktifitas.getColumnModel().getColumnCount() > 0) {
            TAktifitas.getColumnModel().getColumn(3).setHeaderValue("Stok Sesudah");
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 398;
        gridBagConstraints.ipady = 102;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Aktifitas Terbaru");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 0);
        add(jLabel15, gridBagConstraints);

        TInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nama Barang", "Lokasi", "Stok Terkini"
            }
        ));
        jScrollPane2.setViewportView(TInventory);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 398;
        gridBagConstraints.ipady = 102;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 119, 0);
        add(jScrollPane2, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Inventory");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 47;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 0);
        add(jLabel16, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LBarangKeluar;
    private javax.swing.JLabel LBarangMasuk;
    private javax.swing.JLabel LItem;
    private javax.swing.JLabel LLocation;
    private javax.swing.JLabel LSupplier;
    private javax.swing.JLabel LUser;
    private javax.swing.JTable TAktifitas;
    private javax.swing.JTable TInventory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
