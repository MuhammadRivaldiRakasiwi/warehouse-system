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
    private DashboardStaff menuDashboardStaff;
    private DashboardManager menuDashboardManager;
    private BarangPanel menuBarang;
    private UserPanel menuUser;
    private RakPanel menuLokasi;
    private BarangMasukPanel menuBarangMasuk;
    private BarangKeluarPanel menuBarangKeluar;
    private SupplierPanel menuSupplier;
    private TransferBarangPanel menuTransferBarang;

    // 2. CONSTRUCTOR UTAMA
    public ContentFrame() {
                initComponents();

                // Panel hanya di-init saat dibutuhkan (lazy loading)
                // Tidak perlu load semua panel + data sekaligus

                // Atur tampilan awal (hanya load dashboard sesuai role)
                aturHakAkses();
          
                setMenuColor(btnMDashboard);

                this.setExtendedState(ContentFrame.MAXIMIZED_BOTH);
    }
     private void aturHakAkses() {
         User user = Session.getUser(); 
        if (user != null) { 
            String role = user.getRole(); 
            LNamaLengkap.setText(user.getNamaLengkap());
            LEmail.setText(user.getEmail());
            if (role.equalsIgnoreCase("staff")) { 
                LMaster.setVisible(false); 
                SMaster.setVisible(false); 
                btnMUser.setVisible(false); 
                btnMBarang.setVisible(false); 
                btnMLokasi.setVisible(false); 
                btnMSupplier.setVisible(false); 
                
                loadDashboardStaff(); 
            } else if (role.equalsIgnoreCase("manager")) {
                LMaster.setVisible(false); 
                SMaster.setVisible(false); 
                btnMUser.setVisible(false); 
                btnMBarang.setVisible(false); 
                btnMLokasi.setVisible(false); 
                btnMSupplier.setVisible(false); 
                  LTransaction.setVisible(false); 
                  STransaction.setVisible(false);
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
             DashboardAdmin dbAdmin = new DashboardAdmin();
            javax.swing.JScrollPane sp = new javax.swing.JScrollPane(dbAdmin);
            sp.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            sp.getVerticalScrollBar().setUnitIncrement(16);
            sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            panelContent.add(sp, "dashboardAdmin");
            CardLayout cl = (CardLayout) panelContent.getLayout();
            cl.show(panelContent, "dashboardAdmin");        
        }
        
          // FUNGSI UNTUK MEMUAT DASHBOARD MANAGER
        private void loadDashboardManager() {
             DashboardManager dbManager = new DashboardManager();
            javax.swing.JScrollPane sp = new javax.swing.JScrollPane(dbManager);
            sp.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            sp.getVerticalScrollBar().setUnitIncrement(16);
            sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            panelContent.add(sp, "dashboardManager");
            CardLayout cl = (CardLayout) panelContent.getLayout();
            cl.show(panelContent, "dashboardManager");
        }
     // FUNGSI UNTUK MEMUAT DASHBOARD STAFF
        private void loadDashboardStaff() {
             DashboardStaff dbStaff = new DashboardStaff();
            javax.swing.JScrollPane sp = new javax.swing.JScrollPane(dbStaff);
            sp.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            sp.getVerticalScrollBar().setUnitIncrement(16);
            sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            panelContent.add(sp, "dashboardStaff");
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
        jLabel2 = new javax.swing.JLabel();
        LNamaLengkap = new javax.swing.JLabel();
        LEmail = new javax.swing.JLabel();
        panelSidebar = new javax.swing.JPanel();
        btnMBarang = new javax.swing.JButton();
        btnMLokasi = new javax.swing.JButton();
        btnMDashboard = new javax.swing.JButton();
        btnMUser = new javax.swing.JButton();
        btnMSupplier = new javax.swing.JButton();
        btnMBarangMasuk = new javax.swing.JButton();
        btnMBarangKeluar = new javax.swing.JButton();
        btnMTransferBarang = new javax.swing.JButton();
        LMaster = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        LTransaction = new javax.swing.JLabel();
        STransaction = new javax.swing.JSeparator();
        SMaster = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        panelContent = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelNavbar.setBackground(new java.awt.Color(255, 255, 255));
        panelNavbar.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(220, 220, 220)));
        panelNavbar.setPreferredSize(new java.awt.Dimension(200, 50));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Urbanist Black", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(37, 99, 235));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Gudangin.aja");

        LNamaLengkap.setFont(new java.awt.Font("Urbanist", 1, 12)); // NOI18N
        LNamaLengkap.setForeground(new java.awt.Color(30, 41, 59));
        LNamaLengkap.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        LNamaLengkap.setText("Admin1");

        LEmail.setFont(new java.awt.Font("Urbanist", 0, 10)); // NOI18N
        LEmail.setForeground(new java.awt.Color(100, 116, 139));
        LEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        LEmail.setText("admin1@gmail.com");

        javax.swing.GroupLayout panelNavbarLayout = new javax.swing.GroupLayout(panelNavbar);
        panelNavbar.setLayout(panelNavbarLayout);
        panelNavbarLayout.setHorizontalGroup(
            panelNavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNavbarLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 296, Short.MAX_VALUE)
                .addGroup(panelNavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LNamaLengkap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(34, 34, 34))
        );
        panelNavbarLayout.setVerticalGroup(
            panelNavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNavbarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelNavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNavbarLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNavbarLayout.createSequentialGroup()
                        .addComponent(LNamaLengkap)
                        .addGap(0, 0, 0)
                        .addComponent(LEmail)
                        .addContainerGap(9, Short.MAX_VALUE))))
        );

        getContentPane().add(panelNavbar, java.awt.BorderLayout.PAGE_START);

        panelSidebar.setBackground(new java.awt.Color(37, 99, 235));
        panelSidebar.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, new java.awt.Color(30, 80, 200)));
        panelSidebar.setForeground(new java.awt.Color(255, 255, 255));
        panelSidebar.setPreferredSize(new java.awt.Dimension(160, 373));

        btnMBarang.setBackground(new java.awt.Color(255, 255, 255));
        btnMBarang.setFont(new java.awt.Font("Urbanist", 0, 12)); // NOI18N
        btnMBarang.setForeground(new java.awt.Color(37, 99, 235));
        btnMBarang.setText("Barang");
        btnMBarang.setBorderPainted(false);
        btnMBarang.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMBarang.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnMBarang.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnMBarang.addActionListener(this::btnMBarangActionPerformed);

        btnMLokasi.setBackground(new java.awt.Color(255, 255, 255));
        btnMLokasi.setFont(new java.awt.Font("Urbanist", 0, 12)); // NOI18N
        btnMLokasi.setForeground(new java.awt.Color(37, 99, 235));
        btnMLokasi.setText("Rak/Lemari");
        btnMLokasi.setBorderPainted(false);
        btnMLokasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMLokasi.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnMLokasi.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnMLokasi.addActionListener(this::btnMLokasiActionPerformed);

        btnMDashboard.setBackground(new java.awt.Color(255, 255, 255));
        btnMDashboard.setFont(new java.awt.Font("Urbanist", 0, 12)); // NOI18N
        btnMDashboard.setForeground(new java.awt.Color(37, 99, 235));
        btnMDashboard.setText("Dashboard");
        btnMDashboard.setBorderPainted(false);
        btnMDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMDashboard.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnMDashboard.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnMDashboard.addActionListener(this::btnMDashboardActionPerformed);

        btnMUser.setBackground(new java.awt.Color(255, 255, 255));
        btnMUser.setFont(new java.awt.Font("Urbanist", 0, 12)); // NOI18N
        btnMUser.setForeground(new java.awt.Color(37, 99, 235));
        btnMUser.setText("User");
        btnMUser.setBorderPainted(false);
        btnMUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMUser.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnMUser.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnMUser.setPreferredSize(new java.awt.Dimension(130, 30));
        btnMUser.addActionListener(this::btnMUserActionPerformed);

        btnMSupplier.setBackground(new java.awt.Color(255, 255, 255));
        btnMSupplier.setFont(new java.awt.Font("Urbanist", 0, 12)); // NOI18N
        btnMSupplier.setForeground(new java.awt.Color(37, 99, 235));
        btnMSupplier.setText("Supplier");
        btnMSupplier.setBorderPainted(false);
        btnMSupplier.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMSupplier.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnMSupplier.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnMSupplier.setPreferredSize(new java.awt.Dimension(130, 30));
        btnMSupplier.addActionListener(this::btnMSupplierActionPerformed);

        btnMBarangMasuk.setBackground(new java.awt.Color(255, 255, 255));
        btnMBarangMasuk.setFont(new java.awt.Font("Urbanist", 0, 12)); // NOI18N
        btnMBarangMasuk.setForeground(new java.awt.Color(37, 99, 235));
        btnMBarangMasuk.setText("Barang Masuk");
        btnMBarangMasuk.setBorderPainted(false);
        btnMBarangMasuk.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMBarangMasuk.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnMBarangMasuk.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnMBarangMasuk.addActionListener(this::btnMBarangMasukActionPerformed);

        btnMBarangKeluar.setBackground(new java.awt.Color(255, 255, 255));
        btnMBarangKeluar.setFont(new java.awt.Font("Urbanist", 0, 12)); // NOI18N
        btnMBarangKeluar.setForeground(new java.awt.Color(37, 99, 235));
        btnMBarangKeluar.setText("Barang Keluar");
        btnMBarangKeluar.setBorderPainted(false);
        btnMBarangKeluar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMBarangKeluar.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnMBarangKeluar.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnMBarangKeluar.addActionListener(this::btnMBarangKeluarActionPerformed);

        btnMTransferBarang.setBackground(new java.awt.Color(255, 255, 255));
        btnMTransferBarang.setFont(new java.awt.Font("Urbanist", 0, 12)); // NOI18N
        btnMTransferBarang.setForeground(new java.awt.Color(37, 99, 235));
        btnMTransferBarang.setText("Transfer Barang");
        btnMTransferBarang.setBorderPainted(false);
        btnMTransferBarang.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMTransferBarang.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnMTransferBarang.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnMTransferBarang.addActionListener(this::btnMTransferBarangActionPerformed);

        LMaster.setFont(new java.awt.Font("Urbanist", 1, 11)); // NOI18N
        LMaster.setForeground(new java.awt.Color(191, 219, 254));
        LMaster.setText("Data Master");
        LMaster.setEnabled(true);

        btnLogout.setBackground(new java.awt.Color(239, 68, 68));
        btnLogout.setFont(new java.awt.Font("Urbanist Medium", 1, 12)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.setBorderPainted(false);
        btnLogout.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLogout.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        LTransaction.setFont(new java.awt.Font("Urbanist", 1, 11)); // NOI18N
        LTransaction.setForeground(new java.awt.Color(191, 219, 254));
        LTransaction.setText("Logistik");
        LTransaction.setEnabled(true);

        STransaction.setForeground(new java.awt.Color(59, 130, 246));

        SMaster.setForeground(new java.awt.Color(59, 130, 246));

        jSeparator3.setForeground(new java.awt.Color(59, 130, 246));

        javax.swing.GroupLayout panelSidebarLayout = new javax.swing.GroupLayout(panelSidebar);
        panelSidebar.setLayout(panelSidebarLayout);
        panelSidebarLayout.setHorizontalGroup(
            panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSidebarLayout.createSequentialGroup()
                .addGroup(panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelSidebarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelSidebarLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnMUser, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator3)))
                    .addGroup(panelSidebarLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(LMaster, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnMTransferBarang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                                .addComponent(btnMBarangKeluar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnMBarangMasuk, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(LTransaction, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnMSupplier, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnMBarang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnMLokasi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnMDashboard, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(STransaction, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(SMaster)))))
                .addGap(14, 14, 14))
        );
        panelSidebarLayout.setVerticalGroup(
            panelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSidebarLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(btnMDashboard)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(SMaster, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LMaster)
                .addGap(5, 5, 5)
                .addComponent(btnMUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMBarang)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(STransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LTransaction)
                .addGap(5, 5, 5)
                .addComponent(btnMBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMTransferBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogout)
                .addGap(15, 15, 15))
        );

        getContentPane().add(panelSidebar, java.awt.BorderLayout.LINE_START);

        panelContent.setBackground(new java.awt.Color(51, 51, 51));
        panelContent.setPreferredSize(new java.awt.Dimension(400, 211));
        panelContent.setLayout(new java.awt.CardLayout());
        getContentPane().add(panelContent, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMLokasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMLokasiActionPerformed
         if (menuLokasi == null) { menuLokasi = new RakPanel(); panelContent.add(menuLokasi.asScrollable(), "cardLokasi"); }
         CardLayout cl = (CardLayout) panelContent.getLayout();
        cl.show(panelContent, "cardLokasi");
         setMenuColor(btnMLokasi);
    }//GEN-LAST:event_btnMLokasiActionPerformed

    private void btnMDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMDashboardActionPerformed
         User user = Session.getUser();

        CardLayout cl = (CardLayout) panelContent.getLayout();

        if (user.getRole().equalsIgnoreCase("staff")) {
            if (DashboardStaff.instance != null) {
                DashboardStaff.instance.loadDataAktifitas();
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
    if (menuBarang == null) { menuBarang = new BarangPanel(); panelContent.add(menuBarang.asScrollable(), "cardBarang"); }
    CardLayout cl = (CardLayout) panelContent.getLayout();
        cl.show(panelContent, "cardBarang");
         setMenuColor(btnMBarang); 
    }//GEN-LAST:event_btnMBarangActionPerformed

    private void btnMBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMBarangMasukActionPerformed
          if (menuBarangMasuk == null) { menuBarangMasuk = new BarangMasukPanel(); panelContent.add(menuBarangMasuk.asScrollable(), "cardBarangMasuk"); }
          CardLayout cl = (CardLayout) panelContent.getLayout();
          cl.show(panelContent, "cardBarangMasuk");
         setMenuColor(btnMBarangMasuk); 
         menuBarangMasuk.generateNomorPenerimaan();
            menuBarangMasuk.loadSupplier();
            menuBarangMasuk.loadItem();
            menuBarangMasuk.loadLocation();
            menuBarangMasuk.loadDataInbound();
    }//GEN-LAST:event_btnMBarangMasukActionPerformed

    private void btnMBarangKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMBarangKeluarActionPerformed
        if (menuBarangKeluar == null) { menuBarangKeluar = new BarangKeluarPanel(); panelContent.add(menuBarangKeluar.asScrollable(), "cardBarangKeluar"); }
             CardLayout cl = (CardLayout) panelContent.getLayout();
          cl.show(panelContent, "cardBarangKeluar");
         setMenuColor(btnMBarangKeluar); 
         menuBarangKeluar.loadBarangFromInventory();
    }//GEN-LAST:event_btnMBarangKeluarActionPerformed

    private void btnMTransferBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMTransferBarangActionPerformed
        if (menuTransferBarang == null) { menuTransferBarang = new TransferBarangPanel(); panelContent.add(menuTransferBarang.asScrollable(), "cardTransferBarang"); }
             CardLayout cl = (CardLayout) panelContent.getLayout();
          cl.show(panelContent, "cardTransferBarang");
         setMenuColor(btnMTransferBarang); 
         menuTransferBarang.loadDataInventory();
    }//GEN-LAST:event_btnMTransferBarangActionPerformed

    private void btnMSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMSupplierActionPerformed
        if (menuSupplier == null) { menuSupplier = new SupplierPanel(); panelContent.add(menuSupplier.asScrollable(), "cardSupplier"); }
        CardLayout cl = (CardLayout) panelContent.getLayout();
        cl.show(panelContent, "cardSupplier");
         setMenuColor(btnMSupplier); 
         menuSupplier.loadData();
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

    private void btnMUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMUserActionPerformed
          if (menuUser == null) { menuUser = new UserPanel(); panelContent.add(menuUser.asScrollable(), "cardUser"); }
          CardLayout cl = (CardLayout) panelContent.getLayout();
          cl.show(panelContent, "cardUser");
         setMenuColor(btnMUser); 
    }//GEN-LAST:event_btnMUserActionPerformed
