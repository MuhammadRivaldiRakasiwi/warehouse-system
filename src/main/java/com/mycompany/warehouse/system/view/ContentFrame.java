/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.warehouse.system.view;
import com.mycompany.warehouse.system.service.DashboardService;
import com.mycompany.warehouse.system.HistoryFrame;
import com.mycompany.warehouse.system.model.User;
import com.mycompany.warehouse.system.service.Session;
import javax.swing.JOptionPane;
import java.awt.CardLayout;
import java.awt.Color; 
import javax.swing.*;
/**
 *
 * @author ndesc
 */
public class ContentFrame extends javax.swing.JFrame {
    
//    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardFrame.class.getName());
 
    // Tambahkan variabel global untuk menyimpan user yang sedang login
    /**
     * Creates new form DashboardFrame
     * 
     */
   // 1. DEKLARASI VARIABEL GLOBAL (Letakkan di bawah nama class)
    private final DashboardStaff menuDashboardStaff;
     private final DashboardManager menuDashboardManager;
    private final BarangPanel menuBarang;
    private final BarangMasukPanel menuBarangMasuk;
    private final BarangKeluarPanel menuBarangKeluar;
    private final SupplierPanel menuSupplier;

    // 2. CONSTRUCTOR UTAMA
    public ContentFrame() {
                initComponents();
setSkalaGambar();
                // 3. INISIALISASI OBJEK PANEL (Dimasukkan ke dalam constructor)
                menuBarang = new BarangPanel();
                menuDashboardStaff = new DashboardStaff();
                  menuDashboardManager = new DashboardManager();
                menuBarangMasuk = new BarangMasukPanel();
                menuBarangKeluar = new BarangKeluarPanel();
                menuSupplier = new SupplierPanel();

                // 4. MASUKKAN PANEL KE CARDLAYOUT
                panelContent.add(menuBarang, "cardBarang");
                panelContent.add(menuDashboardStaff, "dashboardStaff");
                panelContent.add(menuDashboardManager, "dashboardManager");
                panelContent.add(menuSupplier, "cardSupplier");
                panelContent.add(menuBarangMasuk, "cardBarangMasuk");
                panelContent.add(menuBarangKeluar, "cardBarangKeluar");
                

                // 5. ATUR TAMPILAN AWAL
                aturHakAkses();
          
                setMenuColor(btnMDashboard);

                this.setExtendedState(ContentFrame.MAXIMIZED_BOTH);

        // ... Sisa kode fungsi tombol Anda di bawah ini ...
    }
     private void aturHakAkses() {
         User user = Session.getUser(); 
        if (user != null) { 
            String role = user.getRole(); 
            LNamaLengkap.setText(user.getNamaLengkap());
            if (role.equalsIgnoreCase("staff")) { 
                LMaster.setVisible(false); 
                btnMUser.setVisible(false); 
                btnMBarang.setVisible(false); 
                btnMLokasi.setVisible(false); 
                btnMSupplier.setVisible(false); 
                
                loadDashboardStaff(); 
            } else if (role.equalsIgnoreCase("manager")) {
                    LMaster.setVisible(false); 
                btnMUser.setVisible(false); 
                btnMBarang.setVisible(false); 
                btnMLokasi.setVisible(false); 
                btnMSupplier.setVisible(false); 
                  LTransaction.setVisible(false); 
                btnMBarangMasuk.setVisible(false); 
                btnMBarangKeluar.setVisible(false); 
                btnMTransferBarang.setVisible(false);
               
                loadDashboardManager();
            } else { 
                LTransaction.setVisible(false); 
                btnMBarangMasuk.setVisible(false); 
                btnMBarangKeluar.setVisible(false); 
                btnMTransferBarang.setVisible(false);
               
                loadDashboardAdmin(); // Memuat panel admin
            } 
        } 
    }
      // FUNGSI UNTUK MEMUAT DASHBOARD ADMIN
        private void loadDashboardAdmin() {   
//        lbluser.setText(String.valueOf(DashboardService.getTotalUsers()));
//        lblsupplier.setText(String.valueOf(DashboardService.getTotalSuppliers()));
//        lbllokasi.setText(String.valueOf(DashboardService.getTotalLocations()));
//        lblbarang.setText(String.valueOf(DashboardService.getTotalItems()));
             DashboardAdmin dbAdmin = new DashboardAdmin();
            panelContent.add(dbAdmin, "dashboardAdmin");
            CardLayout cl = (CardLayout) panelContent.getLayout();
            cl.show(panelContent, "dashboardAdmin");        
        }
        
