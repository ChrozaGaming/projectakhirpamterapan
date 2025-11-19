package com.example.projectakhirpamterapan.ui.peserta.kelolaevent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectakhirpamterapan.model.EventAnnouncement
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PesertaKelolaEventScreen(
    vm: PesertaKelolaEventViewModel,
    eventTitle: String,
    eventLocation: String = "Lokasi Belum Diatur",
    eventDate: String = "2025-01-01",
    eventTime: String = "08:00:00",
    eventStatus: String = "Akan Datang",
    onBack: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackMessage = null
        }
    }

    Scaffold(
        containerColor = if (isDark) colorScheme.surface else colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Detail Event",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    FilledIconButton(
                        onClick = { vm.refresh() },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = colorScheme.primaryContainer,
                            contentColor = colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = if (isDark) colorScheme.surfaceVariant else colorScheme.surface,
                    scrolledContainerColor = if (isDark) colorScheme.surfaceVariant else colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDark)
                        Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.surface,
                                colorScheme.surfaceVariant
                            )
                        )
                    else
                        Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.background,
                                colorScheme.surface
                            )
                        )
                )
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = colorScheme.primary,
                        strokeCap = StrokeCap.Round
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 1. Attendance Card
                    item {
                        AttendanceCardPremium(
                            status = uiState.attendance?.status,
                            checkedInAt = uiState.attendance?.checked_in_at,
                            isCheckingIn = uiState.isCheckingIn,
                            onCheckInClick = {
                                vm.checkIn(status = null) { _, message ->
                                    snackMessage = message
                                }
                            }
                        )
                    }

                    // 2. Event Info Card
                    item {
                        EventInfoCard(
                            title = eventTitle,
                            location = eventLocation,
                            date = eventDate,
                            time = eventTime,
                            status = eventStatus
                        )
                    }

                    // Error Display
                    uiState.errorMessage?.let { errorMsg ->
                        item {
                            ErrorBanner(message = errorMsg)
                        }
                    }

                    // 3. Announcement Header
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 4.dp, height = 24.dp)
                                    .background(colorScheme.primary, CircleShape)
                            )
                            Text(
                                text = "Papan Pengumuman",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = null,
                                tint = colorScheme.primary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // 4. Announcement List
                    if (uiState.announcements.isEmpty()) {
                        item { EmptyAnnouncementStatePremium() }
                    } else {
                        items(uiState.announcements, key = { it.id }) { ann ->
                            AnnouncementItemPremium(announcement = ann)
                        }
                    }

                    // Bottom Spacer
                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
        }
    }
}

/* =====================================================
 *  PREMIUM UI COMPONENTS (FULL DARK-MODE FRIENDLY)
 * ===================================================== */

