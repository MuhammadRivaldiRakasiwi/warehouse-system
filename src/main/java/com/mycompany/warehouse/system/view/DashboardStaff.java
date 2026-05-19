/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.warehouse.system.view;
import com.mycompany.warehouse.system.DatabaseConfig;
import com.mycompany.warehouse.system.service.DashboardService;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
/**
 *
 * @author ndesc
 */
public class DashboardStaff extends javax.swing.JPanel {
    public static DashboardStaff instance;
    private int currentPage = 1;
    private final int dataPerPage = 10;

    private int totalData = 0;
    private int totalPage = 0;
    /**
     * Creates new form DashboardStaff
     */
    public DashboardStaff() {
        
        initComponents();
        BNActivity.setOpaque(true);
        BNActivity.setContentAreaFilled(true);

        BPActivity.setOpaque(true);
        BPActivity.setContentAreaFilled(true);
            hitungTotalData();
          loadDataAktifitas();
        instance = this;
       loadDataCount();
    }
public final void loadDataCount(){
     LTotalBarang.setText(String.valueOf(DashboardService.getTotalItemsInventory()));
        LBarangMasuk.setText(String.valueOf(DashboardService.getTotalItemsMasukHariIni()));
        LBarangKeluar.setText(String.valueOf(DashboardService.getTotalItemsKeluarHariIni()));
        LBarangHampirHabis.setText(String.valueOf(DashboardService.getTotalItemsHampirHabis()));
}
   
