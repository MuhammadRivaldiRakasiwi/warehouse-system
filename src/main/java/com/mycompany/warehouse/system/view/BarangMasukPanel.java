/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.warehouse.system.view;
//import com.mycompany.warehouse.system.DatabaseConfig;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author ndesc
 */
public class BarangMasukPanel extends javax.swing.JPanel {
   


    private final HashMap<String, Integer> supplierMap = new HashMap<>();
    private final HashMap<String, Integer> itemMap = new HashMap<>();
    private final HashMap<String, Integer> locationMap = new HashMap<>();
     private int currentPage = 1;
    private final int dataPerPage = 10;

    private int totalData = 0;
    private int totalPage = 0;
    /**
     * Creates new form BarangMasukPanel
     */
    public BarangMasukPanel() {
          initComponents();
            inputTanggalTerima.setDateFormatString("yyyy-MM-dd");
            inputTanggalTerima.getDateEditor().setEnabled(false);
            inputTanggalTerima.setDate(new java.util.Date());
            generateNomorPenerimaan();
            loadSupplier();
            loadItem();
            loadLocation();
            formatHarga();
            
            
              BNBarangMasuk.setOpaque(true);
              BNBarangMasuk.setContentAreaFilled(true);

              BPBarangMasuk.setOpaque(true);
              BPBarangMasuk.setContentAreaFilled(true);
              hitungTotalData();
              loadDataInbound();
    
        
    }
    
    public javax.swing.JScrollPane asScrollable() {
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(this);
        sp.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return sp;
    }
    
