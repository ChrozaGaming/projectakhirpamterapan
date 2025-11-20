package com.example.projectakhirpamterapan.ui.panitia.kelolaevent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectakhirpamterapan.model.EventAnnouncement
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanitiaKelolaEventScreen(
    vm: PanitiaKelolaEventViewModel,
    eventTitle: String,
    eventLocation: String,
    eventDate: String,
    eventTime: String,
    eventStatus: String,
    onBack: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackMessage by remember { mutableStateOf<String?>(null) }
    var showAddAnnouncement by remember { mutableStateOf(false) }

    // ======== STATE PAGINATION & SEARCH PESERTA ========
    val pageSize = 10
    var participantSearchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(0) }

    val filteredParticipants = remember(uiState.participants, participantSearchQuery) {
        if (participantSearchQuery.isBlank()) {
            uiState.participants
        } else {
            uiState.participants.filter {
                it.name.contains(participantSearchQuery, ignoreCase = true)
            }
        }
    }

    val totalParticipantsFiltered = filteredParticipants.size
    val maxPageIndex =
        if (totalParticipantsFiltered == 0) 0 else (totalParticipantsFiltered - 1) / pageSize

    LaunchedEffect(totalParticipantsFiltered) {
        if (currentPage > maxPageIndex) {
            currentPage = 0
        }
    }

    val startIndex = currentPage * pageSize
    val endIndex = (startIndex + pageSize).coerceAtMost(totalParticipantsFiltered)
    val pageParticipants = if (totalParticipantsFiltered == 0 || startIndex >= totalParticipantsFiltered) {
        emptyList()
    } else {
        filteredParticipants.subList(startIndex, endIndex)
    }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackMessage = null
        }
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        text = "Kelola Event",
                        color = Color(0xFF1E40AF),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF1E40AF)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { vm.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh",
                            tint = Color(0xFF1E40AF)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1E40AF))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Info Event
                    item {
                        EventInfoCard(
                            title = eventTitle,
                            location = eventLocation,
                            date = eventDate,
                            time = eventTime,
                            status = eventStatus
                        )
                    }

                    // Attendance summary
                    uiState.attendanceSummary?.let { summary ->
                        item {
                            AttendanceSummaryCard(summary)
                        }
                    }

                    // Participants (dengan search + pagination)
                    item {
                        ParticipantsCard(
                            participants = pageParticipants,
                            searchQuery = participantSearchQuery,
                            onSearchChange = { query ->
                                participantSearchQuery = query
                                currentPage = 0
                            },
                            currentPage = if (totalParticipantsFiltered == 0) 0 else currentPage,
                            totalPages = if (totalParticipantsFiltered == 0) 1 else maxPageIndex + 1,
                            totalParticipants = totalParticipantsFiltered,
                            startIndex = startIndex,
                            onPrevPage = {
                                if (currentPage > 0) currentPage--
                            },
                            onNextPage = {
                                if (currentPage < maxPageIndex) currentPage++
                            }
                        )
                    }

                    // Error Banner
                    uiState.errorMessage?.let { errorMsg ->
                        item {
                            ErrorBanner(message = errorMsg)
                        }
                    }

                    // Announcement header + button
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(4.dp)
                                        .background(Color(0xFF1E40AF), CircleShape)
                                )
                                Text(
                                    text = "Papan Pengumuman",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = Color.Black
                                )
                            }

                            TextButton(onClick = { showAddAnnouncement = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = Color(0xFF1E40AF)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah", color = Color(0xFF1E40AF))
                            }
                        }
                    }

                    // Announcement list
                    if (uiState.announcements.isEmpty()) {
                        item { EmptyAnnouncementState() }
                    } else {
                        items(uiState.announcements, key = { it.id }) { ann ->
                            AnnouncementItem(announcement = ann)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
        }
    }

    if (showAddAnnouncement) {
        AddAnnouncementDialog(
            isSubmitting = uiState.isPostingAnnouncement,
            onDismiss = { showAddAnnouncement = false },
            onSubmit = { title, body ->
                vm.createAnnouncement(
                    title = title,
                    body = body
                ) { success, message ->
                    snackMessage = message
                    if (success) {
                        showAddAnnouncement = false
                    }
                }
            }
        )
    }
}

/* =========================
 * COMPONENTS
 * ========================= */

@Composable
private fun EventInfoCard(
    title: String,
    location: String,
    date: String,
    time: String,
    status: String
) {
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

    val formattedTime = remember(time) {
        try {
            val timePart = time.split(":").take(2).joinToString(".")
            "$timePart WIB"
        } catch (e: Exception) {
            "$time WIB"
        }
    }

    val (statusColor, statusBg) = when (status) {
        "Berlangsung" -> Color(0xFF16A34A) to Color(0xFFDCFCE7)
        "Selesai" -> Color(0xFF475569) to Color(0xFFF1F5F9)
        else -> Color(0xFF2563EB) to Color(0xFFDBEAFE)
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
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
                    color = Color(0xFF94A3B8)
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

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = Color(0xFFE2E8F0))

            Spacer(modifier = Modifier.height(20.dp))

            InfoRowItem(
                icon = Icons.Filled.LocationOn,
                title = "Lokasi",
                value = location
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRowItem(
                icon = Icons.Filled.CalendarToday,
                title = "Tanggal & Waktu",
                value = "$formattedDate\nPukul $formattedTime"
            )
        }
    }
}

@Composable
private fun InfoRowItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Color(0xFFE0F2FE),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF1E40AF),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = Color.Black
            )
        }
    }
}

