package com.example.projectakhirpamterapan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import com.example.projectakhirpamterapan.data.EventRepository
import com.example.projectakhirpamterapan.data.remote.ApiConfig
import com.example.projectakhirpamterapan.ui.auth.AuthViewModel
import com.example.projectakhirpamterapan.ui.auth.LoginScreen
import com.example.projectakhirpamterapan.ui.auth.RegisterScreen
import com.example.projectakhirpamterapan.ui.panitia.CreateEventScreen
import com.example.projectakhirpamterapan.ui.panitia.PanitiaDashboardScreen
import com.example.projectakhirpamterapan.ui.panitia.PanitiaDashboardViewModel
import com.example.projectakhirpamterapan.ui.panitia.PanitiaDashboardViewModelFactory
import com.example.projectakhirpamterapan.ui.role.RoleSelectionScreen
import com.example.projectakhirpamterapan.ui.theme.ProjectakhirpamterapanTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // WARNA STATUS BAR & NAV BAR (dark)
        val systemColor = Color(0xFF020617).toArgb()
        window.statusBarColor = systemColor
        window.navigationBarColor = systemColor
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            ProjectakhirpamterapanTheme(darkTheme = true) {

                val navController = rememberNavController()
                val authVm: AuthViewModel = viewModel()

                // Siapkan ApiService & EventRepository sekali saja
                val apiService = remember { ApiConfig.getApiService() }
                val eventRepository = remember { EventRepository(apiService) }

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    /* ========================== LOGIN ========================== */
                    composable("login") {
                        LoginScreen(
                            vm = authVm,
                            onLoginSuccess = {
                                navController.navigate("role") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            goToRegister = { navController.navigate("register") }
                        )
                    }

                    /* ======================== REGISTER ========================= */
                    composable("register") {
                        RegisterScreen(
                            vm = authVm,
                            goToLogin = { navController.popBackStack() }
                        )
                    }

                    /* ===================== ROLE SELECTION ===================== */
                    composable("role") {
                        val user = authVm.loginState.user

                        RoleSelectionScreen(
                            userName = user?.name ?: "Pengguna",
                            onPesertaSelected = { navController.navigate("pesertaDashboard") },
                            onPanitiaSelected = { navController.navigate("panitiaDashboard") }
                        )
                    }

                    /* ==================== DASHBOARD PESERTA ==================== */
                    composable("pesertaDashboard") {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Dashboard Peserta (FITUR MENYUSUL)",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    /* ==================== DASHBOARD PANITIA ==================== */
                    composable("panitiaDashboard") {
                        val user = authVm.loginState.user
                        val token = authVm.loginState.token

                        if (user == null || token.isNullOrBlank()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Silakan login ulang.")
                            }
                            return@composable
                        }

                        val factory = PanitiaDashboardViewModelFactory(
                            eventRepository = eventRepository,
                            authToken = token,
                            createdByUserId = user.id
                        )

                        val vm: PanitiaDashboardViewModel =
                            viewModel(factory = factory)

                        PanitiaDashboardScreen(
                            vm = vm,
                            userName = user.name,
                            onCreateEvent = {
                                navController.navigate("createEvent")
                            }
                        )
                    }

                    /* ====================== CREATE EVENT ======================= */
                    composable("createEvent") {
                        val user = authVm.loginState.user
                        val token = authVm.loginState.token

                        if (user == null || token.isNullOrBlank()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Sesi berakhir. Silakan login ulang.")
                            }
                            return@composable
                        }

                        CreateEventScreen(
                            eventRepository = eventRepository,
                            authToken = token,
                            createdByUserId = user.id,
                            onBack = {
                                // back dari icon panah
                                navController.popBackStack()
                            },
                            onSuccess = {
                                // 1) Hapus layar createEvent dari backstack
                                navController.popBackStack()

                                // 2) Re-create dashboard supaya ViewModel baru di-init
                                navController.navigate("panitiaDashboard") {
                                    popUpTo("panitiaDashboard") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
