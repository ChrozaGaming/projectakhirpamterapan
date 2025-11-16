package com.example.projectakhirpamterapan.ui.role

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    userName: String?,
    onPesertaSelected: () -> Unit,
    onPanitiaSelected: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    // Gradient disesuaikan supaya enak di light & dark
    val topColor = if (isDark) {
        colorScheme.surfaceVariant.copy(alpha = 0.4f)
    } else {
        colorScheme.primary.copy(alpha = 0.08f)
    }
    val bottomColor = colorScheme.background

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Pilih Peran",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(topColor, bottomColor)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // HEADER + 2 CARD di tengah
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Halo, ${userName ?: "Mahasiswa"} 👋",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pilih cara kamu masuk ke sistem event kampus.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onBackground.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Row dua kartu, sama tinggi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoleCardEqualHeight(
                        emoji = "👥",
                        title = "Peserta",
                        subtitle = "Ikut dan pantau event.",
                        bullet1 = "Lihat event kampus",
                        bullet2 = "Cek waktu & lokasi",
                        bullet3 = "Daftar / batal cepat",
                        footer = "Masuk sebagai peserta",
                        isPrimary = true,
                        onClick = onPesertaSelected,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )

                    RoleCardEqualHeight(
                        emoji = "📅",
                        title = "Panitia",
                        subtitle = "Kelola jalannya event.",
                        bullet1 = "Buat & edit agenda",
                        bullet2 = "Atur rundown & jadwal",
                        bullet3 = "Pantau peserta & status",
                        footer = "Masuk sebagai panitia",
                        isPrimary = false,
                        onClick = onPanitiaSelected,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }

            Text(
                text = "Catatan: pilihan ini hanya mengatur tampilan fitur. " +
                        "Hak akses akun tetap mengikuti role di server.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                color = colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RoleCardEqualHeight(
    emoji: String,
    title: String,
    subtitle: String,
    bullet1: String,
    bullet2: String,
    bullet3: String,
    footer: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val bgColor =
        if (isPrimary) scheme.primaryContainer else scheme.secondaryContainer
    val onBgColor =
        if (isPrimary) scheme.onPrimaryContainer else scheme.onSecondaryContainer

    Card(
        modifier = modifier.heightIn(min = 180.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                color = onBgColor.copy(alpha = 0.08f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 18.sp
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = onBgColor
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = onBgColor.copy(alpha = 0.9f),
                            maxLines = 1
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• $bullet1",
                        style = MaterialTheme.typography.bodySmall,
                        color = onBgColor
                    )
                    Text(
                        text = "• $bullet2",
                        style = MaterialTheme.typography.bodySmall,
                        color = onBgColor
                    )
                    Text(
                        text = "• $bullet3",
                        style = MaterialTheme.typography.bodySmall,
                        color = onBgColor
                    )
                }
            }

            Text(
                text = footer,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = onBgColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}
