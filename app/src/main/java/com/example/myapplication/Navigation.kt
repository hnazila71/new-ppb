package com.example.myapplication

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.TaskManagerScreen
import com.example.myapplication.ui.auth.AuthViewModel
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.OTPScreen
import com.example.myapplication.ui.auth.SignUpScreen
import com.example.myapplication.ui.splash.SplashScreen
import com.example.myapplication.ui.welcome.WelcomeScreen
import com.example.myapplication.ui.auth.AuthState

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object OTP : Screen("otp")
    object TaskManager : Screen("task_manager")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        // ... composable untuk Splash dan Welcome tidak berubah ...
        composable(Screen.Splash.route) {
            SplashScreen(onSplashFinished = {
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) }
            )
        }

        val otpSentEffect: (AuthState) -> Unit = { authState ->
            if (authState is AuthState.OtpSent) {
                // Ambil kode OTP dari ViewModel
                val otp = authViewModel.correctOtp.value
                // Tampilkan sebagai Toast dengan durasi panjang
                Toast.makeText(context, "OTP (untuk testing): $otp", Toast.LENGTH_LONG).show()
                
                navController.navigate(Screen.OTP.route)
                authViewModel.resetState()
            } else if (authState is AuthState.Error) {
                Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
            }
        }

        composable(Screen.Login.route) {
            val authState by authViewModel.authState.collectAsState()
            LoginScreen(
                onLoginClick = { email, password -> authViewModel.login(email, password) },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) }
            )
            LaunchedEffect(authState) { otpSentEffect(authState) }
        }

        composable(Screen.SignUp.route) {
            val authState by authViewModel.authState.collectAsState()
            SignUpScreen(
                onSignUpClick = { email, password -> authViewModel.signUpAndGenerateOtp(email, password) },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
            LaunchedEffect(authState) { otpSentEffect(authState) }
        }

        composable(Screen.OTP.route) {
            val authState by authViewModel.authState.collectAsState()
            OTPScreen(onVerifyClick = { otp -> authViewModel.verifyOtp(otp) })

            LaunchedEffect(authState) {
                when (val state = authState) {
                    is AuthState.Authenticated -> {
                        navController.navigate(Screen.TaskManager.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                        authViewModel.resetState()
                    }
                    is AuthState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        authViewModel.resetState()
                    }
                    else -> Unit
                }
            }
        }

        composable(Screen.TaskManager.route) {
            TaskManagerScreen(
                onLogoutClick = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}