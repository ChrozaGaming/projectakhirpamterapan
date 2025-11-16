package com.example.projectakhirpamterapan.ui.panitia

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectakhirpamterapan.data.EventRepository

object PanitiaRoutes {
    const val DASHBOARD = "panitia_dashboard"
    const val CREATE_EVENT = "create_event"
}

/**
 * NavGraph khusus alur Panitia:
 * - Dashboard Panitia
 * - Create Event
 *
 * ViewModel tidak diambil lewat Hilt, tapi lewat factory manual
 * dengan EventRepository + token + createdByUserId.
 */

@Composable
fun PanitiaNavGraph(
    navController: NavHostController = rememberNavController(),
    eventRepository: EventRepository,
    authToken: String,
    createdByUserId: Int,
    userName: String?
) {
    NavHost(
        navController = navController,
        startDestination = PanitiaRoutes.DASHBOARD
    ) {

        // =================== DASHBOARD PANITIA =================== //
        composable(PanitiaRoutes.DASHBOARD) {

            val factory = PanitiaDashboardViewModelFactory(
                eventRepository = eventRepository,
                authToken = authToken,
                createdByUserId = createdByUserId
            )

            val vm: PanitiaDashboardViewModel = viewModel(factory = factory)

            PanitiaDashboardScreen(
                vm = vm,
                userName = userName,
                onCreateEvent = {
                    // FAB di dashboard akan navigate ke layar create event
                    navController.navigate(PanitiaRoutes.CREATE_EVENT)
                }
            )
        }

        // ======================= CREATE EVENT ====================== //
        composable(PanitiaRoutes.CREATE_EVENT) {
            CreateEventScreen(
                eventRepository = eventRepository,
                authToken = authToken,
                createdByUserId = createdByUserId,
                onBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    // Setelah sukses buat event:
                    // balik ke dashboard (pop layar create event)
                    navController.popBackStack()
                }
            )
        }
    }
}
