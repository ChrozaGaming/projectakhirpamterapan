package com.example.projectakhirpamterapan.ui.panitia

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectakhirpamterapan.data.EventRepository
import com.example.projectakhirpamterapan.model.CreateEventRequest
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    eventRepository: EventRepository,
    authToken: String,
    createdByUserId: Int,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var dateValue by remember { mutableStateOf("") }      // yyyy-MM-dd
    var dateDisplay by remember { mutableStateOf("") }    // 16 Nov 2025
    var timeValue by remember { mutableStateOf("") }      // HH:mm
    var timeDisplay by remember { mutableStateOf("") }    // 14.30 WIB
    var location by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Akan Datang") }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val monthIndex = month + 1
                val backend = "%04d-%02d-%02d".format(year, monthIndex, dayOfMonth)
                dateValue = backend
                dateDisplay = formatIndonesianDateForm(backend)

                val selectedCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val todayCal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                status = when {
                    selectedCal.before(todayCal) -> "Selesai"
                    selectedCal.after(todayCal) -> "Akan Datang"
                    else -> "Berlangsung"
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    fun openTimePicker() {
        val calendar = Calendar.getInstance()
        val dialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val backend = "%02d:%02d".format(hourOfDay, minute)
                timeValue = backend
                timeDisplay = formatIndonesianTimeForm(backend)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        dialog.show()
    }

    val isFormValid = title.isNotBlank() && dateValue.isNotBlank() &&
            timeValue.isNotBlank() && location.isNotBlank()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Buat Event",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White,
                    titleContentColor = Color(0xFF1E40AF),
                    navigationIconContentColor = Color(0xFF1E40AF)
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Text(
                        text = "Detail Agenda",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Isi formulir lengkap untuk membuat QR kehadiran.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1E40AF)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                val borderStroke = BorderStroke(1.dp, Color(0xFF1E40AF))

                Card(
                    shape = RoundedCornerShape(24.dp), // Lebih rounded
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = borderStroke
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp), // Padding dalam card lebih lega
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 1. Nama Event
                        CustomOutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = "Nama Event",
                            placeholder = "Ex: Rapat Koordinasi",
                            icon = Icons.Default.Edit,
                        )

                        // 2. Row Tanggal & Waktu
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.weight(1f)) {
                                CustomOutlinedTextField(
                                    value = dateDisplay,
                                    onValueChange = {},
                                    label = "Tanggal",
                                    placeholder = "Pilih",
                                    icon = Icons.Default.CalendarMonth,
                                    readOnly = true,
                                    onClick = { if (!isSubmitting) openDatePicker() }
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(modifier = Modifier.weight(0.8f)) {
                                CustomOutlinedTextField(
                                    value = timeDisplay,
                                    onValueChange = {},
                                    label = "Jam",
                                    placeholder = "Pilih",
                                    icon = Icons.Default.Schedule,
                                    readOnly = true,
                                    onClick = { if (!isSubmitting) openTimePicker() }
                                )
                            }
                        }

                        // 3. Lokasi
                        CustomOutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = "Lokasi",
                            placeholder = "Ex: Aula Lantai 2",
                            icon = Icons.Default.LocationOn,
                            singleLine = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- STATUS SECTION (READ ONLY) ---
                Text(
                    text = "Status Awal (Otomatis)",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1E40AF)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusChipReadOnly(label = "Akan Datang", isActive = status == "Akan Datang", color = Color(0xFF3B82F6))
                    StatusChipReadOnly(label = "Berlangsung", isActive = status == "Berlangsung", color = Color(0xFF22C55E))
                    StatusChipReadOnly(label = "Selesai", isActive = status == "Selesai", color = Color(0xFF64748B))
                }

                // Error Message
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Surface(
                        color = colorScheme.errorContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(120.dp))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    colorScheme.background.copy(alpha = 0.8f),
                                    colorScheme.background
                                ),
                                startY = 0f
                            )
                        )
                )

                // Container Tombol
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .navigationBarsPadding() // Agar tidak ketutup navigasi HP gesture
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                isSubmitting = true
                                errorMessage = null
                                val request = CreateEventRequest(
                                    created_by = createdByUserId,
                                    title = title.trim(),
                                    event_date = dateValue,
                                    event_time = timeValue,
                                    location = location.trim(),
                                    status = status
                                )
                                val result = eventRepository.createEvent(authToken, request)
                                isSubmitting = false
                                result.onSuccess { onSuccess() }
                                    .onFailure { errorMessage = it.message ?: "Gagal membuat event" }
                            }
                        },
                        enabled = isFormValid && !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = if (isFormValid && !isSubmitting) 8.dp else 0.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = colorScheme.primary.copy(alpha = 0.5f)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E40AF),
                            disabledContainerColor = colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp,
                                color = colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Menyimpan...")
                        } else {
                            Text(
                                "Simpan & Buat QR",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                        }
                    }
                }
            }
        }
    }
}

// --- CUSTOM COMPONENTS ---

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    singleLine: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (onClick != null) {
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect {
                if (it is PressInteraction.Release) {
                    onClick()
                }
            }
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF1E40AF))
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1E40AF),
            unfocusedBorderColor = Color(0xFF1E40AF),
            focusedLabelColor = Color(0xFF1E40AF),
            unfocusedLabelColor = Color(0xFF1E40AF),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color(0xFF1E40AF),
            unfocusedTextColor = Color(0xFF1E40AF)
        ),
        singleLine = singleLine,
        readOnly = readOnly,
        interactionSource = interactionSource
    )
}

@Composable
fun StatusChipReadOnly(
    label: String,
    isActive: Boolean,
    color: Color
) {
    val isDark = isSystemInDarkTheme()

    // Warna Container (Lebih soft)
    val containerColor = if (isActive) {
        color.copy(alpha = if (isDark) 0.15f else 0.1f)
    } else {
        Color(0xFFD3D3D3)  // Light gray background for inactive
    }

    // Warna Text
    val contentColor = if (isActive) {
        if (isDark) color.copy(alpha = 0.9f) else color
    } else {
        Color.White  // White text for inactive
    }

    // Border Logic
    val border = if (isActive) BorderStroke(1.dp, contentColor.copy(alpha = 0.2f)) else null

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .then(if (border != null) Modifier.border(border, RoundedCornerShape(50)) else Modifier)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.5.sp
            ),
            color = contentColor
        )
    }
}

/* === Fungsi format tanggal & waktu === */
private fun formatIndonesianDateForm(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val datePart = raw.split("T", " ")[0]
    val pieces = datePart.split("-")
    if (pieces.size < 3) return raw
    val year = pieces[0]
    val monthNum = pieces[1].toIntOrNull() ?: return raw
    val day = pieces[2].take(2)
    val monthName = when (monthNum) {
        1 -> "Jan" 2 -> "Feb" 3 -> "Mar" 4 -> "Apr" 5 -> "Mei" 6 -> "Jun"
        7 -> "Jul" 8 -> "Agt" 9 -> "Sep" 10 -> "Okt" 11 -> "Nov" 12 -> "Des"
        else -> return raw
    }
    return "$day $monthName $year"
}

private fun formatIndonesianTimeForm(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val timePart = raw.trim().takeWhile { it != ' ' }
    val pieces = timePart.split(":")
    if (pieces.size < 2) return raw
    return "${pieces[0]}:${pieces[1]} WIB"
}