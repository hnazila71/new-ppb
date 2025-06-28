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
 * Kelas ini bertanggung jawab untuk membuat dan mengirim kode OTP ke email.
 */
class OtpSender {

    // !!! PENTING: GANTI DENGAN KREDENSIAL ANDA !!!
    // 1. Gunakan alamat email GMAIL Anda.
    // 2. Untuk password, gunakan "App Password" dari akun Google Anda, bukan password biasa.
    //    Cara mendapatkan App Password: Kunjungi https://myaccount.google.com/apppasswords
    private val senderEmail = "emailpengirimanda@gmail.com"
    private val senderPassword = "password_aplikasi_16_digit_anda"

    /**
     * Fungsi untuk mengirim OTP. Dibuat sebagai suspend function agar bisa dijalankan di background thread.
     * @param recipientEmail Email tujuan untuk pengiriman OTP.
     * @return String berisi kode OTP yang berhasil dikirim.
     * @throws Exception jika pengiriman gagal.
     */
    suspend fun sendOtp(recipientEmail: String): String {
        val otp = generateOtp()
        // Menjalankan proses pengiriman email di thread IO (background)
        withContext(Dispatchers.IO) {
            val props = Properties().apply {
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.socketFactory.port", "465")
                put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                put("mail.smtp.auth", "true")
                put("mail.smtp.port", "465")
            }

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, senderPassword)
                }
            })

            try {
                MimeMessage(session).let { mimeMessage ->
                    mimeMessage.setFrom(InternetAddress(senderEmail))
                    mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                    mimeMessage.subject = "Kode Verifikasi Anda"
                    mimeMessage.setText("Berikut adalah kode OTP Anda: $otp\n\nJangan berikan kode ini kepada siapa pun.")
                    Transport.send(mimeMessage)
                }
            } catch (e: Exception) {
                // Melemparkan exception agar bisa ditangkap di ViewModel
                throw Exception("Gagal mengirim email: ${e.message}")
            }
        }
        return otp
    }

    // Fungsi privat untuk menghasilkan kode OTP 6 digit secara acak
    private fun generateOtp(): String {
        return (100000..999999).random().toString()
    }
}