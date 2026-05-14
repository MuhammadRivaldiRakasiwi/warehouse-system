/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.warehouse.system.service;

import com.mycompany.warehouse.system.model.User;

/**
 *
 * @author ndesc
 */
public class Session {
    // Variabel statis untuk menyimpan data user yang sedang login
    private static User user;

    // Fungsi untuk menyimpan data user saat login berhasil
    public static void setUser(User user) {
        Session.user = user;
    }

    // Fungsi untuk mengambil data user di halaman mana saja
    public static User getUser() {
        return user;
    }

    // Fungsi untuk membersihkan session saat logout
    public static void clear() {
        user = null;
    }
}