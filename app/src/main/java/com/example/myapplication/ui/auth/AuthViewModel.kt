package com.example.myapplication.ui.auth

import android.app.Application
// import android.util.Log // Hapus atau biarkan tidak apa-apa
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DatabaseHelper
import com.example.myapplication.data.OtpSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)
    private val otpSender = OtpSender()

    private val _correctOtp = MutableStateFlow<String?>(null)
    val correctOtp: StateFlow<String?> = _correctOtp.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signUpAndGenerateOtp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            if (dbHelper.isEmailExists(email)) {
                _authState.value = AuthState.Error("Email sudah terdaftar.")
            } else {
                dbHelper.addUser(email, password)
                val generatedOtp = otpSender.generateOtp()
                _correctOtp.value = generatedOtp
                // Hapus baris Log.d dari sini
                _authState.value = AuthState.OtpSent(email)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            if (dbHelper.checkUser(email, password)) {
                val generatedOtp = otpSender.generateOtp()
                _correctOtp.value = generatedOtp
                // Hapus baris Log.d dari sini
                _authState.value = AuthState.OtpSent(email)
            } else {
                _authState.value = AuthState.Error("Email atau password salah.")
            }
        }
    }

    fun verifyOtp(enteredOtp: String) {
        if (enteredOtp == _correctOtp.value) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Error("Kode OTP salah.")
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class OtpSent(val email: String) : AuthState()
    object Authenticated : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}