private void setMenuColor(JButton activeBtn) {
    // Sidebar biru, tombol putih. Aktif = biru lebih terang
    Color warnaNormal = new Color(255, 255, 255);   // Putih
    Color warnaAktif = new Color(219, 234, 254);    // Biru muda (aktif)
    Color teksNormal = new Color(37, 99, 235);      // Biru
    Color teksAktif = new Color(29, 78, 186);       // Biru gelap

    // Reset semua tombol ke putih
    btnMDashboard.setBackground(warnaNormal);
    btnMDashboard.setForeground(teksNormal);
    
    btnMBarang.setBackground(warnaNormal);
    btnMBarang.setForeground(teksNormal);
    
    btnMSupplier.setBackground(warnaNormal);
    btnMSupplier.setForeground(teksNormal);
    
    btnMUser.setBackground(warnaNormal);
    btnMUser.setForeground(teksNormal);
    
    btnMLokasi.setBackground(warnaNormal);
    btnMLokasi.setForeground(teksNormal);
   
    btnMBarangMasuk.setBackground(warnaNormal);
    btnMBarangMasuk.setForeground(teksNormal);
    
    btnMBarangKeluar.setBackground(warnaNormal);
    btnMBarangKeluar.setForeground(teksNormal);

    // Set tombol aktif
    activeBtn.setBackground(warnaAktif);
    activeBtn.setForeground(teksAktif);
}

    /**
     * @param args the command line arguments
     */
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LEmail;
    private javax.swing.JLabel LMaster;
    private javax.swing.JLabel LNamaLengkap;
    private javax.swing.JLabel LTransaction;
    private javax.swing.JSeparator SMaster;
    private javax.swing.JSeparator STransaction;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMBarang;
    private javax.swing.JButton btnMBarangKeluar;
    private javax.swing.JButton btnMBarangMasuk;
    private javax.swing.JButton btnMDashboard;
    private javax.swing.JButton btnMLokasi;
    private javax.swing.JButton btnMSupplier;
    private javax.swing.JButton btnMTransferBarang;
    private javax.swing.JButton btnMUser;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelNavbar;
    private javax.swing.JPanel panelSidebar;
    // End of variables declaration//GEN-END:variables

  
}
