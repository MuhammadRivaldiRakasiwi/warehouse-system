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

import java.text.SimpleDateFormat;

import java.util.HashMap;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.JOptionPane;
/**
 *
 * @author ndesc
 */
public class BarangKeluarPanel extends javax.swing.JPanel {
    private final HashMap<String, Integer> itemMap = new HashMap<>();
    private final HashMap<String, Integer> locationMap = new HashMap<>();
     private javax.swing.Timer searchTimer;
    /**
     * Creates new form BarangKeluarPanel
     */
     private int currentPage = 1;
    private final int dataPerPage = 10;

    private int totalData = 0;
    private int totalPage = 0;

    public BarangKeluarPanel() {
        initComponents();
         inputTanggalKirim.setDateFormatString("yyyy-MM-dd");
         inputTanggalKirim.getDateEditor().setEnabled(false);
         inputTanggalKirim.setDate(new java.util.Date());
         
            loadBarangFromInventory();
            generateNomorPengeluaran();
            
                hitungTotalData();
              loadDataOutbound();
    }
    private void generateNomorPengeluaran() {
    try {
        Connection conn = DatabaseConfig.getConnection();
        String sql = """
            SELECT COUNT(*) + 1 as total 
            FROM outbound_transactions
            """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int nomor = rs.getInt("total");
            String nomorFormat = String.format("DO-2026-%03d", nomor);
            labelNoPengeluaran.setText(nomorFormat);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }    
   }

public void loadBarangFromInventory() {

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
        inputNamaBarang.removeAllItems();
        itemMap.clear();
        
        while (rs.next()) {
            int itemId = rs.getInt("id");
            String namaItem =  rs.getString("nama_item");
            itemMap.put(namaItem,itemId);
            inputNamaBarang.addItem(namaItem);
         
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

        inputLokasi.removeAllItems();

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

            inputLokasi.addItem(display);

            locationMap.put( display,locationId);
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}

private int getCurrentStock( int itemId,int locationId) {

    try {

        Connection conn = DatabaseConfig.getConnection();

        String sql = """
                SELECT stok_terkini
                FROM inventory
                WHERE item_id = ?
                AND location_id = ?
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, itemId);
        ps.setInt(2, locationId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("stok_terkini");
        }
    } catch (Exception e) {
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
  public final void loadDataOutbound() {

            DefaultTableModel model = new DefaultTableModel() {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            model.addColumn("ID");
            model.addColumn("Penerima/Requestor");
            model.addColumn("No Pengiriman");
            model.addColumn("Tujuan");
            model.addColumn("Barang");
            model.addColumn("Satuan");           
            model.addColumn("Quantity");


            int offset = (currentPage - 1) * dataPerPage;

            String sql = """
                SELECT ot.id,
                                                        ot.penerima_requestor ,
                                                        ot.nomor_pengeluaran,
                                                        ot.tujuan,
                                                        i.nama_item,
                                                        i.satuan,
                                                        ot.qty
                                                 FROM outbound_transactions ot
                                                 INNER JOIN items i ON ot.item_id= i.id
                                                 ORDER BY ot.id DESC
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
                        rs.getString("penerima_requestor"),
                        rs.getString("nomor_pengeluaran"),
                        rs.getString("tujuan"),
                        rs.getString("nama_item"),
                        rs.getString("satuan"),
                        rs.getString("qty")
                    });
                }

                TBarangKeluar.setModel(model);

                // ===== STYLE TABLE =====
                TBarangKeluar.setForeground(new java.awt.Color(33, 37, 41));
                TBarangKeluar.setBackground(java.awt.Color.WHITE);

                TBarangKeluar.setSelectionForeground(java.awt.Color.WHITE);
                TBarangKeluar.setSelectionBackground(
                    new java.awt.Color(0, 153, 204)
                );

                TBarangKeluar.setGridColor(
                    new java.awt.Color(230, 230, 230)
                );

                TBarangKeluar.setRowHeight(31);

                TBarangKeluar.getTableHeader().setForeground(
                    java.awt.Color.BLACK
                );

                TBarangKeluar.getTableHeader().setBackground(
                    new java.awt.Color(245,245,245)
                );

                // Hide ID
                TBarangKeluar.getColumnModel().getColumn(0).setMinWidth(0);
                TBarangKeluar.getColumnModel().getColumn(0).setMaxWidth(0);
                TBarangKeluar.getColumnModel().getColumn(0).setPreferredWidth(0);

                // ===== UPDATE PAGINATION =====
                LPBarangMasuk.setText(
                    "Page " + currentPage + " / " + totalPage
                );

