package com.example.myapplication.ui.auth // Pastikan nama package ini sesuai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DatabaseHelper
import com.example.myapplication.data.OtpSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk menangani semua logika otentikasi (SignUp, Login, OTP).
 * Menggunakan AndroidViewModel agar bisa mendapatkan context untuk DatabaseHelper.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Inisialisasi helper dan sender
    private val dbHelper = DatabaseHelper(application)
    private val otpSender = OtpSender()

    // State untuk menyimpan OTP yang benar untuk diverifikasi nanti
    private val _correctOtp = MutableStateFlow<String?>(null)
    val correctOtp: StateFlow<String?> = _correctOtp.asStateFlow()

    // State untuk mengelola UI, seperti menampilkan loading atau pesan error
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Fungsi untuk menangani proses registrasi (Sign Up).
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Panggil fungsi addUser dari DatabaseHelper
            val success = dbHelper.addUser(email, password)
            if (success) {
                _authState.value = AuthState.Success("Registrasi berhasil!")
            } else {
                // Ini terjadi jika email sudah terdaftar
                _authState.value = AuthState.Error("Email sudah terdaftar.")
            }
        }
    }

    /**
     * Fungsi untuk menangani proses login.
     * Ini akan memeriksa email, dan jika terdaftar, akan mengirim OTP.
     */
    fun loginAndSendOtp(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // 1. Cek apakah email terdaftar di database
            if (dbHelper.isEmailExists(email)) {
                // 2. Jika terdaftar, kirim OTP
                try {
                    val sentOtp = otpSender.sendOtp(email)
                    _correctOtp.value = sentOtp // Simpan OTP yang benar
                    // Pindah state ke OtpSent agar UI bisa navigasi ke OTPScreen
                    _authState.value = AuthState.OtpSent(email)
                } catch (e: Exception) {
                    _authState.value = AuthState.Error("Gagal mengirim OTP: ${e.message}")
                }
            } else {
                // 3. Jika tidak terdaftar, beri pesan error
                _authState.value = AuthState.Error("Email tidak terdaftar.")
            }
        }
    }

    /**
     * Fungsi untuk memverifikasi OTP yang dimasukkan pengguna.
     */
    fun verifyOtp(enteredOtp: String) {
        if (enteredOtp == _correctOtp.value) {
            // Jika OTP benar, set state ke Authenticated
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Error("Kode OTP salah.")
        }
    }

    /**
     * Fungsi untuk mereset state kembali ke idle,
     * bisa dipanggil saat pengguna meninggalkan screen atau setelah pesan error ditampilkan.
     */
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

// Sealed class untuk merepresentasikan berbagai state pada UI
sealed class AuthState {
    object Idle : AuthState() // State awal
    object Loading : AuthState() // Saat proses berjalan (menampilkan loading indicator)
    data class OtpSent(val email: String) : AuthState() // Sukses mengirim OTP, siap navigasi
    object Authenticated : AuthState() // Sukses verifikasi OTP, siap masuk ke home
    data class Success(val message: String) : AuthState() // Sukses umum (misal: registrasi)
    data class Error(val message: String) : AuthState() // Terjadi error
}