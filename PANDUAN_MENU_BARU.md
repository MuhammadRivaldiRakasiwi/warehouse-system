# 📖 Panduan Membuat Menu Baru di Warehouse System

Panduan ini menjelaskan langkah-langkah membuat menu baru dari nol, sesuai dengan pola dan struktur yang sudah ada di project ini.

---

## 🏗️ Arsitektur yang Perlu Dipahami

Project ini menggunakan pola **CardLayout** untuk navigasi antar menu. Cara kerjanya:

```
ContentFrame (JFrame utama)
├── panelSidebar  → berisi tombol-tombol menu (btnMBarang, btnMSupplier, dst.)
└── panelContent  → area konten utama, menggunakan CardLayout
    ├── "dashboardAdmin"  → DashboardAdmin.java
    ├── "cardBarang"      → BarangPanel.java
    ├── "cardSupplier"    → SupplierPanel.java
    ├── "cardBarangMasuk" → BarangMasukPanel.java
    └── "cardBarangKeluar"→ BarangKeluarPanel.java
```

Setiap menu adalah sebuah **JPanel** yang disimpan di dalam `panelContent`. Ketika tombol sidebar diklik, CardLayout akan menampilkan panel yang sesuai.

---

## 📁 File yang Perlu Dibuat/Diubah

Untuk membuat 1 menu baru, ada **2 langkah utama**:

| Langkah | File | Aksi |
|---------|------|------|
| 1 | `view/NamaMenuPanel.java` + `.form` | **Buat baru** (Panel konten menu) |
| 2 | `view/ContentFrame.java` | **Edit** (Daftarkan menu ke sidebar) |

---

## LANGKAH 1 — Buat File Panel Baru di NetBeans

### 1.1 Buat JPanel Form

1. Klik kanan pada folder `view` di Project Explorer
2. Pilih **New → JPanel Form...**
3. Isi nama class, contoh: `LokasiPanel`
4. Klik **Finish**

NetBeans akan membuat 2 file sekaligus:
- `LokasiPanel.java` — kode Java
- `LokasiPanel.form` — file desain GUI

### 1.2 Struktur Dasar Panel (Template)

Setelah file dibuat, tambahkan kode berikut sebagai template awal. Sesuaikan nama class dan konten sesuai kebutuhan.

```java
package com.mycompany.warehouse.system.view;

import com.mycompany.warehouse.system.DatabaseConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class LokasiPanel extends javax.swing.JPanel {

    // Variabel untuk menyimpan ID baris yang dipilih di tabel
    private int selectedId = -1;

    public LokasiPanel() {
        initComponents();
        loadData(); // Muat data dari database saat panel dibuka
    }

    // Fungsi untuk mengambil data dari database dan menampilkan ke tabel
    public final void loadData() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");       // Kolom ID (akan disembunyikan)
        model.addColumn("Kode");
        model.addColumn("Nama Lokasi");
        model.addColumn("Keterangan");

        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT id, kode_lokasi, nama_lokasi, keterangan FROM lokasi";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("kode_lokasi"),
                    rs.getString("nama_lokasi"),
                    rs.getString("keterangan")
                });
            }
            tabelLokasi.setModel(model);

            // Sembunyikan kolom ID (index 0) dari tampilan
            tabelLokasi.getColumnModel().getColumn(0).setMinWidth(0);
            tabelLokasi.getColumnModel().getColumn(0).setMaxWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    // Fungsi untuk mengosongkan semua input form
    private void clearForm() {
        inputNama.setText("");
        inputKeterangan.setText("");
        selectedId = -1;
        btnSimpan.setEnabled(true);
        btnEdit.setEnabled(false);
        loadData(); // Refresh tabel
    }

    // ... (initComponents() di-generate otomatis oleh NetBeans, jangan diedit manual)
    // ... (event handler tombol Simpan, Edit, Delete ditambahkan di sini)
}
```

### 1.3 Desain UI di NetBeans GUI Builder

Buka tab **Design** pada file `.form`, lalu tambahkan komponen berikut dari Palette:

| Komponen | Nama Variabel | Fungsi |
|----------|--------------|--------|
| `JLabel` | `jLabel1` | Judul halaman (contoh: "Lokasi") |
| `JTextField` | `inputNama` | Input nama lokasi |
| `JTextField` | `inputKeterangan` | Input keterangan |
| `JButton` | `btnSimpan` | Tombol simpan data baru |
| `JButton` | `btnEdit` | Tombol update data |
| `JButton` | `btnDelete` | Tombol hapus data |
| `JButton` | `btnReset` | Tombol reset/kosongkan form |
| `JTable` | `tabelLokasi` | Tabel untuk menampilkan data |
| `JScrollPane` | `jScrollPane1` | Wrapper untuk JTable |

