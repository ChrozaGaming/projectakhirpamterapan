package com.example.projectakhirpamterapan.ui.panitia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectakhirpamterapan.data.EventRepository
import com.example.projectakhirpamterapan.model.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ==================== FILTER ====================

enum class EventFilter {
    ALL, UPCOMING, ONGOING, DONE
}

// ==================== UI MODELS ====================

data class EventUiModel(
    val id: Int,
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val status: String,
    val participants: Int,
    val qrCode: String          // <–– ini dipakai untuk QR popup dialog
)

data class PanitiaDashboardUiState(
    val isLoading: Boolean = false,
    val events: List<EventUiModel> = emptyList(),
    val filter: EventFilter = EventFilter.ALL,
    val errorMessage: String? = null
)

// ==================== VIEWMODEL ====================

class PanitiaDashboardViewModel(
    private val eventRepository: EventRepository,
    private val authToken: String,
    private val createdByUserId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(PanitiaDashboardUiState())
    val uiState: StateFlow<PanitiaDashboardUiState> = _uiState.asStateFlow()

    init {
        // load pertama
        refresh()

        // polling tiap 3 detik
        viewModelScope.launch {
            while (true) {
                delay(3_000)
                refresh(silent = true)
            }
        }
    }

    fun refresh(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }

            val result = eventRepository.getPanitiaEvents(authToken, createdByUserId)
            result
                .onSuccess { events ->
                    val uiList = events.map { it.toUiModel() }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        events = uiList,
                        errorMessage = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Gagal memuat data"
                    )
                }
        }
    }

    fun onFilterSelected(filter: EventFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }

    private fun Event.toUiModel(): EventUiModel {
        return EventUiModel(
            id = id,
            title = title,
            date = event_date,
            time = event_time,
            location = location,
            status = status,
            participants = participants,
            qrCode = qrCode ?: ""      // kalau null dari server, jadikan string kosong
        )
    }
}

// ==================== FACTORY ====================

class PanitiaDashboardViewModelFactory(
    private val eventRepository: EventRepository,
    private val authToken: String,
    private val createdByUserId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PanitiaDashboardViewModel::class.java)) {
            return PanitiaDashboardViewModel(
                eventRepository = eventRepository,
                authToken = authToken,
                createdByUserId = createdByUserId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
