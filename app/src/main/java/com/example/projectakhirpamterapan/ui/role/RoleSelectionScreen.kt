package com.example.projectakhirpamterapan.ui.role

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    userName: String?,
    onPesertaSelected: () -> Unit,
    onPanitiaSelected: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFFFFF))
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // HEADER + 2 CARD di tengah
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.projectakhirpamterapan.R.drawable.logo_eventaura),
                    contentDescription = "Login Illustration",
                    modifier = Modifier
                        .size(200.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Halo, ${userName ?: "Mahasiswa"} 👋",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1E40AF)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pilih cara kamu masuk ke sistem event kampus.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Row dua kartu, sama tinggi
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
        if (isPrimary) Color(0xFF1E40AF) else Color(0xFFFFFFFF)
    val onBgColor =
        if (isPrimary) Color(0xFFFFFFFF) else Color(0xFF1E40AF)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = if (!isPrimary) BorderStroke(2.dp, Color(0xFF1E40AF)) else null,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = onBgColor.copy(alpha = 0.08f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 30.sp
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
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
                        Column {
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
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 6.dp)
                        .size(28.dp),
                    tint = onBgColor
                )
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    RoleSelectionScreen(
        userName = "John Doe",
        onPesertaSelected = {},
        onPanitiaSelected = {}
    )
}