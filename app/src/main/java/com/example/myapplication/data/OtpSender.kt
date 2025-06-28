package com.example.myapplication.data // Pastikan nama package ini sesuai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import java.util.Random
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Kelas ini bertanggung jawab untuk membuat kode OTP.
 * Untuk sekarang, fungsi pengiriman email tidak akan kita gunakan.
 */
class OtpSender {

    // Fungsi ini sekarang hanya menghasilkan kode OTP 4 digit secara acak
    fun generateOtp(): String {
        return (1000..9999).random().toString()
    }

    // --- FUNGSI DI BAWAH INI TIDAK AKAN DIPANGGIL, TAPI KITA BIARKAN SAJA ---
    private val senderEmail = "emailpengirimanda@gmail.com"
    private val senderPassword = "password_aplikasi_16_digit_anda"

    suspend fun sendOtp(recipientEmail: String): String {
        val otp = generateOtp()
        withContext(Dispatchers.IO) {
            // Logika pengiriman email tidak akan dijalankan
        }
        return otp
    }
}