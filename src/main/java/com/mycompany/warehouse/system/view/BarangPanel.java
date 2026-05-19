/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.warehouse.system.view;
import com.mycompany.warehouse.system.DatabaseConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement; // Diperlukan untuk Simpan/Edit/Hapus
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.Timer;


/**
 *
 * @author ndesc
 */
public class BarangPanel extends javax.swing.JPanel {
  private int selectedId = -1;
private javax.swing.Timer searchTimer;

    private int currentPage = 1;
    private final int dataPerPage = 10;

    private int totalData = 0;
    private int totalPage = 0;
    /**
     * Creates new form BarangPanel
     */
    public BarangPanel() {
        initComponents();
        
              BNBarang.setOpaque(true);
              BNBarang.setContentAreaFilled(true);

              BPBarang.setOpaque(true);
              BPBarang.setContentAreaFilled(true);
           hitungTotalData();
            loadData(); // Tambahkan ini
    }
        public javax.swing.JScrollPane asScrollable() {
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(this);
        sp.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return sp;
    }
    
        
         private void cariBarang() {

        String keyword = TSearchBarang.getText();

        DefaultTableModel model =
            (DefaultTableModel) tableBarang.getModel();

        model.setRowCount(0);

        try {

            Connection conn = DatabaseConfig.getConnection();

            String sql = """
                        SELECT
                             id,
                             kode_item,
                             nama_item,
                             kategori,
                             satuan
                         FROM items
                         WHERE
                             status_aktif = 1
                             AND (
                                 kode_item LIKE ?
                                 OR nama_item LIKE ?
                                 OR kategori LIKE ?
                                 OR satuan LIKE ?
                             )
                         ORDER BY id DESC
                         LIMIT 50
            """;

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ps.setString(4, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                model.addRow(new Object[] {
                     rs.getInt("id"),
                        rs.getString("kode_item"),
                        rs.getString("nama_item"),
                        rs.getString("kategori"),
                        rs.getString("satuan")
                });
            }

        } catch(Exception e) {

            JOptionPane.showMessageDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                e.getMessage()
            );
        }
    }   
    public final void loadData() {
         DefaultTableModel model = new DefaultTableModel();

    model.addColumn("ID");
    model.addColumn("Kode");
    model.addColumn("Nama");
    model.addColumn("Kategori");
    model.addColumn("Satuan");

    /*
     ============================
     HITUNG OFFSET
     ============================
     */
    int offset =
            (currentPage - 1) * dataPerPage;

    try (
        Connection conn =
                DatabaseConfig.getConnection()
    ) {

        String sql = """
            SELECT
                id,
                kode_item,
                nama_item,
                kategori,
                satuan
            FROM items
            WHERE status_aktif = 1
            ORDER BY id DESC
            LIMIT ? OFFSET ?
            """;

        PreparedStatement ps =
                conn.prepareStatement(sql);

        ps.setInt(1, dataPerPage);

        ps.setInt(2, offset);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            model.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("kode_item"),
                rs.getString("nama_item"),
                rs.getString("kategori"),
                rs.getString("satuan")
            });
        }

        tableBarang.setModel(model);

        /*
         ============================
         HIDE ID
         ============================
         */
        tableBarang.getColumnModel()
                .getColumn(0)
                .setMinWidth(0);

        tableBarang.getColumnModel()
                .getColumn(0)
                .setMaxWidth(0);

        /*
         ============================
         UPDATE PAGINATION
         ============================
         */
        LPBarang.setText(
                "Page "
                + currentPage
                + " / "
                + totalPage
        );

        updatePaginationButton();

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
                javax.swing.SwingUtilities
                        .getWindowAncestor(this),
                e.getMessage()
        );
    }
    }
    
     
        private void updatePaginationButton() {
        // PREVIOUS
           BPBarang.setEnabled(currentPage > 1);

           // NEXT
           BNBarang.setEnabled(currentPage < totalPage);

           // STYLE PREVIOUS
           if (BPBarang.isEnabled()) {

               BPBarang.setBackground(
                   new java.awt.Color(0,153,204)
               );

               BPBarang.setForeground(java.awt.Color.WHITE);

           } else {

               BPBarang.setBackground(
                   new java.awt.Color(220, 220, 220)
               );

               BPBarang.setForeground(
                   new java.awt.Color(120,120,120)
               );
           }

           // STYLE NEXT
           if (BNBarang.isEnabled()) {

               BNBarang.setBackground(
                   new java.awt.Color(0,153,204)
               );

               BNBarang.setForeground(java.awt.Color.WHITE);

           } else {

               BNBarang.setBackground(
                   new java.awt.Color(220,220,220)
               );

               BNBarang.setForeground(
                   new java.awt.Color(120,120,120)
               );
           }
       }
    
    private void hitungTotalData() {

    String sql = "SELECT COUNT(*) AS total FROM items WHERE status_aktif = 1";

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
     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        inputNama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        inputKategori = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        inputSatuan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        inputBerat = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        inputPanjang = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        inputLebar = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        inputTinggi = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        inputMinimumStok = new javax.swing.JSpinner();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        TSearchBarang = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableBarang = new javax.swing.JTable();
        BPBarang = new javax.swing.JButton();
        BNBarang = new javax.swing.JButton();
        LPBarang = new javax.swing.JLabel();

        setBackground(new java.awt.Color(248, 250, 252));

        jPanel1.setBackground(new java.awt.Color(248, 250, 252));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        jPanel1.setLayout(new java.awt.BorderLayout(0, 2));

        jLabel1.setFont(new java.awt.Font("Urbanist ExtraBold", 0, 18)); // NOI18N
        jLabel1.setText("Manajemen Barang");
        jPanel1.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        jLabel10.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(142, 157, 166));
        jLabel10.setText("Kelola dan tambahkan data barang ke sistem inventaris.");
        jPanel1.add(jLabel10, java.awt.BorderLayout.PAGE_END);

        jPanel2.setBackground(new java.awt.Color(248, 250, 252));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        jLabel11.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel11.setText("Informasi Barang");

        jLabel12.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(142, 157, 166));
        jLabel12.setText("Silahkan isi form berikut untuk menambahkan data barang, data otomatis masuk ke tabel barang.");

        jLabel2.setText("Nama Barang");
        jLabel2.setPreferredSize(new java.awt.Dimension(60, 16));

        inputNama.addActionListener(this::inputNamaActionPerformed);

        jLabel3.setText("Kategori");

        inputKategori.addActionListener(this::inputKategoriActionPerformed);

        jLabel4.setText("Satuan");

        jLabel5.setText("Berat");

        inputBerat.setModel(new javax.swing.SpinnerNumberModel(0, null, 99999, 1));

        jLabel6.setText("Panjang");

        inputPanjang.setModel(new javax.swing.SpinnerNumberModel(0, null, 99999, 1));

        jLabel7.setText("Lebar");

        inputLebar.setModel(new javax.swing.SpinnerNumberModel(0, null, 99999, 1));

        jLabel8.setText("Tinggi");

        inputTinggi.setModel(new javax.swing.SpinnerNumberModel(0, null, 99999, 1));

        jLabel9.setText("Minimum Stok");

        inputMinimumStok.setModel(new javax.swing.SpinnerNumberModel(0, null, 99999, 1));

        btnSimpan.setBackground(new java.awt.Color(39, 174, 96));
        btnSimpan.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");
        btnSimpan.setBorderPainted(false);
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);

        btnEdit.setBackground(new java.awt.Color(243, 156, 18));
        btnEdit.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setText("Edit");
        btnEdit.setBorderPainted(false);
        btnEdit.addActionListener(this::btnEditActionPerformed);

        btnDelete.setBackground(new java.awt.Color(204, 0, 0));
        btnDelete.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Delete");
        btnDelete.setBorderPainted(false);
        btnDelete.addActionListener(this::btnDeleteActionPerformed);

        btnReset.setBackground(new java.awt.Color(102, 0, 153));
        btnReset.setFont(new java.awt.Font("Urbanist Medium", 0, 12)); // NOI18N
        btnReset.setForeground(new java.awt.Color(255, 255, 255));
        btnReset.setText("Reset");
        btnReset.setBorderPainted(false);
        btnReset.addActionListener(this::btnResetActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(inputTinggi)
                            .addComponent(inputPanjang, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputSatuan, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputNama, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(15, 15, 15))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(260, 260, 260)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputKategori, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputBerat)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputLebar)
                    .addComponent(inputMinimumStok)))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(btnSimpan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReset)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(2, 2, 2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputNama)
                    .addComponent(inputKategori))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputBerat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputPanjang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputLebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputTinggi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputMinimumStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnEdit)
                    .addComponent(btnDelete)
                    .addComponent(btnReset))
                .addContainerGap())
        );

        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel4.setBackground(new java.awt.Color(248, 250, 252));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        jLabel13.setFont(new java.awt.Font("Urbanist", 1, 16)); // NOI18N
        jLabel13.setText("Table Barang");

        jLabel14.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(142, 157, 166));
        jLabel14.setText("List data barang.");

        jLabel15.setText("Cari data:");

        TSearchBarang.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        TSearchBarang.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        TSearchBarang.setToolTipText("");
        TSearchBarang.setActionCommand("<Not Set>");
        TSearchBarang.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(225, 228, 231), 1, true), javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        TSearchBarang.addActionListener(this::TSearchBarangActionPerformed);
        TSearchBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TSearchBarangKeyReleased(evt);
            }
        });

        tableBarang.setModel(new javax.swing.table.DefaultTableModel(
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
        tableBarang.setShowGrid(true);
        tableBarang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableBarangMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableBarang);

        BPBarang.setText("Previous");
        BPBarang.setBorderPainted(false);
        BPBarang.setEnabled(false);
        BPBarang.addActionListener(this::BPBarangActionPerformed);

        BNBarang.setBackground(new java.awt.Color(0, 153, 204));
        BNBarang.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        BNBarang.setForeground(new java.awt.Color(255, 255, 255));
        BNBarang.setText("Next");
        BNBarang.setBorderPainted(false);
        BNBarang.addActionListener(this::BNBarangActionPerformed);

        LPBarang.setFont(new java.awt.Font("Inter", 0, 10)); // NOI18N
        LPBarang.setForeground(new java.awt.Color(142, 157, 166));
        LPBarang.setText("5 dari 10 data ditampilkan");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(TSearchBarang))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jScrollPane2)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(LPBarang, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(BPBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BNBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel14)
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TSearchBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LPBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BNBarang)
                    .addComponent(BPBarang))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel5, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void inputNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputNamaActionPerformed
