package com.mycompany.warehouse.system.model;

/**
 * Model class untuk User
 */
public class User {
    
    private int id;
    private String username;
    private String email;
    private String nama_lengkap;
    private String role;
    
    // Constructor
    public User() {
    }
    
    public User(int id, String username, String email,String nama_lengkap, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nama_lengkap = nama_lengkap;
        this.role = role;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
     public String getNamaLengkap() {
        return nama_lengkap;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public void setNamaLengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                 ", nama_lengkap='" + nama_lengkap + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