    public final void loadDataAktifitas() {

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

        // ===== STYLE TABLE =====
        TAktifitas.setForeground(new java.awt.Color(33, 37, 41));
        TAktifitas.setBackground(java.awt.Color.WHITE);

        TAktifitas.setSelectionForeground(java.awt.Color.WHITE);
        TAktifitas.setSelectionBackground(
            new java.awt.Color(0, 153, 204)
        );

        TAktifitas.setGridColor(
            new java.awt.Color(230, 230, 230)
        );

        TAktifitas.setRowHeight(31);

        TAktifitas.getTableHeader().setForeground(
            java.awt.Color.BLACK
        );

        TAktifitas.getTableHeader().setBackground(
            new java.awt.Color(245,245,245)
        );

        // Hide ID
        TAktifitas.getColumnModel().getColumn(0).setMinWidth(0);
        TAktifitas.getColumnModel().getColumn(0).setMaxWidth(0);
        TAktifitas.getColumnModel().getColumn(0).setPreferredWidth(0);

        // ===== UPDATE PAGINATION =====
        LPActivity.setText(
            "Page " + currentPage + " / " + totalPage
        );

        updatePaginationButton();

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            null,
            "Gagal memuat data: " + e.getMessage()
        );
    }
}
    private void updatePaginationButton() {
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
                                   WHERE sh.waktu_transaksi  LIKE ?
                                     OR sh.jenis_transaksi  LIKE ?
                                     OR i.nama_item  LIKE ?
                                     OR l.kode_lokasi   LIKE ?
                                     OR sh.stock_sebelum   LIKE ?
                                      OR sh.stock_sesudah   LIKE ?
                                  ORDER BY sh.id DESC
            """;

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ps.setString(4, "%" + keyword + "%");
            ps.setString(5, "%" + keyword + "%");
            ps.setString(6, "%" + keyword + "%");

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
       
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel6 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        label7 = new java.awt.Label();
        jPanel7 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        LTotalBarang = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        LBarangMasuk = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        LBarangKeluar = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        LBarangHampirHabis = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        label8 = new java.awt.Label();
        jScrollPane1 = new javax.swing.JScrollPane();
        TAktifitas = new javax.swing.JTable();
        TSearchActivity = new javax.swing.JTextField();
        LPActivity = new javax.swing.JLabel();
        BPActivity = new javax.swing.JButton();
        BNActivity = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnSearchActivity = new javax.swing.JButton();

        setBackground(new java.awt.Color(248, 250, 252));

        jPanel6.setBackground(new java.awt.Color(248, 250, 252));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        jPanel6.setLayout(new java.awt.BorderLayout(0, 2));

        jLabel24.setFont(new java.awt.Font("Urbanist", 1, 18)); // NOI18N
        jLabel24.setText("Selamat datang di Gudangin.aja");
        jPanel6.add(jLabel24, java.awt.BorderLayout.PAGE_START);

        label7.setBackground(new java.awt.Color(248, 250, 252));
        label7.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        label7.setForeground(new java.awt.Color(142, 157, 166));
        label7.setText("Kelola stok barang dan pantau aktivitas gudang dengan lebih mudah.");
        jPanel6.add(label7, java.awt.BorderLayout.PAGE_END);

        jPanel7.setBackground(new java.awt.Color(248, 250, 252));
        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel7.setLayout(new java.awt.GridLayout(1, 0, 15, 0));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel2.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(119, 130, 153));
        jLabel2.setText("Total Barang");

        LTotalBarang.setFont(new java.awt.Font("Inter ExtraBold", 0, 24)); // NOI18N
        LTotalBarang.setForeground(new java.awt.Color(14, 72, 210));
        LTotalBarang.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LTotalBarang.setText("2.000");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(LTotalBarang, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(LTotalBarang))
        );

        jPanel7.add(jPanel1);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel6.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(119, 130, 153));
        jLabel6.setText("Barang Masuk Hari ini");

        LBarangMasuk.setFont(new java.awt.Font("Inter ExtraBold", 0, 24)); // NOI18N
        LBarangMasuk.setForeground(new java.awt.Color(0, 153, 102));
        LBarangMasuk.setText("2.000");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
            .addComponent(LBarangMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(5, 5, 5)
                .addComponent(LBarangMasuk)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel7.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(119, 130, 153));
        jLabel7.setText("Barang Keluar Hari ini");

        LBarangKeluar.setFont(new java.awt.Font("Inter ExtraBold", 0, 24)); // NOI18N
        LBarangKeluar.setForeground(new java.awt.Color(204, 0, 51));
        LBarangKeluar.setText("2.000");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
            .addComponent(LBarangKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel7)
                .addGap(5, 5, 5)
                .addComponent(LBarangKeluar)
                .addGap(0, 0, 0))
        );

        jPanel7.add(jPanel4);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel4.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(119, 130, 153));
        jLabel4.setText("Barang Hampir Habis < 10");

        LBarangHampirHabis.setFont(new java.awt.Font("Inter ExtraBold", 0, 24)); // NOI18N
        LBarangHampirHabis.setForeground(new java.awt.Color(255, 204, 0));
        LBarangHampirHabis.setText("2.000");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
            .addComponent(LBarangHampirHabis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(5, 5, 5)
                .addComponent(LBarangHampirHabis)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel2);

        jPanel8.setBackground(new java.awt.Color(248, 250, 252));
        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 20, 20));
        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel25.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel25.setText("Aktifitas Terbaru");

        label8.setBackground(new java.awt.Color(255, 255, 255));
        label8.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        label8.setForeground(new java.awt.Color(142, 157, 166));
        label8.setText("Pantau aktivitas gudang.");

        TAktifitas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 228, 231)));
        TAktifitas.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
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
        TAktifitas.setRowMargin(20);
        TAktifitas.setSelectionBackground(new java.awt.Color(229, 235, 239));
        TAktifitas.setSelectionForeground(new java.awt.Color(229, 235, 239));
        TAktifitas.setShowGrid(true);
        jScrollPane1.setViewportView(TAktifitas);

        TSearchActivity.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TSearchActivity.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TSearchActivity.setToolTipText("");
        TSearchActivity.setActionCommand("<Not Set>");
        TSearchActivity.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(225, 228, 231), 1, true), javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        TSearchActivity.addActionListener(this::TSearchActivityActionPerformed);
        TSearchActivity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TSearchActivityKeyReleased(evt);
            }
        });

        LPActivity.setFont(new java.awt.Font("Inter", 0, 10)); // NOI18N
        LPActivity.setForeground(new java.awt.Color(142, 157, 166));
        LPActivity.setText("5 dari 10 data ditampilkan");

        BPActivity.setText("Previous");
        BPActivity.setBorderPainted(false);
        BPActivity.setEnabled(false);
        BPActivity.addActionListener(this::BPActivityActionPerformed);

        BNActivity.setBackground(new java.awt.Color(0, 153, 204));
        BNActivity.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        BNActivity.setForeground(new java.awt.Color(255, 255, 255));
        BNActivity.setText("Next");
        BNActivity.setBorderPainted(false);
        BNActivity.addActionListener(this::BNActivityActionPerformed);

        jLabel1.setText("Cari data:");

        btnSearchActivity.setBackground(new java.awt.Color(0, 153, 204));
        btnSearchActivity.setForeground(new java.awt.Color(255, 255, 255));
        btnSearchActivity.setText("Cari ");
        btnSearchActivity.setBorderPainted(false);
        btnSearchActivity.addActionListener(this::btnSearchActivityActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(LPActivity, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(BPActivity, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BNActivity, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(label8, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TSearchActivity)
                        .addGap(18, 18, 18)
                        .addComponent(btnSearchActivity, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel25)
                .addGap(0, 0, 0)
                .addComponent(label8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TSearchActivity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnSearchActivity))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LPActivity, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BNActivity)
                    .addComponent(BPActivity)))
        );

        jPanel8.add(jPanel5, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void TSearchActivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TSearchActivityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TSearchActivityActionPerformed

    private void BPActivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BPActivityActionPerformed
            if (currentPage > 1) {

            currentPage--;

            loadDataAktifitas();
        }
    }//GEN-LAST:event_BPActivityActionPerformed

    private void BNActivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BNActivityActionPerformed
           if (currentPage < totalPage) {

                currentPage++;

                loadDataAktifitas();
            }
    }//GEN-LAST:event_BNActivityActionPerformed

    private void TSearchActivityKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TSearchActivityKeyReleased

    }//GEN-LAST:event_TSearchActivityKeyReleased

    private void btnSearchActivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActivityActionPerformed
       cariActivity();
    }//GEN-LAST:event_btnSearchActivityActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BNActivity;
    private javax.swing.JButton BPActivity;
    private javax.swing.JLabel LBarangHampirHabis;
    private javax.swing.JLabel LBarangKeluar;
    private javax.swing.JLabel LBarangMasuk;
    private javax.swing.JLabel LPActivity;
    private javax.swing.JLabel LTotalBarang;
    private javax.swing.JTable TAktifitas;
    private javax.swing.JTextField TSearchActivity;
    private javax.swing.JButton btnSearchActivity;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.Label label7;
    private java.awt.Label label8;
    // End of variables declaration//GEN-END:variables
}
