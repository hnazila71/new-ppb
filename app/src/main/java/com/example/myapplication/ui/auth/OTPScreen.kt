package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPScreen(onVerifyClick: () -> Unit) {
    var otpValue by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center) {
        Text("Verifikasi Kode", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Masukkan 4 digit kode yang dikirim ke email Anda.", color = Color.Gray)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = otpValue,
            onValueChange = { if (it.length <= 4) otpValue = it },
            label = { Text("Kode OTP") },
            textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onVerifyClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = otpValue.length == 4
        ) {
            Text("Verifikasi")
        }
    }
}