> **Tips:** Bungkus `JTable` di dalam `JScrollPane` agar tabel bisa di-scroll.

### 1.4 Styling Tombol (Sesuai Standar Project)

Klik kanan tombol → Properties, atur warna sesuai standar project ini:

```
btnSimpan  → Background: (39, 174, 96)  | Foreground: Putih  | Teks: "Simpan"
btnEdit    → Background: (243, 156, 18) | Foreground: Putih  | Teks: "Edit"
btnDelete  → Background: (231, 76, 60)  | Foreground: Putih  | Teks: "Delete"
btnReset   → Background: (149, 165, 166)| Foreground: Putih  | Teks: "Reset"
```

### 1.5 Tambahkan Event Handler Tombol

Double-klik setiap tombol di GUI Builder untuk membuat event handler, lalu isi logikanya:

**Tombol Simpan:**
```java
private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
    // Validasi input tidak boleh kosong
    if (inputNama.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama lokasi tidak boleh kosong!");
        inputNama.requestFocus();
        return;
    }

    String sql = "INSERT INTO lokasi (nama_lokasi, keterangan) VALUES (?, ?)";

    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, inputNama.getText().trim());
        ps.setString(2, inputKeterangan.getText().trim());
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
        clearForm();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
    }
}
```

**Tombol Edit:**
```java
private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {
    if (selectedId == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data di tabel yang ingin diubah!");
        return;
    }

    String sql = "UPDATE lokasi SET nama_lokasi=?, keterangan=? WHERE id=?";

    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, inputNama.getText().trim());
        ps.setString(2, inputKeterangan.getText().trim());
        ps.setInt(3, selectedId);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");
        clearForm();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error update: " + e.getMessage());
    }
}
```

**Tombol Delete:**
```java
private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
    int row = tabelLokasi.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data di tabel dulu!");
        return;
    }

    String id = tabelLokasi.getValueAt(row, 0).toString();
    String nama = tabelLokasi.getValueAt(row, 2).toString();

    int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Hapus data '" + nama + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

    if (konfirmasi == JOptionPane.YES_OPTION) {
        String sql = "DELETE FROM lokasi WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
            clearForm();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error hapus: " + e.getMessage());
        }
    }
}
```

**Klik baris tabel (untuk mengisi form otomatis):**

Tambahkan MouseListener pada `tabelLokasi` di GUI Builder, lalu isi:

```java
private void tabelLokasiMouseClicked(java.awt.event.MouseEvent evt) {
    int row = tabelLokasi.getSelectedRow();

    if (row != -1) {
        // Ambil ID dari kolom tersembunyi (index 0)
        selectedId = Integer.parseInt(tabelLokasi.getValueAt(row, 0).toString());

        // Isi form dari database berdasarkan ID
        String sql = "SELECT * FROM lokasi WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, selectedId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                inputNama.setText(rs.getString("nama_lokasi"));
                inputKeterangan.setText(rs.getString("keterangan"));

                // Atur status tombol: nonaktifkan Simpan, aktifkan Edit
                btnSimpan.setEnabled(false);
                btnEdit.setEnabled(true);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
```

---

## LANGKAH 2 — Daftarkan Menu ke ContentFrame.java

Buka file `ContentFrame.java` dan lakukan 4 perubahan berikut. Semua perubahan dilakukan **di luar blok `initComponents()`** (jangan edit kode yang di-generate NetBeans).

### 2.1 Deklarasi Variabel (Bagian atas class)

Cari bagian deklarasi variabel panel yang sudah ada, tambahkan baris baru:

```java
// Yang sudah ada:
private final BarangPanel menuBarang;
private final BarangMasukPanel menuBarangMasuk;
private final BarangKeluarPanel menuBarangKeluar;
private final SupplierPanel menuSupplier;

// Tambahkan ini:
private final LokasiPanel menuLokasi;
```

### 2.2 Inisialisasi di Constructor

Di dalam constructor `ContentFrame()`, cari bagian inisialisasi panel, tambahkan:

```java
// Yang sudah ada:
menuBarang = new BarangPanel();
menuBarangMasuk = new BarangMasukPanel();
menuBarangKeluar = new BarangKeluarPanel();
menuSupplier = new SupplierPanel();

// Tambahkan ini:
menuLokasi = new LokasiPanel();
```

### 2.3 Daftarkan ke CardLayout

Masih di constructor, cari bagian `panelContent.add(...)`, tambahkan:

```java
// Yang sudah ada:
panelContent.add(menuBarang, "cardBarang");
panelContent.add(menuSupplier, "cardSupplier");
panelContent.add(menuBarangMasuk, "cardBarangMasuk");
panelContent.add(menuBarangKeluar, "cardBarangKeluar");

// Tambahkan ini:
panelContent.add(menuLokasi, "cardLokasi");
```

