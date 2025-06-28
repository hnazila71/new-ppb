package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPScreen(
    onVerifyClick: (String) -> Unit,
    // Kita tidak perlu ViewModel di sini lagi
) {
    var otpValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Verifikasi Kode", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Masukkan 4 digit kode OTP.", color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = otpValue,
            onValueChange = { if (it.length <= 4) otpValue = it },
            label = { Text("Kode OTP") },
            textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Teks bantuan sudah dihapus dari sini

        Button(
            onClick = { onVerifyClick(otpValue) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = otpValue.length == 4
        ) {
            Text("Verifikasi")
        }
    }
}