@Composable
private fun EventInfoCard(
    title: String,
    location: String,
    date: String,
    time: String,
    status: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    // Format Indonesian Date
    val formattedDate = remember(date) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateObj = inputFormat.parse(date)
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            outputFormat.format(dateObj ?: "")
        } catch (e: Exception) {
            date
        }
    }

    // Format Time
    val formattedTime = remember(time) {
        try {
            val timePart = time.split(":").take(2).joinToString(".")
            "$timePart WIB"
        } catch (e: Exception) {
            "$time WIB"
        }
    }

    // Adaptive Status Colors (Dark Mode Support)
    val (statusColor, statusBg) = when (status) {
        "Berlangsung" -> {
            if (isDark) Color(0xFF4ADE80) to Color(0xFF14532D)
            else Color(0xFF16A34A) to Color(0xFFDCFCE7)
        }
        "Selesai" -> {
            if (isDark) Color(0xFF94A3B8) to Color(0xFF1E293B)
            else Color(0xFF475569) to Color(0xFFF1F5F9)
        }
        else -> {
            if (isDark) Color(0xFF60A5FA) to Color(0xFF1E3A8A)
            else Color(0xFF2563EB) to Color(0xFFDBEAFE)
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark)
                colorScheme.surfaceVariant.copy(alpha = 0.9f)
            else
                colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header: Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INFORMASI EVENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(50),
                ) {
                    Text(
                        text = status.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Event Name
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                ),
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(20.dp))

            // Location
            InfoRowItem(
                icon = Icons.Filled.LocationOn,
                title = "Lokasi",
                value = location,
                colorScheme = colorScheme
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date & Time
            InfoRowItem(
                icon = Icons.Filled.CalendarToday,
                title = "Tanggal & Waktu",
                value = "$formattedDate\nPukul $formattedTime",
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
private fun InfoRowItem(
    icon: ImageVector,
    title: String,
    value: String,
    colorScheme: ColorScheme
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AttendanceCardPremium(
    status: String?,
    checkedInAt: String?,
    isCheckingIn: Boolean,
    onCheckInClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()
    val isPresent = status != null

    // Format check-in time to Indonesian date & time if available
    val formattedCheckIn = remember(checkedInAt) {
        checkedInAt?.let { formatDateTimeIndonesian(it) } ?: "-"
    }

    // Adaptive Gradient & Colors
    val bgGradient = if (isPresent) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF052E16),
                    colorScheme.surface
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFDCFCE7),
                    Color.White
                )
            )
        }
    } else {
        Brush.verticalGradient(
            colors = listOf(
                colorScheme.primaryContainer.copy(alpha = 0.2f),
                colorScheme.surface
            )
        )
    }

    val borderColor = if (isPresent) {
        if (isDark) Color(0xFF22C55E).copy(alpha = 0.5f) else Color(0xFF22C55E).copy(alpha = 0.3f)
    } else {
        colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    val accentColor = if (isPresent) {
        if (isDark) Color(0xFF4ADE80) else Color(0xFF16A34A)
    } else {
        colorScheme.primary
    }

    val labelColor = if (isPresent) {
        if (isDark) Color(0xFF86EFAC) else Color(0xFF15803D)
    } else {
        colorScheme.primary
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.background(bgGradient)) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "ABSENSI KEHADIRAN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = labelColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            Text(
                                text = if (isPresent) "Sudah Hadir" else "Belum Absen",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = accentColor
                            )
                        }
                    }

                    // Status Badge Icon
                    Surface(
                        shape = CircleShape,
                        color = if (isPresent) accentColor else colorScheme.surfaceVariant,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPresent) Icons.Filled.Check else Icons.Filled.QrCodeScanner,
                                contentDescription = null,
                                tint = if (isPresent) {
                                    if (isDark) Color.Black else Color.White
                                } else {
                                    colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isPresent) {
                    // Detail waktu check-in (format Indonesia)
                    AttendanceDetailRow(
                        icon = Icons.Filled.AccessTime,
                        label = "Waktu Check-in",
                        value = formattedCheckIn,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = onCheckInClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, colorScheme.outlineVariant)
                    ) {
                        if (isCheckingIn) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Update Data", color = colorScheme.onSurface)
                        }
                    }
                } else {
                    Text(
                        text = "Silahkan melakukan absensi yang disediakan panitia untuk mencatat kehadiranmu.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onCheckInClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        if (isCheckingIn) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = colorScheme.onPrimary,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Memproses...")
                        } else {
                            Icon(
                                Icons.Filled.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Check-in Sekarang",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF064E3B).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.85f)
    val borderColor = if (isDark) Color(0xFF059669).copy(alpha = 0.4f) else Color(0xFF22C55E).copy(alpha = 0.25f)
    val iconTint = if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D)
    val labelColor = if (isDark) Color(0xFFBBF7D0) else Color(0xFF15803D).copy(alpha = 0.85f)
    val valueColor = if (isDark) Color(0xFFD1FAE5) else Color(0xFF14532D)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = labelColor
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = valueColor
            )
        }
    }
}

@Composable
private fun AnnouncementItemPremium(
    announcement: EventAnnouncement
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    // Format tanggal & waktu Indonesia
    val formattedCreatedAt = remember(announcement.created_at) {
        formatDateTimeIndonesian(announcement.created_at)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark)
                colorScheme.surfaceVariant
            else
                colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header: Avatar & Meta
            Row(verticalAlignment = Alignment.CenterVertically) {
                val initial = announcement.created_by_name.firstOrNull()?.toString() ?: "A"
                Surface(
                    shape = CircleShape,
                    color = colorScheme.tertiaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initial,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colorScheme.onTertiaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = announcement.created_by_name,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = formattedCreatedAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body Content
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = colorScheme.onSurface.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun EmptyAnnouncementStatePremium() {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    if (isDark) colorScheme.surfaceVariant else colorScheme.surfaceContainerHigh,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Campaign,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Belum Ada Pengumuman",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onSurfaceVariant
        )
        Text(
            text = "Informasi penting dari panitia akan muncul di sini.",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Helper: format string tanggal dari backend ke format Indonesia
 * Contoh output: "Rabu, 19 November 2025 • 13.45 WIB"
 */
private fun formatDateTimeIndonesian(raw: String): String {
    val localeId = Locale("id", "ID")
    val patterns = listOf(
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ssX",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    val date = patterns
        .asSequence()
        .mapNotNull { pattern ->
            try {
                SimpleDateFormat(pattern, Locale.getDefault()).parse(raw)
            } catch (_: Exception) {
                null
            }
        }
        .firstOrNull() ?: return raw

    return SimpleDateFormat("EEEE, dd MMMM yyyy • HH.mm 'WIB'", localeId).format(date)
}