@Composable
private fun AttendanceSummaryCard(
    summary: AttendanceSummaryUi
) {
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE0F2FE),
            Color.White
        )
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = CardDefaults.outlinedCardBorder(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.background(bgGradient)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Ringkasan Kehadiran",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Total peserta: ${summary.total}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AttendanceStatRow(
                    label = "Hadir",
                    count = summary.hadir,
                    percent = summary.percentHadir,
                    barColor = Color(0xFF16A34A)
                )
                Spacer(modifier = Modifier.height(12.dp))
                AttendanceStatRow(
                    label = "Alfa/Belum Absen",
                    count = summary.alfa,
                    percent = summary.percentAlfa,
                    barColor = Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
private fun AttendanceStatRow(
    label: String,
    count: Int,
    percent: Float,
    barColor: Color
) {
    val percentLabel = String.format(Locale("id", "ID"), "%.1f%%", percent)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Black
            )
            Text(
                text = "$count • $percentLabel",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = percent / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = barColor,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

@Composable
private fun ParticipantsCard(
    participants: List<ParticipantUi>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    currentPage: Int,
    totalPages: Int,
    totalParticipants: Int,
    startIndex: Int,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFE0F2FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Groups,
                            contentDescription = null,
                            tint = Color(0xFF1E40AF)
                        )
                    }

                    Text(
                        text = "Daftar Peserta",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Cari nama peserta...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (totalParticipants == 0) {
                Text(
                    text = "Belum ada peserta yang bergabung.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            } else if (participants.isEmpty()) {
                Text(
                    text = "Tidak ditemukan peserta dengan kata kunci tersebut.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            } else {
                participants.forEach { p ->
                    ParticipantRow(p)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Spacer(modifier = Modifier.height(8.dp))

                val firstIndex = startIndex + 1
                val lastIndex = startIndex + participants.size

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Menampilkan $firstIndex–$lastIndex dari $totalParticipants peserta",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPrevPage,
                            enabled = currentPage > 0,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (currentPage > 0)
                                    Color(0xFFE0F2FE)
                                else
                                    Color(0xFFF1F5F9),
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChevronLeft,
                                contentDescription = "Sebelumnya"
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Halaman ${currentPage + 1} / $totalPages",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = onNextPage,
                            enabled = currentPage < totalPages - 1,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (currentPage < totalPages - 1)
                                    Color(0xFFE0F2FE)
                                else
                                    Color(0xFFF1F5F9),
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "Berikutnya"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParticipantRow(
    participant: ParticipantUi
) {
    val initial = participant.name.firstOrNull()?.toString()?.uppercase() ?: "P"

    // === Format tanggal & waktu check-in ke format Indonesia ===
    val formattedCheckIn = remember(participant.checkedInAt) {
        participant.checkedInAt?.let { raw ->
            val localeId = Locale("id", "ID")

            fun tryParse(pattern: String) =
                try {
                    SimpleDateFormat(pattern, Locale.getDefault()).parse(raw)
                } catch (_: Exception) {
                    null
                }

            val date = tryParse("yyyy-MM-dd HH:mm:ss")
                ?: tryParse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                ?: tryParse("yyyy-MM-dd'T'HH:mm:ss'Z'")
                ?: tryParse("yyyy-MM-dd'T'HH:mm:ss")

            if (date != null) {
                SimpleDateFormat("EEEE, dd MMMM yyyy • HH.mm 'WIB'", localeId).format(date)
            } else {
                raw
            }
        }
    }

    val (badgeText, badgeColor, badgeBg) = when (participant.status) {
        "hadir" -> Triple(
            "Hadir",
            Color(0xFF16A34A),
            Color(0xFFDCFCE7)
        )

        "izin" -> Triple(
            "Izin",
            Color(0xFFF97316),
            Color(0xFFFFEDD5)
        )

        "alfa" -> Triple(
            "Alfa",
            Color(0xFFDC2626),
            Color(0xFFFEF2F2)
        )

        else -> Triple(
            "Belum Absen",
            Color(0xFF64748B),
            Color(0xFFF1F5F9)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFE0F2FE)
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1E40AF)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = participant.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = participant.email,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            formattedCheckIn?.let { formatted ->
                Text(
                    text = "Check-in: $formatted",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Surface(
            color = badgeBg,
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = badgeColor,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun AnnouncementItem(
    announcement: EventAnnouncement
) {
    // ==== FORMAT TANGGAL + JAM INDONESIA ====
    val formattedCreatedAt = remember(announcement.created_at) {
        val raw = announcement.created_at
        val localeId = Locale("id", "ID")

        fun tryParse(pattern: String): java.util.Date? =
            try {
                SimpleDateFormat(pattern, Locale.getDefault()).parse(raw)
            } catch (_: Exception) {
                null
            }

        val date = tryParse("yyyy-MM-dd HH:mm:ss")
            ?: tryParse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            ?: tryParse("yyyy-MM-dd'T'HH:mm:ss'Z'")
            ?: tryParse("yyyy-MM-dd'T'HH:mm:ss")

        if (date != null) {
            val output = SimpleDateFormat("EEEE, dd MMMM yyyy • HH.mm 'WIB'", localeId)
            output.format(date)
        } else {
            raw
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val initial = announcement.created_by_name.firstOrNull()?.toString() ?: "A"
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE0F2FE),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initial,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1E40AF)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = announcement.created_by_name,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Text(
                        text = formattedCreatedAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = Color(0xFF0F172A)
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
private fun EmptyAnnouncementState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFF1F5F9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Campaign,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Belum Ada Pengumuman",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF475569)
        )
        Text(
            text = "Pengumuman dari panitia akan tampil di sini.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AddAnnouncementDialog(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Pengumuman",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Isi Pengumuman") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(title.trim(), body.trim()) },
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyimpan...")
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("Batal")
            }
        }
    )
}
