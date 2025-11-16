package com.example.projectakhirpamterapan.ui.panitia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import com.example.projectakhirpamterapan.ui.panitia.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanitiaDashboardScreen(
    vm: PanitiaDashboardViewModel,
    userName: String? = null,
    onCreateEvent: () -> Unit = {}
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
                title = {
                    Text(
                        text = "Dashboard Panitia",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
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
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
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
                                    onShowQr = { selectedEventForQr = event }
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
