package com.example.projectakhirpamterapan.ui.panitia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import com.example.projectakhirpamterapan.ui.panitia.EventFilter
import com.example.projectakhirpamterapan.ui.panitia.PanitiaDashboardUiState
/* ===== HERO SECTION ===== */

@Composable
fun HeroSection(
    userName: String?,
    state: PanitiaDashboardUiState
) {
    val colorScheme = MaterialTheme.colorScheme
    val upcoming = state.events.count { it.status == "Akan Datang" }
    val ongoing = state.events.count { it.status == "Berlangsung" }
    val done = state.events.count { it.status == "Selesai" }
    val totalParticipants = state.events.sumOf { it.participants }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            colorScheme.primary.copy(alpha = 0.18f),
                            colorScheme.surfaceVariant.copy(alpha = 0.9f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.5f)
                ) {
                    Text(
                        text = "Halo, ${userName ?: "Panitia"} 👋",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pantau event, peserta, dan status kehadiran secara real-time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurface.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MiniStat(
                            label = "Upcoming",
                            value = upcoming.toString()
                        )
                        MiniStat(
                            label = "Berlangsung",
                            value = ongoing.toString()
                        )
                        MiniStat(
                            label = "Selesai",
                            value = done.toString()
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Total peserta terdaftar: $totalParticipants",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            colorScheme.surface.copy(alpha = 0.4f)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📅",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$upcoming upcoming",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$ongoing berlangsung",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$done selesai",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniStat(
    label: String,
    value: String
) {
    val colorScheme = MaterialTheme.colorScheme
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

/* ===== FILTER + SEARCH ===== */

@Composable
fun FilterAndSearchRow(
    selected: EventFilter,
    onFilterSelected: (EventFilter) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                label = "Semua",
                selected = selected == EventFilter.ALL,
                onClick = { onFilterSelected(EventFilter.ALL) }
            )
            FilterChip(
                label = "Akan datang",
                selected = selected == EventFilter.UPCOMING,
                onClick = { onFilterSelected(EventFilter.UPCOMING) }
            )
            FilterChip(
                label = "Berlangsung",
                selected = selected == EventFilter.ONGOING,
                onClick = { onFilterSelected(EventFilter.ONGOING) }
            )
            FilterChip(
                label = "Selesai",
                selected = selected == EventFilter.DONE,
                onClick = { onFilterSelected(EventFilter.DONE) }
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            placeholder = { Text("Cari event / lokasi…") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            )
        )
    }
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected)
                colorScheme.primary.copy(alpha = 0.18f)
            else
                colorScheme.surfaceVariant,
            labelColor = if (selected)
                colorScheme.primary
            else
                colorScheme.onSurfaceVariant
        )
    )
}