     public final void loadDataInbound() {

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
                 SELECT it.id,
                     s.nama_supplier ,
                                       it.nomor_penerimaan,
                                        l.kode_lokasi,
                                       i.nama_item,
                                       i.satuan,
                                       it.qty
                                FROM inbound_transactions it
                                INNER JOIN suppliers s ON it.supplier_id = s.id
                                INNER JOIN items i ON it.item_id= i.id
                                INNER JOIN locations l ON it.location_id= l.id
                                ORDER BY it.id DESC
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
                        rs.getString("nama_supplier"),
                        rs.getString("nomor_penerimaan"),
                        rs.getString("kode_lokasi"),
                        rs.getString("nama_item"),
                        rs.getString("satuan"),
                        rs.getString("qty")
                    });
                }

                TBarangMasuk.setModel(model);

                // ===== STYLE TABLE =====
                TBarangMasuk.setForeground(new java.awt.Color(33, 37, 41));
                TBarangMasuk.setBackground(java.awt.Color.WHITE);

                TBarangMasuk.setSelectionForeground(java.awt.Color.WHITE);
                TBarangMasuk.setSelectionBackground(
                    new java.awt.Color(0, 153, 204)
                );

                TBarangMasuk.setGridColor(
                    new java.awt.Color(230, 230, 230)
                );

                TBarangMasuk.setRowHeight(31);

                TBarangMasuk.getTableHeader().setForeground(
                    java.awt.Color.BLACK
                );

                TBarangMasuk.getTableHeader().setBackground(
                    new java.awt.Color(245,245,245)
                );

                // Hide ID
                TBarangMasuk.getColumnModel().getColumn(0).setMinWidth(0);
                TBarangMasuk.getColumnModel().getColumn(0).setMaxWidth(0);
                TBarangMasuk.getColumnModel().getColumn(0).setPreferredWidth(0);

                // ===== UPDATE PAGINATION =====
                LPBarangMasuk.setText(
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
           BPBarangMasuk.setEnabled(currentPage > 1);

           // NEXT
           BNBarangMasuk.setEnabled(currentPage < totalPage);

           // STYLE PREVIOUS
           if (BPBarangMasuk.isEnabled()) {

               BPBarangMasuk.setBackground(
                   new java.awt.Color(0,153,204)
               );

               BPBarangMasuk.setForeground(java.awt.Color.WHITE);

           } else {

               BPBarangMasuk.setBackground(
                   new java.awt.Color(220, 220, 220)
               );

               BPBarangMasuk.setForeground(
                   new java.awt.Color(120,120,120)
               );
           }

           // STYLE NEXT
           if (BNBarangMasuk.isEnabled()) {

               BNBarangMasuk.setBackground(
                   new java.awt.Color(0,153,204)
               );

               BNBarangMasuk.setForeground(java.awt.Color.WHITE);

           } else {

               BNBarangMasuk.setBackground(
                   new java.awt.Color(220,220,220)
               );

               BNBarangMasuk.setForeground(
                   new java.awt.Color(120,120,120)
               );
           }
       }
    
    private void hitungTotalData() {

    String sql = "SELECT COUNT(*) AS total FROM inbound_transactions";

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
     
    private void formatHarga(){
      inputHarga.addKeyListener(new KeyAdapter() {

    @Override
    public void keyTyped(KeyEvent evt) {
            char c = evt.getKeyChar();
            String text = inputHarga.getText();
            // hanya angka dan titik
            if (!Character.isDigit(c)
                    && c != '.'
                    && c != KeyEvent.VK_BACK_SPACE) {
                evt.consume();
            }
            // cegah titik lebih dari satu
            if (c == '.' && text.contains(".")) {

                evt.consume();
            }
        }
    });
    }
  private void cariBarangMasuk() {

        String keyword = TSearchBarangMasuk.getText();

        DefaultTableModel model =
            (DefaultTableModel) TBarangMasuk.getModel();

        model.setRowCount(0);

        try {

            Connection conn = DatabaseConfig.getConnection();

            String sql = """
                           SELECT it.id,s.nama_supplier , it.nomor_penerimaan,  l.kode_lokasi,i.nama_item,i.satuan,it.qty
                            FROM inbound_transactions it
                                        INNER JOIN suppliers s ON it.supplier_id = s.id
                                         INNER JOIN items i ON it.item_id= i.id
                                           INNER JOIN locations l ON it.location_id= l.id
                                      WHERE s.nama_supplier LIKE ? 
                                      OR it.nomor_penerimaan LIKE ?
                                    OR it.nomor_penerimaan LIKE ?
                                    OR  l.kode_lokasi LIKE ?
                                    OR i.satuan LIKE ?
                                    OR it.qty LIKE ?
                         ORDER BY it.id DESC
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
                        rs.getString("nama_supplier"),
                        rs.getString("nomor_penerimaan"),
                        rs.getString("kode_lokasi"),
                        rs.getString("nama_item"),
                        rs.getString("satuan"),
                        rs.getString("qty")
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TBarangMasuk = new javax.swing.JTable();
        TSearchBarangMasuk = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        LPBarangMasuk = new javax.swing.JLabel();
        BNBarangMasuk = new javax.swing.JButton();
        BPBarangMasuk = new javax.swing.JButton();
        BSearchBarangMasuk = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        labelNomorPenerimaan = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        inputTanggalTerima = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        inputSupplier = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        inputKodeBarang = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        inputQty = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        labelNamaBarang = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        labelSatuan = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        inputHarga = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        inputKondisiBarang = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        inputLokasi = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();

        jScrollPane1.setViewportView(jEditorPane1);

        setBackground(new java.awt.Color(248, 250, 252));

        jPanel3.setBackground(new java.awt.Color(248, 250, 252));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 20, 20));
        jPanel3.setLayout(new java.awt.BorderLayout());
        setForeground(new java.awt.Color(0, 0, 0));

        jPanel2.setMaximumSize(new java.awt.Dimension(70, 15));
        jPanel2.setMinimumSize(new java.awt.Dimension(70, 15));
        jPanel2.setPreferredSize(new java.awt.Dimension(70, 15));

        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setText("Nama Barang                :");
        jLabel13.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel13.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel13.setPreferredSize(new java.awt.Dimension(70, 15));

        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("Tanggal Terima             :");
        jLabel4.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel4.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel4.setPreferredSize(new java.awt.Dimension(70, 15));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("Input Barang Masuk");
        jLabel1.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel1.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel1.setPreferredSize(new java.awt.Dimension(70, 15));

        jLabel12.setForeground(new java.awt.Color(102, 102, 102));
        jLabel12.setText("Kode Barang                 :");
        jLabel12.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel12.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel12.setPreferredSize(new java.awt.Dimension(70, 15));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 153, 153));
        jLabel8.setText("Barang");
        jLabel8.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel8.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel8.setPreferredSize(new java.awt.Dimension(70, 15));

        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setText("Satuan                          :");
        jLabel15.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel15.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel15.setPreferredSize(new java.awt.Dimension(70, 15));

        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setText("Qty                               :");
        jLabel14.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel14.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel14.setPreferredSize(new java.awt.Dimension(70, 15));

        jLabel11.setForeground(new java.awt.Color(153, 153, 153));

        labelNamaBarang.setForeground(new java.awt.Color(153, 153, 153));
        labelNamaBarang.setText("-");
        labelNamaBarang.setMinimumSize(new java.awt.Dimension(100, 22));

        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("Nomor Penerimaan      :");
        jLabel2.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel2.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel2.setPreferredSize(new java.awt.Dimension(70, 15));

        inputLokasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LOC-A-01-1", "LOC-A-01-2", "LOC-A-01-3" }));
        inputLokasi.setMinimumSize(new java.awt.Dimension(100, 22));
        inputLokasi.addActionListener(this::inputLokasiActionPerformed);

        inputHarga.setMinimumSize(new java.awt.Dimension(100, 22));
        inputHarga.addActionListener(this::inputHargaActionPerformed);

        labelNomorPenerimaan.setForeground(new java.awt.Color(153, 153, 153));
        labelNomorPenerimaan.setText("GR-2026-001");

        btnSimpan.setBackground(new java.awt.Color(39, 174, 96));
        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");
        btnSimpan.setBorderPainted(false);
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);

        inputQty.setMinimumSize(new java.awt.Dimension(100, 22));

        inputKodeBarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ITM-001", "ITM-002", "ITM-003", "ITM-004" }));
        inputKodeBarang.setMinimumSize(new java.awt.Dimension(100, 22));
        inputKodeBarang.addActionListener(this::inputKodeBarangActionPerformed);

        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setText("Harga                           :");
        jLabel6.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel6.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 15));

        jLabel16.setForeground(new java.awt.Color(102, 102, 102));
        jLabel16.setText("Lokasi                           :");
        jLabel16.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel16.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel16.setPreferredSize(new java.awt.Dimension(70, 15));

        inputSupplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PT A", "PT B", "PT C" }));

        jLabel5.setForeground(new java.awt.Color(102, 102, 102));
        jLabel5.setText("Supplier                        :");
        jLabel5.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel5.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel5.setPreferredSize(new java.awt.Dimension(70, 15));

        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("Kondisi Barang             :");
        jLabel7.setMaximumSize(new java.awt.Dimension(70, 15));
        jLabel7.setMinimumSize(new java.awt.Dimension(70, 15));
        jLabel7.setPreferredSize(new java.awt.Dimension(70, 15));

        inputKondisiBarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baik", "Rusak", "Cacat" }));
        inputKondisiBarang.setMinimumSize(new java.awt.Dimension(100, 22));
        inputKondisiBarang.addActionListener(this::inputKondisiBarangActionPerformed);

        labelSatuan.setForeground(new java.awt.Color(153, 153, 153));
        labelSatuan.setText("-");
        labelSatuan.setMinimumSize(new java.awt.Dimension(100, 22));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(80, 80, 80)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelNomorPenerimaan, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(inputTanggalTerima, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(inputSupplier, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 676, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 676, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(70, 70, 70)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(inputLokasi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(inputHarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(inputKondisiBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(inputQty, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(inputKodeBarang, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(labelNamaBarang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(labelSatuan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(177, 177, 177))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelNomorPenerimaan))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputTanggalTerima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(inputSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)))
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(inputKodeBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(labelNamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(labelSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(inputQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(24, 24, 24)
                                        .addComponent(inputLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(inputKondisiBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(inputHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSimpan)
                .addGap(84, 84, 84))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabel17.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel17.setText("Barang Masuk");

        jLabel3.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(142, 157, 166));
        jLabel3.setText("Daftar Barang Masuk.");

        TBarangMasuk.setModel(new javax.swing.table.DefaultTableModel(
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
        TBarangMasuk.setShowGrid(true);
        jScrollPane2.setViewportView(TBarangMasuk);

        TSearchBarangMasuk.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TSearchBarangMasuk.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        TSearchBarangMasuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TSearchBarangMasukKeyReleased(evt);
            }
        });

        jLabel9.setText("Cari data:");

        LPBarangMasuk.setFont(new java.awt.Font("Inter", 0, 10)); // NOI18N
        LPBarangMasuk.setForeground(new java.awt.Color(142, 157, 166));
        LPBarangMasuk.setText("5 dari 10 data ditampilkan");

        BNBarangMasuk.setBackground(new java.awt.Color(0, 153, 204));
        BNBarangMasuk.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        BNBarangMasuk.setForeground(new java.awt.Color(255, 255, 255));
        BNBarangMasuk.setText("Next");
        BNBarangMasuk.setBorderPainted(false);
        BNBarangMasuk.addActionListener(this::BNBarangMasukActionPerformed);

        BPBarangMasuk.setText("Previous");
        BPBarangMasuk.setBorderPainted(false);
        BPBarangMasuk.setEnabled(false);
        BPBarangMasuk.addActionListener(this::BPBarangMasukActionPerformed);

        BSearchBarangMasuk.setBackground(new java.awt.Color(0, 153, 204));
        BSearchBarangMasuk.setForeground(new java.awt.Color(255, 255, 255));
        BSearchBarangMasuk.setText("Cari");
        BSearchBarangMasuk.addActionListener(this::BSearchBarangMasukActionPerformed);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TSearchBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 744, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BSearchBarangMasuk)
                .addGap(0, 27, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(LPBarangMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BPBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(BNBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel17)
                .addGap(2, 2, 2)
                .addComponent(jLabel3)
                .addGap(15, 15, 15)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TSearchBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BSearchBarangMasuk))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BNBarangMasuk)
                    .addComponent(BPBarangMasuk)
                    .addComponent(LPBarangMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        jPanel3.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel4.setBackground(new java.awt.Color(248, 250, 252));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        jLabel1.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel1.setText("Informasi Barang Masuk");

        jLabel19.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(142, 157, 166));
        jLabel19.setText("Silahkan isi form berikut untuk menambahkan data barang masuk, data otomatis masuk ke tabel barang masuk.");

        jLabel2.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel2.setText("Nomor Penerimaan");

        labelNomorPenerimaan.setBackground(new java.awt.Color(197, 203, 209));
        labelNomorPenerimaan.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        labelNomorPenerimaan.setText("GR-2026-001");
        labelNomorPenerimaan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        labelNomorPenerimaan.setEnabled(false);

        jLabel4.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel4.setText("Tanggal Terima");

        jLabel5.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel5.setText("Supplier");

        inputSupplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PT A", "PT B", "PT C" }));

        jLabel12.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel12.setText("Kode Barang");

        inputKodeBarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ITM-001", "ITM-002", "ITM-003", "ITM-004" }));
        inputKodeBarang.setMinimumSize(new java.awt.Dimension(100, 22));
        inputKodeBarang.addActionListener(this::inputKodeBarangActionPerformed);

        jLabel13.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel13.setText("Nama Barang");

        inputQty.setMinimumSize(new java.awt.Dimension(100, 22));

        jLabel15.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel15.setText("Satuan");

        labelNamaBarang.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        labelNamaBarang.setText("-");
        labelNamaBarang.setEnabled(false);
        labelNamaBarang.setMinimumSize(new java.awt.Dimension(100, 22));

        jLabel14.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel14.setText("Qty");

        labelSatuan.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        labelSatuan.setText("-");
        labelSatuan.setEnabled(false);
        labelSatuan.setMinimumSize(new java.awt.Dimension(100, 22));

        jLabel6.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel6.setText("Harga");

        inputHarga.setMinimumSize(new java.awt.Dimension(100, 22));
        inputHarga.addActionListener(this::inputHargaActionPerformed);

        jLabel7.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel7.setText("Kondisi Barang");

        inputKondisiBarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baik", "Rusak", "Cacat" }));
        inputKondisiBarang.setMinimumSize(new java.awt.Dimension(100, 22));
        inputKondisiBarang.addActionListener(this::inputKondisiBarangActionPerformed);

        jLabel16.setForeground(new java.awt.Color(102, 102, 102));
        jLabel16.setText("Lokasi");

        inputLokasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LOC-A-01-1", "LOC-A-01-2", "LOC-A-01-3" }));
        inputLokasi.setMinimumSize(new java.awt.Dimension(100, 22));
        inputLokasi.addActionListener(this::inputLokasiActionPerformed);

        btnSimpan.setBackground(new java.awt.Color(39, 174, 96));
        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");
        btnSimpan.setBorderPainted(false);
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(inputKondisiBarang, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelSatuan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelNomorPenerimaan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputQty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                    .addComponent(inputSupplier, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelNamaBarang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputKodeBarang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputTanggalTerima, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputHarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputLokasi, javax.swing.GroupLayout.Alignment.TRAILING, 0, 457, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(jLabel19)
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(inputTanggalTerima, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(labelNomorPenerimaan, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(5, 5, 5)
                        .addComponent(inputKodeBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(5, 5, 5)
                        .addComponent(inputSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(5, 5, 5)
                        .addComponent(inputQty, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(5, 5, 5)
                        .addComponent(labelNamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(5, 5, 5)
                        .addComponent(inputHarga, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(5, 5, 5)
                        .addComponent(labelSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel16))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputKondisiBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(btnSimpan)
                .addContainerGap())
        );

        jPanel4.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(new java.awt.Color(248, 250, 252));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel10.setFont(new java.awt.Font("Urbanist ExtraBold", 0, 18)); // NOI18N
        jLabel10.setText("Manajemen Barang Masuk");
        jPanel1.add(jLabel10, java.awt.BorderLayout.PAGE_START);

        jLabel18.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(142, 157, 166));
        jLabel18.setText("Kelola dan tambahkan data barang yang masuk ke dalam sistem inventaris.");
        jPanel1.add(jLabel18, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(303, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))

                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 982, Short.MAX_VALUE))
                .addGap(20, 20, 20))

        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()

                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))

                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))

        );
    }// </editor-fold>//GEN-END:initComponents

    private void inputKodeBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKodeBarangActionPerformed
      
        try {
            // Cegah null saat pertama load
            if (inputKodeBarang.getSelectedItem() == null) {
                return;
            }
            String kodeBarang =
                    inputKodeBarang.getSelectedItem().toString();
            Connection conn =
                    DatabaseConfig.getConnection();
            String sql = """
                    SELECT nama_item, satuan
                    FROM items
                    WHERE kode_item = ?
                    """;

            PreparedStatement ps =
                    conn.prepareStatement(sql);
            ps.setString(1, kodeBarang);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String namaBarang =
                        rs.getString("nama_item");
                String satuan =
                        rs.getString("satuan");
                // Update label otomatis
                labelNamaBarang.setText(namaBarang);
                labelSatuan.setText(satuan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_inputKodeBarangActionPerformed

    private void inputLokasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputLokasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputLokasiActionPerformed

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
                          this,
                          "Qty harus lebih dari 0"
                  );
                  return;
              }

              if (inputHarga.getText().trim().isEmpty()) {
                  JOptionPane.showMessageDialog(
                          this,
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
                      this,
                      "Barang masuk berhasil disimpan\n"
                      + "Batch : " + noBatchLot
              );
              clearForm();
              generateNomorPenerimaan();

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
                      this,
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

    private void inputKondisiBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKondisiBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputKondisiBarangActionPerformed

    private void inputHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputHargaActionPerformed

    private void TSearchBarangMasukKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TSearchBarangMasukKeyReleased

    }//GEN-LAST:event_TSearchBarangMasukKeyReleased

    private void BNBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BNBarangMasukActionPerformed
        if (currentPage < totalPage) {
            currentPage++;
            loadDataInbound();
        }
    }//GEN-LAST:event_BNBarangMasukActionPerformed

    private void BPBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BPBarangMasukActionPerformed
        if (currentPage > 1) {
            currentPage--;
            loadDataInbound();
        }
    }//GEN-LAST:event_BPBarangMasukActionPerformed

    private void BSearchBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BSearchBarangMasukActionPerformed
        cariBarangMasuk();
    }//GEN-LAST:event_BSearchBarangMasukActionPerformed

public void generateNomorPenerimaan() {

    try {
           Connection conn = DatabaseConfig.getConnection();
        String sql = """
                SELECT COUNT(*) + 1 as total
                FROM inbound_transactions
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int nomor = rs.getInt("total");
            String nomorFormat = String.format("GR-2026-%03d", nomor);
            labelNomorPenerimaan.setText(nomorFormat);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void loadSupplier() {

    try {

        Connection conn = DatabaseConfig.getConnection();
        String sql = "SELECT id, nama_supplier FROM suppliers";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        inputSupplier.removeAllItems();
        while (rs.next()) {
            int id = rs.getInt("id");
            String nama = rs.getString("nama_supplier");
            supplierMap.put(nama, id);
            inputSupplier.addItem(nama);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void loadItem() {

    try {

        Connection conn = DatabaseConfig.getConnection();

        String sql = """
                SELECT id, kode_item
                FROM items
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        inputKodeBarang.removeAllItems();

        while (rs.next()) {
            int id = rs.getInt("id");
            String kodeBarang = rs.getString("kode_item");
            itemMap.put(kodeBarang, id);
            inputKodeBarang.addItem(kodeBarang);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
public void loadLocation() {

    try {

        Connection conn = DatabaseConfig.getConnection();

        String sql = """
                SELECT id, kode_lokasi
                FROM locations
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        inputLokasi.removeAllItems();

        while (rs.next()) {
            int id = rs.getInt("id");
            String lokasi = rs.getString("kode_lokasi");
            locationMap.put(lokasi, id);
            inputLokasi.addItem(lokasi);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
private void clearForm() {

    // Reset supplier ke item pertama
    inputSupplier.setSelectedIndex(0);
    // Reset kode barang ke item pertama
    inputKodeBarang.setSelectedIndex(0);
    // Reset lokasi ke item pertama
    inputLokasi.setSelectedIndex(0);
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

    PreparedStatement ps =
            conn.prepareStatement(sql);

    ps.setString(1, "BATCH-" + tahun + "%");

    ResultSet rs = ps.executeQuery();

    if (rs.next()) {

        int nomor = rs.getInt("total");

        /*
         FORMAT:
         01
         02
         03
         */
        String urutan =
                String.format("%02d", nomor);

        return "BATCH-" + tahun + urutan;
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
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelNamaBarang;
    private javax.swing.JLabel labelNomorPenerimaan;
    private javax.swing.JLabel labelSatuan;
    // End of variables declaration//GEN-END:variables
}
