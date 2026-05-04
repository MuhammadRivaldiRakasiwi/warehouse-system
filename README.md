# 🏢 Warehouse System - Autentikasi dengan Database TiDB Cloud

Sistem autentikasi lengkap untuk Warehouse System dengan database TiDB Cloud, password hashing BCrypt, dan security best practices.

---

## 🚀 Quick Start

### Prerequisites
- Java 25 atau lebih
- NetBeans
- Maven
- Internet connection

### 1. Build Project (2 menit)
```bash
Di NetBeans:
1. Klik kanan project
2. Pilih "Clean and Build"
3. Tunggu hingga selesai
```

### 2. Test Database Connection (1 menit)
```bash
Di NetBeans:
1. Buka DatabaseTest.java
2. Klik kanan → Run File
3. Lihat hasil test di console
```

### 3. Run Application (1 menit)
```bash
Di NetBeans:
1. Klik kanan project
2. Pilih "Run" atau tekan F6
3. Login form akan muncul
```

### 4. Login
```
Gunakan credentials dari seeder Anda
Jika berhasil, pesan sukses akan muncul
```

---

## 📋 Database Configuration

### Credentials
```
Host: gateway01.ap-southeast-1.prod.aws.tidbcloud.com
Port: 4000
Username: 2bQGoHWjBnMaSNx.root
Password: 8SglvCDWsgNdYHaB
Database: warehouse
```

### Environment File (.env)
```
DB_HOST=gateway01.ap-southeast-1.prod.aws.tidbcloud.com
DB_PORT=4000
DB_USERNAME=2bQGoHWjBnMaSNx.root
DB_PASSWORD=8SglvCDWsgNdYHaB
DB_NAME=warehouse

APP_NAME=Warehouse System
APP_VERSION=1.0.0
LOG_LEVEL=INFO
```

### Database Schema
```sql
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 📁 Project Structure

```
warehouse-system/
├── src/main/java/com/mycompany/warehouse/system/
│   ├── DatabaseConfig.java          - Database connection management
│   ├── UserService.java             - Authentication service
│   ├── User.java                    - User model
│   ├── DatabaseTest.java            - Testing utilities
│   ├── PasswordHashGenerator.java    - Password hash generator
│   ├── form_login.java              - Login form with authentication
│   └── WarehouseSystem.java         - Main application class
├── pom.xml                          - Maven configuration
├── .env                             - Environment configuration
├── .env.example                     - Environment template
├── database_setup.sql               - Database schema
└── README.md                        - This file
```

---

## ✨ Features

### Authentication
- ✅ Login dengan username dan password
- ✅ Password hashing dengan BCrypt
- ✅ Input validation
- ✅ Error handling
- ✅ Success/failure messages

### Security
- ✅ BCrypt password hashing (salt rounds: 10)
- ✅ SQL injection prevention dengan PreparedStatement
- ✅ Input validation
- ✅ SSL database connection
- ✅ Logging untuk audit trail
- ✅ No sensitive data in logs

### Database
- ✅ TiDB Cloud integration
- ✅ MySQL JDBC Driver
- ✅ Connection management
- ✅ Error handling

### Testing
- ✅ Database connection test
- ✅ Authentication test
- ✅ 5 comprehensive test cases

---

## 🔐 Security Features

### Password Security
- PBKDF2 hashing dengan SHA-256
- 10,000 iterations untuk security
- Setiap password di-hash dengan salt unik (16 bytes)
- Password tidak pernah disimpan plain text
- Password tidak di-log

### Database Security
- SSL enabled untuk koneksi
- PreparedStatement untuk SQL injection prevention
- Input validation
- Unique constraints untuk username dan email

### Application Security
- Error handling yang proper
- Logging untuk audit trail
- No sensitive data in logs
- Secure connection string

---

## 🧪 Testing

### Test Database Connection
```bash
Di NetBeans:
1. Buka DatabaseTest.java
2. Klik kanan → Run File
3. Lihat hasil test di console
```

### Test Cases
1. ✓ Database connection
2. ✓ Admin login
3. ✓ Regular user login
4. ✓ Wrong password rejection
5. ✓ Non-existent user rejection

---

## 🛠️ Development

### Generate Password Hash
```bash
Di NetBeans:
1. Buka PasswordHashGenerator.java
2. Klik kanan → Run File
3. Masukkan password untuk generate hash
```

### Add New User (via Code)
```java
UserService.register("newuser", "password123", "email@example.com", "user");
```

### Authenticate User
```java
User user = UserService.authenticate("username", "password");
if (user != null) {
    System.out.println("Login berhasil: " + user.getUsername());
}
```

---

## 📊 Dependencies

### Maven Dependencies
```xml
<!-- MySQL JDBC Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

