package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.TaskManagerScreen
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.OTPScreen
import com.example.myapplication.ui.auth.SignUpScreen
import com.example.myapplication.ui.splash.SplashScreen
import com.example.myapplication.ui.welcome.WelcomeScreen

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
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

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

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.TaskManager.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpClick = { navController.navigate(Screen.OTP.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }

        // --- PERUBAHAN 1: Navigasi setelah OTP ---
        composable(Screen.OTP.route) {
            OTPScreen(
                onVerifyClick = {
                    // Setelah verifikasi, arahkan ke halaman Login
                    navController.navigate(Screen.Login.route) {
                        // Hapus halaman SignUp dan OTP dari back stack
                        popUpTo(Screen.Welcome.route)
                    }
                }
            )
        }

        // --- PERUBAHAN 2: Menambahkan logika untuk Logout ---
        composable(Screen.TaskManager.route) {
            TaskManagerScreen(
                onLogoutClick = {
                    navController.navigate(Screen.Welcome.route) {
                        // Hapus semua halaman dari back stack agar tidak bisa kembali
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        // Pastikan hanya ada satu instance halaman Welcome
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}