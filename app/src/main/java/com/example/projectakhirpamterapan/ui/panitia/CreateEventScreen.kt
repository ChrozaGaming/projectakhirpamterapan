package com.example.projectakhirpamterapan.ui.panitia

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    var dateValue by remember { mutableStateOf("") }      // yyyy-MM-dd (untuk API)
    var dateDisplay by remember { mutableStateOf("") }    // 16 November 2025
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
                // PAKAI HELPER KHUSUS FORM (nama beda supaya tidak bentrok)
                dateDisplay = formatIndonesianDateForm(backend)
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
                // PAKAI HELPER KHUSUS FORM
                timeDisplay = formatIndonesianTimeForm(backend)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        dialog.show()
    }

    val isFormValid =
        title.isNotBlank() &&
                dateValue.isNotBlank() &&
                timeValue.isNotBlank() &&
                location.isNotBlank()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Buat Event Baru",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        },
        containerColor = colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // HERO CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    colorScheme.primary.copy(alpha = 0.22f),
                                    colorScheme.surfaceVariant.copy(alpha = 0.9f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Rancang event panitia kamu.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lengkapi detail di bawah. QR undangan akan dibuat otomatis setelah event tersimpan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nama event
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nama Event") },
                placeholder = { Text("Misalnya: Briefing Panitia RAJA 2025") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tanggal (dibungkus Box clickable)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isSubmitting) { openDatePicker() }
            ) {
                OutlinedTextField(
                    value = dateDisplay,
                    onValueChange = { /* read-only */ },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Tanggal Event") },
                    placeholder = { Text("Pilih tanggal") },
                    singleLine = true,
                    enabled = false,
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pilih tanggal"
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Waktu (dibungkus Box clickable)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isSubmitting) { openTimePicker() }
            ) {
                OutlinedTextField(
                    value = timeDisplay,
                    onValueChange = { /* read-only */ },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Waktu Mulai") },
                    placeholder = { Text("Pilih waktu") },
                    singleLine = true,
                    enabled = false,
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Pilih waktu"
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lokasi
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Lokasi") },
                placeholder = { Text("Misalnya: Aula Gedung F - Lantai 7") },
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Status Awal",
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onBackground.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(
                    label = "Akan Datang",
                    selected = status == "Akan Datang",
                    onClick = { status = "Akan Datang" }
                )
                StatusChip(
                    label = "Berlangsung",
                    selected = status == "Berlangsung",
                    onClick = { status = "Berlangsung" }
                )
                StatusChip(
                    label = "Selesai",
                    selected = status == "Selesai",
                    onClick = { status = "Selesai" }
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

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

                        val result = eventRepository.createEvent(
                            authToken = authToken,
                            request = request
                        )

                        isSubmitting = false

                        result
                            .onSuccess {
                                onSuccess()
                            }
                            .onFailure { e ->
                                errorMessage = e.message ?: "Gagal membuat event"
                            }
                    }
                },
                enabled = isFormValid && !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(18.dp)
                            .width(18.dp),
                        strokeWidth = 2.dp,
                        color = colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyimpan…")
                } else {
                    Text("Simpan Event")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Setelah tersimpan, QR undangan otomatis dibuat di server dan siap dipakai untuk scan kehadiran.",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val background = if (selected) {
        colorScheme.primary.copy(alpha = 0.2f)
    } else {
        colorScheme.surfaceVariant
    }
    val textColor = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

/* === Util format tanggal & waktu khusus form ini === */

private fun formatIndonesianDateForm(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"

    val datePart = raw.split("T", " ")[0]
    val pieces = datePart.split("-")
    if (pieces.size < 3) return raw

    val year = pieces[0]
    val monthNum = pieces[1].toIntOrNull() ?: return raw
    val day = pieces[2].take(2)

    val monthName = when (monthNum) {
        1 -> "Januari"
        2 -> "Februari"
        3 -> "Maret"
        4 -> "April"
        5 -> "Mei"
        6 -> "Juni"
        7 -> "Juli"
        8 -> "Agustus"
        9 -> "September"
        10 -> "Oktober"
        11 -> "November"
        12 -> "Desember"
        else -> return raw
    }

    return "$day $monthName $year"
}

private fun formatIndonesianTimeForm(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"

    val timePart = raw.trim().takeWhile { it != ' ' }
    val pieces = timePart.split(":")
    if (pieces.size < 2) return raw

    val hour = pieces[0].padStart(2, '0')
    val minute = pieces[1].padStart(2, '0')

    return "$hour.$minute WIB"
}