private String generateKodeItem(Connection conn)
        throws SQLException {

    /*
     =====================================
     AMBIL KODE ITEM TERAKHIR
     =====================================
     */
    String sql = """
            SELECT kode_item
            FROM items
            ORDER BY kode_item DESC
            LIMIT 1
            """;
    PreparedStatement ps =
            conn.prepareStatement(sql);

    ResultSet rs = ps.executeQuery();
    /*
     =====================================
     DEFAULT NOMOR AWAL
     =====================================
     */
    int urutan = 1;

    /*
     =====================================
     JIKA DATA SUDAH ADA
     =====================================
     */
    if (rs.next()) {

        String lastKode =
                rs.getString("kode_item");
        /*
         =====================================
         VALIDASI FORMAT
         CONTOH:
         ITM-011
         =====================================
         */
        if (lastKode != null
                && lastKode.startsWith("ITM-")) {

            /*
             AMBIL ANGKA:
             ITM-011 -> 011
             */
            String angka =
                    lastKode.substring(4);

            /*
             CONVERT KE INTEGER
             */
            urutan =
                    Integer.parseInt(angka) + 1;
        }
    }

    
    return String.format(
            "ITM-%03d",
            urutan
    );
}
    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // --- 1. VALIDASI INPUT TIDAK BOLEH KOSONG ---
            if (inputNama.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama barang tidak boleh kosong!");
                inputNama.requestFocus(); // Arahkan kursor ke input yang kosong
                return;
            }

            if (inputSatuan.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Satuan barang tidak boleh kosong! (Contoh: Pcs, Box)");
                inputSatuan.requestFocus();
                return;
            }

            // Validasi tambahan: Cek jika JSpinner masih bernilai 0 (Opsional)
            if ((int) inputMinimumStok.getValue() <= 0) {
                int opsi = JOptionPane.showConfirmDialog(this, 
                    "Minimum stok bernilai 0 atau kurang. Tetap simpan?", 
                    "Peringatan", JOptionPane.YES_NO_OPTION);
                if (opsi == JOptionPane.NO_OPTION) return;
            }

            // --- 2. PROSES DATABASE ---
            String sql = "INSERT INTO items (kode_item, nama_item, kategori, satuan, berat, panjang, lebar, tinggi, minimum_stok, status_aktif) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConfig.getConnection()) {

                String kodeOtomatis =  generateKodeItem(conn);

                // --- B. EKSEKUSI INSERT ---
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, kodeOtomatis);
                    ps.setString(2, inputNama.getText().trim());
                    ps.setString(3, inputKategori.getText().trim());
                    ps.setString(4, inputSatuan.getText().trim());

                    // Konversi Spinner ke Double/Integer secara aman
                    ps.setDouble(5, Double.parseDouble(inputBerat.getValue().toString()));
                    ps.setDouble(6, Double.parseDouble(inputPanjang.getValue().toString()));
                    ps.setDouble(7, Double.parseDouble(inputLebar.getValue().toString()));
                    ps.setDouble(8, Double.parseDouble(inputTinggi.getValue().toString()));

                    ps.setInt(9, (int) inputMinimumStok.getValue());
                    ps.setInt(10, 1); // status_aktif default 1

                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan dengan Kode: " + kodeOtomatis);

                    clearForm(); // Mengosongkan form setelah sukses
                    loadData();  // Refresh JTable
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal simpan ke Database: " + e.getMessage());
            }
    }//GEN-LAST:event_btnSimpanActionPerformed


    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
      // 1. Validasi: Pastikan ada data yang dipilih (ID tidak -1)
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data di tabel yang ingin diubah!");
            return;
        }

        // 2. Validasi input kosong
        if (inputNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!");
            return;
        }

        String sql = "UPDATE items SET nama_item=?, kategori=?, satuan=?, berat=?, panjang=?, lebar=?, tinggi=?, minimum_stok=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 3. Masukkan data dari form ke query
            ps.setString(1, inputNama.getText().trim());
            ps.setString(2, inputKategori.getText().trim());
            ps.setString(3, inputSatuan.getText().trim());

            // Mengambil nilai Spinner
            ps.setInt(4, (int) inputBerat.getValue());
            ps.setInt(5, (int) inputPanjang.getValue());
            ps.setInt(6, (int) inputLebar.getValue());
            ps.setInt(7, (int) inputTinggi.getValue());
            ps.setInt(8, (int) inputMinimumStok.getValue());

            // Parameter WHERE id
            ps.setInt(9, selectedId);

            // 4. Eksekusi Update
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");

                // 5. Kembalikan kondisi form
                clearForm();    // Fungsi untuk mengosongkan input
         
              
                // 3. ATUR STATUS TOMBOL
                btnSimpan.setEnabled(true); // Mematikan tombol Simpan
                btnEdit.setEnabled(false);    // Pastikan tombol Edit menyala
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Update: " + e.getMessage());
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
         int row = tableBarang.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data di tabel dulu!");
        return;
    }

    // Ambil ID dari kolom index 0, dan Nama dari kolom index 2 (sesuaikan indexnya)
    String id = tableBarang.getValueAt(row, 0).toString();
    String nama = tableBarang.getValueAt(row, 2).toString(); 

    int konfirmasi = JOptionPane.showConfirmDialog(this, 
            "Hapus data " + nama + " (ID: " + id + ")?", 
            "Konfirmasi", JOptionPane.YES_NO_OPTION);

    if (konfirmasi == JOptionPane.YES_OPTION) {
        // Gunakan nama tabel 'items' sesuai query INSERT Anda sebelumnya
        String sql = "DELETE FROM items WHERE id=?"; 
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus");
         
            clearForm(); // Bersihkan form input
             btnSimpan.setEnabled(true); // Mematikan tombol Simpan
                btnEdit.setEnabled(false);    // Pastikan tombol Edit menyala
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Hapus: " + e.getMessage());
        }
    }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void tableBarangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableBarangMouseClicked
        int row = tableBarang.getSelectedRow();
        
    if (row != -1) {
        // 1. Ambil ID dari kolom pertama (index 0)
        String idStr = tableBarang.getValueAt(row, 0).toString();
        selectedId = Integer.parseInt(idStr); 

        // 2. Query ke database untuk mendapatkan data lengkap berdasarkan ID
        String sql = "SELECT * FROM items WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idStr);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // 3. Set data ke Form Input dari hasil query (ResultSet)
                inputNama.setText(rs.getString("nama_item"));
                inputKategori.setText(rs.getString("kategori"));
                inputSatuan.setText(rs.getString("satuan"));
                
                // Mengisi Spinner secara aman
                inputBerat.setValue(rs.getInt("berat"));
                inputPanjang.setValue(rs.getInt("panjang"));
                inputLebar.setValue(rs.getInt("lebar"));
                inputTinggi.setValue(rs.getInt("tinggi"));
                inputMinimumStok.setValue(rs.getInt("minimum_stok"));
                
               // 3. ATUR STATUS TOMBOL
                btnSimpan.setEnabled(false); // Mematikan tombol Simpan
                btnEdit.setEnabled(true);    // Pastikan tombol Edit menyala
               
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error ambil data: " + e.getMessage());
        }
    }
    }//GEN-LAST:event_tableBarangMouseClicked

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        clearForm(); 
    }//GEN-LAST:event_btnResetActionPerformed

    private void inputKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputKategoriActionPerformed

    private void TSearchBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TSearchBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TSearchBarangActionPerformed

    private void TSearchBarangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TSearchBarangKeyReleased
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

                          cariBarang();
                      });

              searchTimer.setRepeats(false);

              searchTimer.start();
    }//GEN-LAST:event_TSearchBarangKeyReleased

    private void BPBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BPBarangActionPerformed
        if (currentPage > 1) {

            currentPage--;

            loadDataAktifitas();
        }
    }//GEN-LAST:event_BPBarangActionPerformed

    private void BNBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BNBarangActionPerformed
        if (currentPage < totalPage) {

            currentPage++;

            loadDataAktifitas();
        }
    }//GEN-LAST:event_BNBarangActionPerformed

private void clearForm() {
    inputNama.setText("");
    inputSatuan.setText("");
    inputKategori.setText("");
    inputBerat.setValue(0);
    inputPanjang.setValue(0);
    inputLebar.setValue(0);
    inputTinggi.setValue(0);
    inputMinimumStok.setValue(0);
    inputNama.requestFocus();
    btnSimpan.setEnabled(true); // Mematikan tombol Simpan
    selectedId = -1; // Reset ID pilihan
       loadData(); // Refresh tabel
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BNBarang;
    private javax.swing.JButton BPBarang;
    private javax.swing.JLabel LPBarang;
    private javax.swing.JTextField TSearchBarang;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JSpinner inputBerat;
    private javax.swing.JTextField inputKategori;
    private javax.swing.JSpinner inputLebar;
    private javax.swing.JSpinner inputMinimumStok;
    private javax.swing.JTextField inputNama;
    private javax.swing.JSpinner inputPanjang;
    private javax.swing.JTextField inputSatuan;
    private javax.swing.JSpinner inputTinggi;
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
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tableBarang;
    // End of variables declaration//GEN-END:variables

    private void loadDataAktifitas() {
        // TODO: implementasi pagination data barang
        loadData();
    }
}
