package com.example.projectakhirpamterapan.ui.panitia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projectakhirpamterapan.ui.panitia.components.EmptyStateCard
import com.example.projectakhirpamterapan.ui.panitia.components.ErrorBanner
import com.example.projectakhirpamterapan.ui.panitia.components.EventCard
import com.example.projectakhirpamterapan.ui.panitia.components.EventSkeletonCard
import com.example.projectakhirpamterapan.ui.panitia.components.FilterAndSearchRow
import com.example.projectakhirpamterapan.ui.panitia.components.HeroSection
import com.example.projectakhirpamterapan.ui.panitia.components.QrInviteDialog
import com.example.projectakhirpamterapan.ui.panitia.components.VerticalScrollbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanitiaDashboardScreen(
    vm: PanitiaDashboardViewModel,
    userName: String? = null,
    onBack: () -> Unit = {},
    onCreateEvent: () -> Unit = {},
    onOpenEventDetail: (
        eventId: Int,
        eventTitle: String,
        eventDate: String,
        eventTime: String,
        eventLocation: String,
        eventStatus: String
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    val uiState by vm.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val listState = rememberLazyListState()

    val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }

    // state dialog QR
    var selectedEventForQr by remember { mutableStateOf<EventUiModel?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                title = {
                    Text(
                        text = "Dashboard Panitia",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateEvent,
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Buat Event"
                )
            }
        },
        containerColor = colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ERROR BANNER
            if (!uiState.errorMessage.isNullOrBlank()) {
                ErrorBanner(
                    message = uiState.errorMessage ?: "",
                    onRetry = { vm.refresh() }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // HERO SECTION
            HeroSection(
                userName = userName,
                state = uiState
            )

            Spacer(modifier = Modifier.height(16.dp))

            // FILTER + SEARCH
            FilterAndSearchRow(
                selected = uiState.filter,
                onFilterSelected = vm::onFilterSelected,
                searchQuery = searchQuery,
                onSearchChange = setSearchQuery
            )

            Spacer(modifier = Modifier.height(12.dp))

            // HEADER LIST
            Text(
                text = "Daftar Event Kamu",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Scroll ke bawah untuk melihat semua event yang kamu pegang.",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // FILTERED LIST
            val filteredEvents = uiState.events.filter { ev ->
                val matchFilter = when (uiState.filter) {
                    EventFilter.ALL -> true
                    EventFilter.UPCOMING -> ev.status == "Akan Datang"
                    EventFilter.ONGOING -> ev.status == "Berlangsung"
                    EventFilter.DONE -> ev.status == "Selesai"
                }

                val matchQuery = if (searchQuery.isBlank()) {
                    true
                } else {
                    ev.title.contains(searchQuery, ignoreCase = true) ||
                            ev.location.contains(searchQuery, ignoreCase = true)
                }

                matchFilter && matchQuery
            }

            Spacer(modifier = Modifier.height(4.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            state = listState,
                            contentPadding = PaddingValues(
                                start = 0.dp,
                                top = 4.dp,
                                end = 0.dp,
                                bottom = 80.dp
                            )
                        ) {
                            items(4) {
                                EventSkeletonCard()
                            }
                        }

                        VerticalScrollbar(
                            listState = listState,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 4.dp, top = 8.dp, bottom = 8.dp)
                        )
                    }
                }

                filteredEvents.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 12.dp)
                    ) {
                        EmptyStateCard()
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            state = listState,
                            contentPadding = PaddingValues(
                                start = 0.dp,
                                top = 4.dp,
                                end = 0.dp,
                                bottom = 80.dp
                            )
                        ) {
                            items(filteredEvents, key = { it.id }) { event ->
                                EventCard(
                                    event = event,
                                    onShowQr = { selectedEventForQr = event },
                                    onManageEvent = {
                                        onOpenEventDetail(
                                            event.id,
                                            event.title,
                                            event.date,
                                            event.time,
                                            event.location,
                                            event.status
                                        )
                                    }
                                )
                            }
                        }

                        VerticalScrollbar(
                            listState = listState,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 4.dp, top = 8.dp, bottom = 8.dp)
                        )
                    }
                }
            }
        }

        // Dialog QR di luar Column biar overlay
        selectedEventForQr?.let { ev ->
            QrInviteDialog(
                event = ev,
                onDismiss = { selectedEventForQr = null }
            )
        }
    }
}