> **Penting:** String `"cardLokasi"` adalah **nama kartu** yang akan dipakai untuk menampilkan panel ini. Nama ini bebas, tapi harus konsisten.

### 2.4 Isi Event Handler Tombol Sidebar

Cari method `btnMLokasiActionPerformed` yang sudah ada (tapi masih kosong), isi dengan:

```java
private void btnMLokasiActionPerformed(java.awt.event.ActionEvent evt) {
    CardLayout cl = (CardLayout) panelContent.getLayout();
    cl.show(panelContent, "cardLokasi");  // Nama kartu harus sama dengan yang didaftarkan
    setMenuColor(btnMLokasi);             // Highlight tombol aktif di sidebar
}
```

### 2.5 Tambahkan ke setMenuColor (Opsional tapi Disarankan)

Cari method `setMenuColor()`, tambahkan reset warna untuk tombol baru:

```java
private void setMenuColor(JButton activeBtn) {
    // ... kode reset yang sudah ada ...
    
    // Tambahkan baris ini jika belum ada:
    btnMLokasi.setBackground(warnaSidebar);
    btnMLokasi.setForeground(teksHitam);
    
    // Set tombol aktif (sudah ada di akhir method)
    activeBtn.setBackground(warnaAktif);
    activeBtn.setForeground(teksPutih);
}
```

---

## 🔐 Mengatur Hak Akses Menu (Role-Based)

Jika menu baru hanya boleh diakses oleh role tertentu, edit method `aturHakAkses()` di `ContentFrame.java`:

```java
private void aturHakAkses() {
    User user = Session.getUser();
    if (user != null) {
        String role = user.getRole();

        if (role.equalsIgnoreCase("staff")) {
            // Staff tidak bisa lihat menu Master
            LMaster.setVisible(false);
            btnMUser.setVisible(false);
            btnMBarang.setVisible(false);
            btnMLokasi.setVisible(false);   // ← Sembunyikan dari staff
            btnMSupplier.setVisible(false);
            loadDashboardStaff();

        } else {
            // Admin tidak bisa lihat menu Transaksi
            LTransaction.setVisible(false);
            btnMBarangMasuk.setVisible(false);
            btnMBarangKeluar.setVisible(false);
            loadDashboardAdmin();
        }
    }
}
```

---

## ✅ Checklist Sebelum Run

Sebelum menjalankan aplikasi, pastikan semua langkah ini sudah selesai:

- [ ] File `LokasiPanel.java` dan `LokasiPanel.form` sudah dibuat
- [ ] Komponen UI (JTextField, JButton, JTable) sudah ditambahkan di GUI Builder
- [ ] Method `loadData()` sudah diisi dengan query SQL yang benar
- [ ] Event handler semua tombol sudah diisi
- [ ] Variabel `menuLokasi` sudah dideklarasikan di `ContentFrame.java`
- [ ] `menuLokasi = new LokasiPanel()` sudah ada di constructor
- [ ] `panelContent.add(menuLokasi, "cardLokasi")` sudah ditambahkan
- [ ] `btnMLokasiActionPerformed` sudah diisi dengan `cl.show(panelContent, "cardLokasi")`
- [ ] Tabel di database sudah ada (jika belum, buat dulu via SQL)

---

## 🧩 Ringkasan Pola yang Digunakan Project Ini

```
1. Setiap menu = 1 JPanel (extends javax.swing.JPanel)
2. Semua panel disimpan di folder: src/main/java/com/mycompany/warehouse/system/view/
3. Navigasi menggunakan CardLayout di panelContent
4. Koneksi database selalu via: DatabaseConfig.getConnection()
5. Query database selalu menggunakan PreparedStatement (bukan Statement biasa)
6. Setiap panel punya: loadData(), clearForm(), dan event handler tombol
7. Kolom ID di tabel selalu disembunyikan (setMinWidth(0) + setMaxWidth(0))
8. Hak akses diatur di method aturHakAkses() di ContentFrame.java
```

---

## 💡 Tips Tambahan

- **Jangan edit kode di dalam blok `// <editor-fold>` dan `//GEN-BEGIN`** — kode itu di-generate otomatis oleh NetBeans dan akan ditimpa ulang setiap kali Anda mengubah desain di GUI Builder.
- Semua logika bisnis (query, validasi) ditulis **di luar** blok tersebut.
- Gunakan `public final void loadData()` (bukan `private`) jika panel lain perlu memanggil refresh data dari luar.
- Jika panel butuh data dari panel lain saat dibuka (seperti `menuBarangMasuk.loadSupplier()`), panggil method tersebut di dalam event handler tombol sidebar di `ContentFrame.java`.