          // FUNGSI UNTUK MEMUAT DASHBOARD STAFF
        private void loadDashboardManager() {

             DashboardManager dbManager = new DashboardManager();
            panelContent.add(dbManager, "dashboardManager");
            CardLayout cl = (CardLayout) panelContent.getLayout();
            cl.show(panelContent, "dashboardManager");
        }
     // FUNGSI UNTUK MEMUAT DASHBOARD STAFF
        private void loadDashboardStaff() {

             DashboardStaff dbStaff = new DashboardStaff();
            panelContent.add(dbStaff, "dashboardStaff");
            CardLayout cl = (CardLayout) panelContent.getLayout();
            cl.show(panelContent, "dashboardStaff");
        }
        
        

       
     
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelNavbar = new javax.swing.JPanel();
        btnLogout = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        LNamaLengkap = new javax.swing.JLabel();
        LGambar = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        panelSidebar = new javax.swing.JPanel();
        btnMBarang = new javax.swing.JButton();
        btnMLokasi = new javax.swing.JButton();
        btnMDashboard = new javax.swing.JButton();
        btnMUser = new javax.swing.JButton();
        btnMSupplier = new javax.swing.JButton();
        btnMBarangMasuk = new javax.swing.JButton();
        btnMBarangKeluar = new javax.swing.JButton();
        btnMTransferBarang = new javax.swing.JButton();
        LTransaction = new javax.swing.JLabel();
        LMaster = new javax.swing.JLabel();
        panelContent = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelNavbar.setBackground(new java.awt.Color(153, 255, 255));
        panelNavbar.setPreferredSize(new java.awt.Dimension(200, 43));

