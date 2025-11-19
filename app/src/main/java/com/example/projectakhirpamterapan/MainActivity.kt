package com.example.projectakhirpamterapan

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.example.projectakhirpamterapan.ui.panitia.kelolaevent.PanitiaKelolaEventScreen
import com.example.projectakhirpamterapan.ui.panitia.kelolaevent.PanitiaKelolaEventViewModel
import com.example.projectakhirpamterapan.ui.panitia.kelolaevent.PanitiaKelolaEventViewModelFactory
import com.example.projectakhirpamterapan.ui.peserta.PesertaDashboardScreen
import com.example.projectakhirpamterapan.ui.peserta.PesertaDashboardViewModel
import com.example.projectakhirpamterapan.ui.peserta.PesertaDashboardViewModelFactory
import com.example.projectakhirpamterapan.ui.peserta.kelolaevent.PesertaKelolaEventScreen
import com.example.projectakhirpamterapan.ui.peserta.kelolaevent.PesertaKelolaEventViewModel
import com.example.projectakhirpamterapan.ui.peserta.kelolaevent.PesertaKelolaEventViewModelFactory
import com.example.projectakhirpamterapan.ui.role.RoleSelectionScreen
import com.example.projectakhirpamterapan.ui.theme.ProjectakhirpamterapanTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                val apiService = remember { ApiConfig.getApiService() }
                val eventRepository = remember { EventRepository(apiService) }

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    // ========================== LOGIN ==========================
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

                    // ======================== REGISTER =========================
                    composable("register") {
                        RegisterScreen(
                            vm = authVm,
                            goToLogin = { navController.popBackStack() }
                        )
                    }

                    // ===================== ROLE SELECTION =====================
                    composable("role") {
                        val user = authVm.loginState.user

                        RoleSelectionScreen(
                            userName = user?.name ?: "Pengguna",
                            onPesertaSelected = {
                                navController.navigate("pesertaDashboard")
                            },
                            onPanitiaSelected = {
                                navController.navigate("panitiaDashboard")
                            }
                        )
                    }

                    // ==================== DASHBOARD PESERTA ====================
                    composable("pesertaDashboard") {
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

                        val factory = PesertaDashboardViewModelFactory(
                            eventRepository = eventRepository,
                            authToken = token,
                            userId = user.id
                        )
                        val pesertaVm: PesertaDashboardViewModel =
                            viewModel(factory = factory)

                        PesertaDashboardScreen(
                            vm = pesertaVm,
                            userName = user.name,
                            onBackToRole = {
                                navController.popBackStack()
                                navController.navigate("role") {
                                    popUpTo("role") { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onOpenEventDetail = { eventId, eventTitle, eventDate, eventTime, eventLocation, eventStatus ->
                                navController.navigate(
                                    "pesertaEventDetail/$eventId/" +
                                            "${Uri.encode(eventTitle)}/" +
                                            "${Uri.encode(eventDate ?: "")}/" +
                                            "${Uri.encode(eventTime ?: "")}/" +
                                            "${Uri.encode(eventLocation)}/" +
                                            "${Uri.encode(eventStatus)}"
                                )
                            }
                        )
                    }

                    // ========== DETAIL / KELOLA EVENT PESERTA ==========
                    composable(
                        route = "pesertaEventDetail/{eventId}/{eventTitle}/{eventDate}/{eventTime}/{eventLocation}/{eventStatus}",
                        arguments = listOf(
                            navArgument("eventId") { type = NavType.IntType },
                            navArgument("eventTitle") { type = NavType.StringType },
                            navArgument("eventDate") { type = NavType.StringType },
                            navArgument("eventTime") { type = NavType.StringType },
                            navArgument("eventLocation") { type = NavType.StringType },
                            navArgument("eventStatus") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
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

                        val eventId = backStackEntry.arguments?.getInt("eventId") ?: run {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Event tidak ditemukan.")
                            }
                            return@composable
                        }

                        val eventTitle =
                            backStackEntry.arguments?.getString("eventTitle") ?: "Detail Event"
                        val eventDate =
                            backStackEntry.arguments?.getString("eventDate") ?: ""
                        val eventTime =
                            backStackEntry.arguments?.getString("eventTime") ?: ""
                        val eventLocation =
                            backStackEntry.arguments?.getString("eventLocation") ?: "-"
                        val eventStatus =
                            backStackEntry.arguments?.getString("eventStatus") ?: "Akan Datang"

                        val kelolaFactory = PesertaKelolaEventViewModelFactory(
                            eventRepository = eventRepository,
                            authToken = token,
                            eventId = eventId
                        )
                        val kelolaVm: PesertaKelolaEventViewModel =
                            viewModel(factory = kelolaFactory)

                        PesertaKelolaEventScreen(
                            vm = kelolaVm,
                            eventTitle = eventTitle,
                            eventLocation = eventLocation,
                            eventDate = eventDate,
                            eventTime = eventTime,
                            eventStatus = eventStatus,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // ==================== DASHBOARD PANITIA ====================
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
                        val panitiaVm: PanitiaDashboardViewModel =
                            viewModel(factory = factory)

                        PanitiaDashboardScreen(
                            vm = panitiaVm,
                            userName = user.name,
                            onBack = {
                                // contoh: kembali ke role selection
                                navController.popBackStack()
                                navController.navigate("role") {
                                    popUpTo("role") { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onCreateEvent = {
                                navController.navigate("createEvent")
                            },
                            onOpenEventDetail = { eventId, title, date, time, location, status ->
                                navController.navigate(
                                    "panitiaEventDetail/$eventId/" +
                                            "${Uri.encode(title)}/" +
                                            "${Uri.encode(date)}/" +
                                            "${Uri.encode(time)}/" +
                                            "${Uri.encode(location)}/" +
                                            "${Uri.encode(status)}"
                                )
                            }
                        )
                    }

                    // ========== DETAIL / KELOLA EVENT PANITIA ==========
                    composable(
                        route = "panitiaEventDetail/{eventId}/{eventTitle}/{eventDate}/{eventTime}/{eventLocation}/{eventStatus}",
                        arguments = listOf(
                            navArgument("eventId") { type = NavType.IntType },
                            navArgument("eventTitle") { type = NavType.StringType },
                            navArgument("eventDate") { type = NavType.StringType },
                            navArgument("eventTime") { type = NavType.StringType },
                            navArgument("eventLocation") { type = NavType.StringType },
                            navArgument("eventStatus") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
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

                        val eventId = backStackEntry.arguments?.getInt("eventId") ?: run {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Event tidak ditemukan.")
                            }
                            return@composable
                        }

                        val eventTitle =
                            backStackEntry.arguments?.getString("eventTitle") ?: "Kelola Event"
                        val eventDate =
                            backStackEntry.arguments?.getString("eventDate") ?: ""
                        val eventTime =
                            backStackEntry.arguments?.getString("eventTime") ?: ""
                        val eventLocation =
                            backStackEntry.arguments?.getString("eventLocation") ?: "-"
                        val eventStatus =
                            backStackEntry.arguments?.getString("eventStatus") ?: "Akan Datang"

                        val kelolaFactory = PanitiaKelolaEventViewModelFactory(
                            eventRepository = eventRepository,
                            authToken = token,
                            eventId = eventId
                        )
                        val kelolaVm: PanitiaKelolaEventViewModel =
                            viewModel(factory = kelolaFactory)

                        PanitiaKelolaEventScreen(
                            vm = kelolaVm,
                            eventTitle = eventTitle,
                            eventLocation = eventLocation,
                            eventDate = eventDate,
                            eventTime = eventTime,
                            eventStatus = eventStatus,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // ======================== CREATE EVENT =====================
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
                                navController.popBackStack()
                            },
                            onSuccess = {
                                navController.popBackStack()
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
