/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.warehouse.system.view;
//import com.mycompany.warehouse.system.DatabaseConfig;
import com.mycompany.warehouse.system.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.table.DefaultTableModel;import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
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
                    javax.swing.SwingUtilities.getWindowAncestor(this),
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
            javax.swing.SwingUtilities.getWindowAncestor(this),
            e.getMessage()
        );
    }
}
     
   private void formatHarga() {

        inputHarga.setHorizontalAlignment(
            javax.swing.JTextField.RIGHT
    );

    AbstractDocument document =
            (AbstractDocument) inputHarga.getDocument();

    document.setDocumentFilter(new DocumentFilter() {

        @Override
        public void insertString(
                FilterBypass fb,
                int offset,
                String string,
                AttributeSet attr
        ) throws BadLocationException {

            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(
                FilterBypass fb,
                int offset,
                int length,
                String text,
                AttributeSet attrs
        ) throws BadLocationException {

            String oldText =
                    fb.getDocument().getText(
                            0,
                            fb.getDocument().getLength()
                    );

            String newText =
                    oldText.substring(0, offset)
                    + text
                    + oldText.substring(offset + length);

            /*
             hanya angka + titik desimal
             contoh valid:
             100
             1000
             1000.50
             */
            if (newText.matches("^\\d*(\\.\\d{0,2})?$")) {

                super.replace(
                        fb,
                        offset,
                        length,
                        text,
                        attrs
                );
            }
        }
    });
}
  private void cariBarangMasuk() {

        String keyword = TSearchInventory.getText();

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
        TSearchInventory = new javax.swing.JTextField();
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
            .addComponent(jScrollPane3)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(LPBarangMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(BPBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BNBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TSearchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(TSearchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
              clearForm();
              generateNomorPenerimaan();
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

    private void TSearchInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TSearchInventoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TSearchInventoryActionPerformed

    private void TSearchInventoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TSearchInventoryKeyReleased

    }//GEN-LAST:event_TSearchInventoryKeyReleased

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
    private javax.swing.JTextField TSearchInventory;
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