                updatePaginationButton();

            } catch (SQLException e) {

                JOptionPane.showMessageDialog(
                    javax.swing.SwingUtilities.getWindowAncestor(this),
                    "Gagal memuat data: " + e.getMessage()
                );
                }
    }
     
        private void updatePaginationButton() {
        // PREVIOUS
           BPBarangKeluar.setEnabled(currentPage > 1);

           // NEXT
           BNBarangKeluar.setEnabled(currentPage < totalPage);

           // STYLE PREVIOUS
           if (BPBarangKeluar.isEnabled()) {

               BPBarangKeluar.setBackground(
                   new java.awt.Color(0,153,204)
               );

               BPBarangKeluar.setForeground(java.awt.Color.WHITE);

           } else {

               BPBarangKeluar.setBackground(
                   new java.awt.Color(220, 220, 220)
               );

               BPBarangKeluar.setForeground(
                   new java.awt.Color(120,120,120)
               );
           }

           // STYLE NEXT
           if (BNBarangKeluar.isEnabled()) {

               BNBarangKeluar.setBackground(
                   new java.awt.Color(0,153,204)
               );

               BNBarangKeluar.setForeground(java.awt.Color.WHITE);

           } else {

               BNBarangKeluar.setBackground(
                   new java.awt.Color(220,220,220)
               );

               BNBarangKeluar.setForeground(
                   new java.awt.Color(120,120,120)
               );
           }
       }
    
    private void hitungTotalData() {

    String sql = "SELECT COUNT(*) AS total FROM outbound_transactions";

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
            javax.swing.SwingUtilities.getWindowAncestor(this),
            e.getMessage()
        );
    }
}
     
   
 
private void cariBarangKeluar() {

        String keyword = TSearchBarangKeluar.getText();

        DefaultTableModel model =
            (DefaultTableModel) TBarangKeluar.getModel();

        model.setRowCount(0);

        try {

            Connection conn = DatabaseConfig.getConnection();

            String sql = """
                           SELECT
                                         ot.id,
                                         ot.penerima_requestor,
                                         ot.nomor_pengeluaran,
                                         ot.tujuan,
                                         i.nama_item,
                                         i.satuan,
                                         ot.qty
                                     FROM outbound_transactions ot
                                     INNER JOIN items i
                                         ON ot.item_id = i.id
                                     WHERE
                                         ot.penerima_requestor LIKE ?
                                         OR ot.nomor_pengeluaran LIKE ?
                                         OR ot.tujuan LIKE ?
                                         OR i.nama_item LIKE ?
                                         OR i.satuan LIKE ?
                                         OR CAST(ot.qty AS CHAR) LIKE ?
                                     ORDER BY ot.id DESC
                         LIMIT 50
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
                        rs.getString("penerima_requestor"),
                        rs.getString("nomor_pengeluaran"),
                        rs.getString("tujuan"),
                        rs.getString("nama_item"),
                        rs.getString("satuan"),
                        rs.getString("qty")
                });
            }

        } catch(Exception e) {

            JOptionPane.showMessageDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
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

            cariBarangKeluar();
        });

        searchTimer.setRepeats(false);

        searchTimer.start();
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
        int itemId = itemMap.get(namaBarang);
        loadLokasiByItem(itemId);
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
        
        generateNomorPengeluaran();
        loadDataOutbound();

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

    try {

        Connection conn =
                DatabaseConfig.getConnection();

        String tahun =
                new SimpleDateFormat("yyyy")
                        .format(
                                new java.util.Date()
                        );

        String sql = """
                SELECT COUNT(*) + 1 as total
                FROM outbound_transactions
                """;

        PreparedStatement ps =
                conn.prepareStatement(sql);

        ResultSet rs =
                ps.executeQuery();

        if (rs.next()) {

            int nomor =
                    rs.getInt("total");

            String urutan =
                    String.format("%03d", nomor);

            return "REQ-"
                    + tahun
                    + "-"
                    + urutan;
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return "REQ-ERROR";
}

    private String generateNoSuratJalan() {

        try {

            Connection conn =
                    DatabaseConfig.getConnection();

            String tahun =
                    new SimpleDateFormat("yyyy")
                            .format(
                                    new java.util.Date()
                            );

            String sql = """
                    SELECT COUNT(*) + 1 as total
                    FROM outbound_transactions
                    """;

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                int nomor =
                        rs.getInt("total");

                String urutan =
                        String.format("%03d", nomor);

                return "SJ-"
                        + tahun
                        + "-"
                        + urutan;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "SJ-ERROR";
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