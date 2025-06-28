package com.example.myapplication.data // Pastikan nama package ini sesuai dengan proyek Anda

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Kelas ini mengelola database SQLite lokal untuk menyimpan data pengguna.
 * Disesuaikan untuk kebutuhan aplikasi Anda yang memiliki proses Sign Up dan Login.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Auth.db" // Nama database yang lebih relevan
        private const val TABLE_USERS = "users"

        // Kolom yang akan ada di tabel users
        private const val KEY_ID = "id"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
    }

    // Fungsi ini dipanggil saat database dibuat pertama kali.
    override fun onCreate(db: SQLiteDatabase?) {
        // Perintah SQL untuk membuat tabel users
        val createTableQuery = ("CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_EMAIL + " TEXT UNIQUE," // Email harus unik
                + KEY_PASSWORD + " TEXT" + ")")
        db?.execSQL(createTableQuery)
    }

    // Fungsi ini dipanggil jika ada pembaruan versi database.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    /**
     * Fungsi untuk menambahkan pengguna baru ke database.
     * Akan dipanggil dari SignUpScreen.
     */
    fun addUser(email: String, password: String): Boolean {
        // Cek dulu apakah email sudah ada agar tidak duplikat
        if (isEmailExists(email)) {
            return false // Gagal menambahkan karena email sudah terdaftar
        }

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_EMAIL, email)
            put(KEY_PASSWORD, password) // Menyimpan password
        }

        val success = db.insert(TABLE_USERS, null, values)
        db.close()
        return success != -1L // Mengembalikan true jika berhasil, false jika gagal
    }

    /**
     * Fungsi untuk memeriksa apakah email sudah ada di database.
     * Akan dipanggil dari LoginScreen sebelum mengirim OTP.
     */
    fun isEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(KEY_ID), // Cukup cek keberadaan ID
            "$KEY_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0 // Mengembalikan true jika email ditemukan
    }
}