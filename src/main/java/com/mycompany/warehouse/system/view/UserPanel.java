/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.warehouse.system.view;

import com.mycompany.warehouse.system.DatabaseConfig;
import com.mycompany.warehouse.system.service.PasswordHashGenerator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * @author ndesc
 */
public class UserPanel extends javax.swing.JPanel {

    private int selectedId = -1;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private final java.util.List<Integer> userIds = new java.util.ArrayList<>();

    public UserPanel() {
        initComponents();
        setupSearch();
        setupTable();
        loadData();
    }

    public javax.swing.JScrollPane asScrollable() {
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(this);
        sp.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        label1 = new java.awt.Label();
        jPanelForm = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        labelDesc = new java.awt.Label();
        jLabelUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabelPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabelNamaLengkap = new javax.swing.JLabel();
        txtNamaLengkap = new javax.swing.JTextField();
        jLabelEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabelRole = new javax.swing.JLabel();
        cmbRole = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabelTable = new javax.swing.JLabel();
        labelTableDesc = new java.awt.Label();
        jPanelTable = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        labelInfo = new java.awt.Label();

        setBackground(new java.awt.Color(244, 245, 246));

        jLabel1.setFont(new java.awt.Font("Urbanist", 1, 16));
        jLabel1.setText("Manajemen User");

        label1.setFont(new java.awt.Font("Inter", 0, 12));
        label1.setForeground(new java.awt.Color(142, 157, 166));
        label1.setText("Kelola semua data user.");

        jPanelForm.setBackground(new java.awt.Color(255, 255, 255));
        jPanelForm.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        jLabelTitle.setFont(new java.awt.Font("Urbanist", 1, 14));
        jLabelTitle.setText("Informasi User");
        labelDesc.setFont(new java.awt.Font("Inter", 0, 12));
        labelDesc.setForeground(new java.awt.Color(142, 157, 166));
        labelDesc.setText("Silahkan isi form berikut untuk menambahkan data user.");

        jLabelUsername.setText("Username");
        txtUsername.setForeground(java.awt.Color.BLACK);
        jLabelPassword.setText("Password");
        txtPassword.setForeground(java.awt.Color.BLACK);
        jLabelNamaLengkap.setText("Nama Lengkap");
        txtNamaLengkap.setForeground(java.awt.Color.BLACK);
        jLabelEmail.setText("Email");
        txtEmail.setForeground(java.awt.Color.BLACK);
        jLabelRole.setText("Role");
        cmbRole.setForeground(java.awt.Color.BLACK);
        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"", "admin", "staff", "manager"}));

        btnSimpan.setBackground(new java.awt.Color(39, 174, 96));
        btnSimpan.setFont(new java.awt.Font("Inter Medium", 0, 12));
        btnSimpan.setForeground(java.awt.Color.WHITE);
        btnSimpan.setText("Simpan Data");
        btnSimpan.setBorderPainted(false);
        btnSimpan.setFocusPainted(false);
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);

        btnReset.setBackground(new java.awt.Color(51, 0, 102));
        btnReset.setFont(new java.awt.Font("Inter", 0, 12));
        btnReset.setForeground(java.awt.Color.WHITE);
        btnReset.setText("Reset");
        btnReset.setBorderPainted(false);
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(this::btnResetActionPerformed);

        btnHapus.setBackground(new java.awt.Color(192, 57, 43));
        btnHapus.setFont(new java.awt.Font("Inter Medium", 0, 12));
        btnHapus.setForeground(java.awt.Color.WHITE);
        btnHapus.setText("Hapus Data");
        btnHapus.setBorderPainted(false);
        btnHapus.setFocusPainted(false);
        btnHapus.addActionListener(this::btnHapusActionPerformed);

        javax.swing.GroupLayout jPanelFormLayout = new javax.swing.GroupLayout(jPanelForm);
        jPanelForm.setLayout(jPanelFormLayout);
        jPanelFormLayout.setHorizontalGroup(jPanelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtUsername)
            .addComponent(jLabelPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtPassword)
            .addComponent(jLabelNamaLengkap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtNamaLengkap)
            .addComponent(jLabelEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtEmail)
            .addComponent(jLabelRole, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cmbRole, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelFormLayout.createSequentialGroup()
                .addComponent(btnSimpan).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnReset).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnHapus).addGap(0, 0, Short.MAX_VALUE)));
        jPanelFormLayout.setVerticalGroup(jPanelFormLayout.createSequentialGroup()
            .addComponent(jLabelTitle).addGap(2)
            .addComponent(labelDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12)
            .addComponent(jLabelUsername).addGap(4)
            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabelPassword).addGap(4)
            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabelNamaLengkap).addGap(4)
            .addComponent(txtNamaLengkap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabelEmail).addGap(4)
            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabelRole).addGap(4)
            .addComponent(cmbRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18)
            .addGroup(jPanelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnSimpan).addComponent(btnReset).addComponent(btnHapus)));

        jLabelTable.setFont(new java.awt.Font("Urbanist", 1, 16));
        jLabelTable.setText("Table Data User");
        labelTableDesc.setFont(new java.awt.Font("Inter", 0, 12));
        labelTableDesc.setForeground(new java.awt.Color(142, 157, 166));
        labelTableDesc.setText("List data user.");

        jPanelTable.setBackground(new java.awt.Color(255, 255, 255));
        jPanelTable.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(197, 203, 209), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        txtSearch.setFont(new java.awt.Font("Inter", 0, 12));
        txtSearch.setForeground(java.awt.Color.BLACK);
        txtSearch.setText("Cari data..");
        txtSearch.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(225, 228, 231), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        jTable1.setFont(new java.awt.Font("Inter", 0, 12));
        jTable1.setForeground(java.awt.Color.BLACK);
        jTable1.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"Username", "Nama Lengkap", "Email", "Role"}));
        jTable1.setGridColor(new java.awt.Color(225, 228, 231));
        jTable1.setRowHeight(28);
        jTable1.setSelectionBackground(new java.awt.Color(184, 207, 229));
        jTable1.setSelectionForeground(java.awt.Color.BLACK);
        jTable1.setShowGrid(true);
        jScrollPane1.setViewportView(jTable1);

        labelInfo.setFont(new java.awt.Font("Inter", 0, 10));
        labelInfo.setForeground(new java.awt.Color(142, 157, 166));
        labelInfo.setText("Total data user.");

        javax.swing.GroupLayout jPanelTableLayout = new javax.swing.GroupLayout(jPanelTable);
        jPanelTable.setLayout(jPanelTableLayout);
        jPanelTableLayout.setHorizontalGroup(jPanelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtSearch).addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        jPanelTableLayout.setVerticalGroup(jPanelTableLayout.createSequentialGroup()
            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(labelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addGap(30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jLabelTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelTableDesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30)));
        layout.setVerticalGroup(layout.createSequentialGroup().addGap(20)
            .addComponent(jLabel1).addGap(0)
            .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(15)
            .addComponent(jPanelForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(20)
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(15)
            .addComponent(jLabelTable).addGap(0)
            .addComponent(labelTableDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
            .addComponent(jPanelTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(20, Short.MAX_VALUE));
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        simpanData();
    }//GEN-LAST:event_btnSimpanActionPerformed
    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        clearForm();
    }//GEN-LAST:event_btnResetActionPerformed
    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusData();
    }//GEN-LAST:event_btnHapusActionPerformed

    // ============================================================
    // BUSINESS LOGIC
    // ============================================================

    public final void loadData() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Username", "Nama Lengkap", "Email", "Role"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        userIds.clear();
        try (Connection conn = DatabaseConfig.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT id, username, nama_lengkap, email, role FROM users ORDER BY id ASC");
            while (rs.next()) {
                userIds.add(rs.getInt("id"));
                model.addRow(new Object[]{
                    rs.getString("username"),
                    rs.getString("nama_lengkap"),
                    rs.getString("email"),
                    rs.getString("role")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
        jTable1.setModel(model);
        rowSorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(rowSorter);
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Renderer hitam
        javax.swing.table.DefaultTableCellRenderer r = new javax.swing.table.DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(javax.swing.JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setForeground(java.awt.Color.BLACK);
                setBackground(sel ? new java.awt.Color(184, 207, 229) : java.awt.Color.WHITE);
                setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        };
        jTable1.setDefaultRenderer(Object.class, r);

        // Fit tinggi tabel
        int h = jTable1.getTableHeader().getPreferredSize().height + model.getRowCount() * jTable1.getRowHeight() + 2;
        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, Math.max(h, 60)));
        jPanelTable.revalidate();

        labelInfo.setText("Total " + model.getRowCount() + " data user.");
    }

    private void simpanData() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String namaLengkap = txtNamaLengkap.getText().trim();
        String email = txtEmail.getText().trim();
        String role = cmbRole.getSelectedItem() != null ? cmbRole.getSelectedItem().toString() : "";

        if (username.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Role tidak boleh kosong!");
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (selectedId == -1) {
                if (password.isEmpty()) { JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!"); return; }
                String hash = PasswordHashGenerator.hashPassword(password);
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, password_hash, nama_lengkap, email, role, created_at, updated_at) VALUES (?,?,?,?,?,NOW(),NOW())");
                ps.setString(1, username); ps.setString(2, hash); ps.setString(3, namaLengkap);
                ps.setString(4, email); ps.setString(5, role);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User berhasil ditambahkan!");
            } else {
                String sql = "UPDATE users SET username=?, nama_lengkap=?, email=?, role=?, updated_at=NOW()";
                if (!password.isEmpty()) sql += ", password_hash=?";
                sql += " WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username); ps.setString(2, namaLengkap);
                ps.setString(3, email); ps.setString(4, role);
                if (!password.isEmpty()) {
                    ps.setString(5, PasswordHashGenerator.hashPassword(password));
                    ps.setInt(6, selectedId);
                } else {
                    ps.setInt(5, selectedId);
                }
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User berhasil diupdate!");
            }
            clearForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage());
        }
    }

    private void hapusData() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Pilih user dari tabel!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Yakin hapus user ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConfig.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?");
                ps.setInt(1, selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User berhasil dihapus!");
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + e.getMessage());
            }
        }
    }

    private void clearForm() {
        txtUsername.setText(""); txtPassword.setText(""); txtNamaLengkap.setText("");
        txtEmail.setText(""); cmbRole.setSelectedIndex(0);
        selectedId = -1; btnSimpan.setText("Simpan Data");
        txtUsername.requestFocus(); loadData();
    }

    private void setupSearch() {
        final String ph = "Cari data..";
        txtSearch.setForeground(new java.awt.Color(150, 150, 150));
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().equals(ph)) { txtSearch.setText(""); txtSearch.setForeground(java.awt.Color.BLACK); }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().isEmpty()) { txtSearch.setText(ph); txtSearch.setForeground(new java.awt.Color(150,150,150)); }
            }
        });
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });
    }

    private void filter() {
        String kw = txtSearch.getText();
        if (kw.equals("Cari data..")) kw = "";
        if (rowSorter != null) rowSorter.setRowFilter(kw.trim().isEmpty() ? null :
                RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(kw)));
    }

    private void setupTable() {
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int vr = jTable1.getSelectedRow(); if (vr < 0) return;
                int row = jTable1.convertRowIndexToModel(vr);
                if (row >= 0 && row < userIds.size()) {
                    selectedId = userIds.get(row);
                    javax.swing.table.TableModel m = jTable1.getModel();
                    txtUsername.setText(String.valueOf(m.getValueAt(row, 0)));
                    txtNamaLengkap.setText(String.valueOf(m.getValueAt(row, 1)));
                    txtEmail.setText(String.valueOf(m.getValueAt(row, 2)));
                    cmbRole.setSelectedItem(String.valueOf(m.getValueAt(row, 3)));
                    txtPassword.setText("");
                    btnSimpan.setText("Update Data");
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<String> cmbRole;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelEmail;
    private javax.swing.JLabel jLabelNamaLengkap;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JLabel jLabelRole;
    private javax.swing.JLabel jLabelTable;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelUsername;
    private javax.swing.JPanel jPanelForm;
    private javax.swing.JPanel jPanelTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private java.awt.Label label1;
    private java.awt.Label labelDesc;
    private java.awt.Label labelInfo;
    private java.awt.Label labelTableDesc;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNamaLengkap;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