        btnLogout.setBackground(new java.awt.Color(255, 51, 51));
        btnLogout.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(23, 81, 141));
        jLabel1.setText("Hi,");

        LNamaLengkap.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        LNamaLengkap.setForeground(new java.awt.Color(23, 81, 141));
        LNamaLengkap.setText("Manager");

        LGambar.setIcon(new javax.swing.ImageIcon("C:\\Users\\ndesc\\KULIAH\\JAVA\\warehouse-system\\src\\LogoWarehouse.png")); // NOI18N
        LGambar.setText("jLabel4");
        LGambar.setPreferredSize(new java.awt.Dimension(270, 150));

        jLabel2.setFont(new java.awt.Font("Rockwell", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(23, 81, 141));
        jLabel2.setText("WAREHOUSE");

        javax.swing.GroupLayout panelNavbarLayout = new javax.swing.GroupLayout(panelNavbar);
        panelNavbar.setLayout(panelNavbarLayout);
        panelNavbarLayout.setHorizontalGroup(
            panelNavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNavbarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LGambar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LNamaLengkap, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(16, 16, 16))
        );
        panelNavbarLayout.setVerticalGroup(
            panelNavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNavbarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelNavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LGambar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNavbarLayout.createSequentialGroup()
                        .addGroup(panelNavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelNavbarLayout.createSequentialGroup()
                                .addComponent(btnLogout)
                                .addGap(0, 8, Short.MAX_VALUE))
                            .addGroup(panelNavbarLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(panelNavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(LNamaLengkap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(panelNavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)))))
                        .addContainerGap())))
        );

        getContentPane().add(panelNavbar, java.awt.BorderLayout.PAGE_START);

        panelSidebar.setBackground(new java.awt.Color(153, 255, 255));
        panelSidebar.setForeground(new java.awt.Color(255, 255, 255));
        panelSidebar.setPreferredSize(new java.awt.Dimension(150, 373));

        btnMBarang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMBarang.setText("Barang");
        btnMBarang.addActionListener(this::btnMBarangActionPerformed);

        btnMLokasi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMLokasi.setText("Lokasi");
        btnMLokasi.addActionListener(this::btnMLokasiActionPerformed);

        btnMDashboard.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMDashboard.setText("Dashboard");
        btnMDashboard.addActionListener(this::btnMDashboardActionPerformed);

        btnMUser.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMUser.setText("User");
        btnMUser.setMaximumSize(new java.awt.Dimension(89, 23));
        btnMUser.setPreferredSize(new java.awt.Dimension(89, 23));

        btnMSupplier.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMSupplier.setText("Supplier");
        btnMSupplier.setMaximumSize(new java.awt.Dimension(89, 23));
        btnMSupplier.setPreferredSize(new java.awt.Dimension(89, 23));
        btnMSupplier.addActionListener(this::btnMSupplierActionPerformed);

        btnMBarangMasuk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMBarangMasuk.setText("Barang Masuk");
        btnMBarangMasuk.addActionListener(this::btnMBarangMasukActionPerformed);

        btnMBarangKeluar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMBarangKeluar.setText("Barang Keluar");
        btnMBarangKeluar.addActionListener(this::btnMBarangKeluarActionPerformed);

        btnMTransferBarang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMTransferBarang.setText("Transfer Barang");
        btnMTransferBarang.addActionListener(this::btnMTransferBarangActionPerformed);

        LTransaction.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LTransaction.setForeground(new java.awt.Color(43, 146, 229));
        LTransaction.setText("Transaction");

        LMaster.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LMaster.setForeground(new java.awt.Color(43, 146, 229));
        LMaster.setText("Master");

        javax.swing.GroupLayout panelSidebarLayout = new javax.swing.GroupLayout(panelSidebar);
        panelSidebar.setLayout(panelSidebarLayout);
        panelSidebarLayout.setHorizontalGroup(
            panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMLokasi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnMDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnMBarang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnMUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnMSupplier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnMBarangMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnMBarangKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnMTransferBarang, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
            .addGroup(panelSidebarLayout.createSequentialGroup()
                .addGroup(panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSidebarLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(LTransaction))
                    .addGroup(panelSidebarLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(LMaster)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelSidebarLayout.setVerticalGroup(
            panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSidebarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMDashboard)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LMaster)
                .addGap(5, 5, 5)
                .addComponent(btnMUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMBarang)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LTransaction)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMTransferBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        getContentPane().add(panelSidebar, java.awt.BorderLayout.LINE_START);

        panelContent.setBackground(new java.awt.Color(51, 51, 51));
        panelContent.setPreferredSize(new java.awt.Dimension(400, 211));
        panelContent.setLayout(new java.awt.CardLayout());
        getContentPane().add(panelContent, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMLokasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMLokasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMLokasiActionPerformed

    private void btnMDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMDashboardActionPerformed
         User user = Session.getUser();

        CardLayout cl = (CardLayout) panelContent.getLayout();

        if (user.getRole().equalsIgnoreCase("staff")) {
            if (DashboardStaff.instance != null) {
                DashboardStaff.instance.loadData();
                DashboardStaff.instance.loadDataCount();
            }
            cl.show(panelContent, "dashboardStaff");

        }else if (user.getRole().equalsIgnoreCase("manager")) {
            if (DashboardManager.instance != null) {
//                DashboardManager.instance.loadDataInventory();
                DashboardManager.instance.loadDataCount();
            }
            cl.show(panelContent, "dashboardManager");

        } else {
              DashboardAdmin.instance.loadDataInventory();
              DashboardAdmin.instance.loadDataAktifitas();
                DashboardAdmin.instance.loadDataCount();
            cl.show(panelContent, "dashboardAdmin");
        }

             setMenuColor(btnMDashboard); 
    }//GEN-LAST:event_btnMDashboardActionPerformed

    private void btnMBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMBarangActionPerformed
    CardLayout cl = (CardLayout) panelContent.getLayout();
        cl.show(panelContent, "cardBarang");
         setMenuColor(btnMBarang); 
    }//GEN-LAST:event_btnMBarangActionPerformed

    private void btnMBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMBarangMasukActionPerformed
          CardLayout cl = (CardLayout) panelContent.getLayout();
          cl.show(panelContent, "cardBarangMasuk");
         setMenuColor(btnMBarangMasuk); 
         menuBarangMasuk.generateNomorPenerimaan();
            menuBarangMasuk.loadSupplier();
            menuBarangMasuk.loadItem();
            menuBarangMasuk.loadLocation();
    }//GEN-LAST:event_btnMBarangMasukActionPerformed

    private void btnMBarangKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMBarangKeluarActionPerformed
        // TODO add your handling code here:
             CardLayout cl = (CardLayout) panelContent.getLayout();
          cl.show(panelContent, "cardBarangKeluar");
         setMenuColor(btnMBarangKeluar); 
         menuBarangKeluar.loadBarangFromInventory();
    }//GEN-LAST:event_btnMBarangKeluarActionPerformed

    private void btnMTransferBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMTransferBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMTransferBarangActionPerformed

    private void btnMSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMSupplierActionPerformed
        CardLayout cl = (CardLayout) panelContent.getLayout();
        cl.show(panelContent, "cardSupplier");
         setMenuColor(btnMSupplier); 
    }//GEN-LAST:event_btnMSupplierActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // 1. Konfirmasi ke user agar tidak tidak sengaja logout
        int opsi = javax.swing.JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin logout?", "Konfirmasi Logout",
            javax.swing.JOptionPane.YES_NO_OPTION);

        if (opsi == javax.swing.JOptionPane.YES_OPTION) {
            // 2. Bersihkan data user dari Session
            com.mycompany.warehouse.system.service.Session.clear();

            // 3. Tampilkan kembali form Login
            new com.mycompany.warehouse.system.view.FormLogin().setVisible(true);

            // 4. Tutup dashboard saat ini
            this.dispose();
        } // Menutup aplikasi
    }//GEN-LAST:event_btnLogoutActionPerformed
