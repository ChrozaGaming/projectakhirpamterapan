package com.example.projectakhirpamterapan.ui.peserta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectakhirpamterapan.data.EventRepository
import com.example.projectakhirpamterapan.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/* ==============================
   FORMATTER TANGGAL & WAKTU
   ============================== */

fun formatIndonesianDate(date: String?): String {
    return try {
        if (date.isNullOrBlank()) return "-"
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
        formatter.format(parser.parse(date)!!)
    } catch (e: Exception) {
        "-"
    }
}

fun formatIndonesianTime(time: String?): String {
    return try {
        if (time.isNullOrBlank()) return "-"
        val parser = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("HH.mm", Locale("id"))
        "${formatter.format(parser.parse(time)!!)} WIB"
    } catch (e: Exception) {
        "-"
    }
}

/* ==============================
   UI MODEL
   ============================== */

data class PesertaEventUiModel(
    val id: Int,
    val title: String,
    val dateFormatted: String,
    val timeFormatted: String,
    val location: String,
    val status: String,
    val participants: Int,
    val qrCode: String?
)

data class PesertaDashboardUiState(
    val isLoading: Boolean = false,
    val events: List<PesertaEventUiModel> = emptyList(),
    val errorMessage: String? = null
)

/* ==============================
   VIEWMODEL
   ============================== */

class PesertaDashboardViewModel(
    private val eventRepository: EventRepository,
    private val authToken: String,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(PesertaDashboardUiState(isLoading = true))
    val uiState: StateFlow<PesertaDashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = PesertaDashboardUiState(isLoading = true)

            val result = eventRepository.getPesertaEvents(authToken, userId)
            result
                .onSuccess { list ->
                    _uiState.value = PesertaDashboardUiState(
                        isLoading = false,
                        events = list.map { it.toUiModel() }
                    )
                }
                .onFailure { e ->
                    _uiState.value = PesertaDashboardUiState(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
        }
    }

    fun joinByQrCode(code: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = eventRepository.joinEventByQr(authToken, code)
            result
                .onSuccess { joined ->
                    val current = _uiState.value.events.toMutableList()
                    val idx = current.indexOfFirst { it.id == joined.id }

                    val uiModel = joined.toUiModel()
                    if (idx >= 0) current[idx] = uiModel else current.add(uiModel)

                    _uiState.value = _uiState.value.copy(events = current)
                    onResult(true, "Berhasil join event")
                }
                .onFailure { e ->
                    onResult(false, e.message ?: "Gagal join event")
                }
        }
    }

    private fun Event.toUiModel(): PesertaEventUiModel {
        return PesertaEventUiModel(
            id = id,
            title = title,
            dateFormatted = formatIndonesianDate(event_date),
            timeFormatted = formatIndonesianTime(event_time),
            location = location,
            status = status,
            participants = participants,
            qrCode = qr_code
        )
    }
}

/* ==============================
   FACTORY
   ============================== */

class PesertaDashboardViewModelFactory(
    private val eventRepository: EventRepository,
    private val authToken: String,
    private val userId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PesertaDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PesertaDashboardViewModel(eventRepository, authToken, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