**Note**: Password hashing menggunakan Java built-in PBKDF2 (SHA-256) dengan 10,000 iterations, tidak memerlukan external library.

---

## 🆘 Troubleshooting

### Build Error
**Problem**: "MySQL Driver tidak ditemukan"
```
Solusi:
1. Clean and Build project
2. Pastikan pom.xml sudah diupdate
3. Restart NetBeans
```

### Database Connection Error
**Problem**: "Koneksi database gagal"
```
Solusi:
1. Verifikasi kredensial di .env file
2. Cek internet connection
3. Pastikan TiDB Cloud cluster aktif
4. Cek firewall settings
```

### Login Error
**Problem**: "Login gagal meski password benar"
```
Solusi:
1. Pastikan tabel users sudah dibuat
2. Verifikasi password di database
3. Pastikan password di-hash dengan BCrypt
4. Lihat log untuk error message
```

### Form tidak muncul
**Problem**: "Login form tidak muncul"
```
Solusi:
1. Restart NetBeans
2. Clean and Build project
3. Check Java Swing library
4. Lihat console untuk error
```

---

## 📝 File Descriptions

### DatabaseConfig.java
Mengelola koneksi ke database TiDB Cloud.

**Methods:**
- `getConnection()` - Mendapatkan koneksi database
- `testConnection()` - Test koneksi database

**Features:**
- MySQL JDBC Driver
- SSL enabled
- Error handling
- Logging

### UserService.java
Service untuk autentikasi dan manajemen user.

**Methods:**
- `authenticate(username, password)` - Login user
- `register(username, password, email, role)` - Registrasi user
- `userExists(username)` - Cek user ada
- `getStoredPassword(username)` - Get password hash

**Features:**
- BCrypt password hashing
- Input validation
- SQL injection prevention
- Logging

### User.java
Model class untuk user.

**Properties:**
- id (int)
- username (String)
- email (String)
- role (String)

### DatabaseTest.java
Testing database dan autentikasi.

**Test Cases:**
1. Database connection test
2. Admin login test
3. Regular user login test
4. Wrong password rejection test
5. Non-existent user rejection test

### PasswordHashGenerator.java
Generate BCrypt password hash.

**Features:**
- Interactive CLI
- Hash generation
- Hash verification

### form_login.java
Login form dengan autentikasi.

**Features:**
- Input validation
- Database authentication
- Error messages
- Success messages

### WarehouseSystem.java
Main application class.

**Features:**
- Database connection test
- Login form display
- Logging

---

## 🔄 Workflow

```
User Input (Username & Password)
         ↓
   Validasi Input
         ↓
   Query Database (SELECT password FROM users WHERE username = ?)
         ↓
   User Ditemukan?
    ├─ Ya → Verifikasi Password dengan BCrypt
    │        ├─ Cocok → Return User Object
    │        └─ Tidak → Return null
    └─ Tidak → Return null
         ↓
   Tampilkan Pesan Hasil
```

---

## 🔑 Default Credentials

Gunakan credentials dari seeder Anda yang sudah ada di database.

---

## ⚠️ Security Notes

- ❌ JANGAN commit .env file ke git
- ❌ JANGAN share .env file dengan orang lain
- ❌ JANGAN push .env file ke repository
- ✅ Simpan .env file di tempat aman
- ✅ Gunakan .env.example untuk template

---

## 📚 Technology Stack

- **Language**: Java 25
- **Framework**: Java Swing
- **Build Tool**: Maven
- **Database**: TiDB Cloud (MySQL Compatible)
- **Password Hashing**: BCrypt
- **JDBC Driver**: MySQL Connector/J 8.0.33

---

## 🎯 Next Steps

### Immediate
- [ ] Build project
- [ ] Test database connection
- [ ] Run aplikasi
- [ ] Login dengan credentials dari seeder

### Short Term
- [ ] Buat form registrasi
- [ ] Buat dashboard
- [ ] Implementasi RBAC

### Medium Term
- [ ] Fitur lupa password
- [ ] Session management
- [ ] Audit logging

---

## 📞 Support

Jika ada pertanyaan atau masalah:
1. Lihat troubleshooting section di atas
2. Check database connection dengan DatabaseTest.java
3. Lihat console logs untuk error message
4. Verifikasi .env file configuration

---

## 📄 License

Warehouse System - Implementasi Autentikasi
© 2024 - All Rights Reserved

---

## ✅ Status

**Version**: 1.0.0
**Status**: ✅ Production Ready
**Last Updated**: 2024

---

**Happy Coding! 🚀**
