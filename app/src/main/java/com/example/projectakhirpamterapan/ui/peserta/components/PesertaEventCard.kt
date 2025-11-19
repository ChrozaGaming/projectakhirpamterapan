package com.example.projectakhirpamterapan.ui.peserta.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectakhirpamterapan.ui.peserta.PesertaEventUiModel

@Composable
fun PesertaEventCard(
    event: PesertaEventUiModel,
    modifier: Modifier = Modifier,
    onManageClick: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    // 1. Warna Status yang lebih vivid
    val statusColor = when (event.status) {
        "Berlangsung" -> if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D)
        "Selesai" -> if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
        "Akan Datang" -> if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB)
        else -> colorScheme.primary
    }

    // 2. Background Card Logic (Kunci Perbaikan)
    // Di Dark Mode: Kita pakai Surface Container (abu gelap) sebagai dasar,
    // lalu ditumpuk gradient halus biar tidak flat.
    val cardContainerColor = if (isDark) colorScheme.surfaceContainerLow else colorScheme.surface

    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                colorScheme.surfaceContainerLow,
                colorScheme.surfaceContainerLow.copy(alpha = 0.8f)
            )
        )
    } else null

    // 3. Border yang lebih terlihat di Dark Mode
    val borderStroke = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)) else BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.4f))

    // 4. Shadow Effect
    val shadowElevation = if (isDark) 0.dp else 8.dp
    // Trik: Di dark mode, kita bisa kasih shadow berwarna tipis jika mau,
    // tapi surfaceContainerLow + Border biasanya sudah cukup classy.

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onManageClick?.invoke() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = shadowElevation
        ),
        border = BorderStroke(1.dp, Color(0xFF1E40AF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Terapkan gradient di atas warna solid container
                .then(
                    if (backgroundBrush != null) Modifier.background(backgroundBrush)
                    else Modifier
                )
                .padding(20.dp)
        ) {
            Column {
                // ===== HEADER =====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date Capsule
                    Surface(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .height(32.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF1E40AF),
                                        Color(0xFF0D1B49)
                                    )
                                ),
                                shape = RoundedCornerShape(50)
                            ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = if (isDark) Color.White.copy(alpha = 0.9f) else colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = event.dateFormatted ?: "-",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                color = if (isDark) Color.White.copy(alpha = 0.9f) else colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .shadow(4.dp, CircleShape, spotColor = statusColor) // Glow kecil
                                .background(statusColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.status,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = statusColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ===== TITLE =====
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        // Sedikit lebih renggang biar elegan
                        letterSpacing = (-0.5).sp
                    ),
                    color = if (isDark) Color.White else Color(0xFF1E40AF),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ===== INFO ROW =====
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoItem(
                        icon = Icons.Default.AccessTime,
                        text = "${event.timeFormatted ?: "-"} WIB",
                        isDark = isDark,
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Vertical Line Divider
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(1.dp)
                            .background(
                                if(isDark) Color.White.copy(alpha=0.2f)
                                else colorScheme.outlineVariant
                            )
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    InfoItem(
                        icon = Icons.Default.LocationOn,
                        text = event.location,
                        isDark = isDark,
                        colorScheme = colorScheme,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider Horizontal
                HorizontalDivider(
                    thickness = 1.dp,
                    color = if (isDark) Color.White.copy(alpha = 0.1f) else colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ===== FOOTER =====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Group & Count
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar Icon Background
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = if(isDark) Color(0xFF334155) else colorScheme.secondaryContainer,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = if(isDark) Color(0xFF94A3B8) else colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Total Peserta",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1E40AF)
                            )
                            Text(
                                text = "${event.participants}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1E40AF)
                            )
                        }
                    }

                    // Button Action
                    Surface(
                        onClick = { onManageClick?.invoke() },
                        shape = RoundedCornerShape(10.dp), // Squircle button
                        color = Color.White,
                        contentColor = Color(0xFF1E40AF),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Detail",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF1E40AF)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF1E40AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: ImageVector,
    text: String,
    isDark: Boolean,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF1E40AF),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1E40AF),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