private void setMenuColor(JButton activeBtn) {
     // Contoh menggunakan Opsi 1 (Kontras Profesional)
    Color warnaSidebar = new Color(153, 255, 255);
    Color warnaAktif = new Color(0, 153, 153); // Hijau-Biru gelap
    Color teksPutih = Color.WHITE;
    Color teksHitam = Color.BLACK;

    // Reset semua tombol ke warna standar sidebar
    btnMDashboard.setBackground(warnaSidebar);
    btnMDashboard.setForeground(teksHitam);
    
    btnMBarang.setBackground(warnaSidebar);
    btnMBarang.setForeground(teksHitam);
    
    btnMSupplier.setBackground(warnaSidebar);
    btnMSupplier.setForeground(teksHitam);
    
    btnMUser.setBackground(warnaSidebar);
    btnMUser.setForeground(teksHitam);
    
    btnMLokasi.setBackground(warnaSidebar);
    btnMLokasi.setForeground(teksHitam);
   
    
    btnMBarangMasuk.setBackground(warnaSidebar);
    btnMBarangMasuk.setForeground(teksHitam);
    
    btnMBarangKeluar.setBackground(warnaSidebar);
    btnMBarangKeluar.setForeground(teksHitam);
    

    // Set tombol aktif
    activeBtn.setBackground(warnaAktif);
    activeBtn.setForeground(teksPutih); // Ubah teks jadi putih agar kontras
}

private void setSkalaGambar() {
         // Mengambil ikon asli dari label
    ImageIcon iconAsli = new ImageIcon(getClass().getResource("/logo1.png"));
    
    // Mengubah ukuran gambar sesuai ukuran label saat ini
    java.awt.Image img = iconAsli.getImage();
    
    
    // Memasang kembali gambar yang sudah di-resize ke label
    LGambar.setIcon(new ImageIcon(img));
}

    /**
     * @param args the command line arguments
     */
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LGambar;
    private javax.swing.JLabel LMaster;
    private javax.swing.JLabel LNamaLengkap;
    private javax.swing.JLabel LTransaction;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMBarang;
    private javax.swing.JButton btnMBarangKeluar;
    private javax.swing.JButton btnMBarangMasuk;
    private javax.swing.JButton btnMDashboard;
    private javax.swing.JButton btnMLokasi;
    private javax.swing.JButton btnMSupplier;
    private javax.swing.JButton btnMTransferBarang;
    private javax.swing.JButton btnMUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelNavbar;
    private javax.swing.JPanel panelSidebar;
    // End of variables declaration//GEN-END:variables
}
