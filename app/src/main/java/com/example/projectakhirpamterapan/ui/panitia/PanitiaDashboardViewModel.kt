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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Filter status event
enum class EventFilter {
    ALL, UPCOMING, ONGOING, DONE
}

// Model untuk UI kartu event panitia
data class EventUiModel(
    val id: Int,
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val status: String,
    val participants: Int,
    val qrCode: String? = null
)

// State dashboard panitia
data class PanitiaDashboardUiState(
    val isLoading: Boolean = false,
    val events: List<EventUiModel> = emptyList(),
    val filter: EventFilter = EventFilter.ALL,
    val searchQuery: String = "",
    val errorMessage: String? = null
)

class PanitiaDashboardViewModel(
    private val eventRepository: EventRepository,
    private val authToken: String,
    private val createdByUserId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(PanitiaDashboardUiState(isLoading = true))
    val uiState: StateFlow<PanitiaDashboardUiState> = _uiState.asStateFlow()

    // Menyimpan semua event sebelum difilter / search
    private var allEvents: List<EventUiModel> = emptyList()

    init {
        refresh(showLoading = true)
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                delay(3_000) // refresh tiap 3 detik tanpa skeleton
                refresh(showLoading = false)
            }
        }
    }

    fun refresh(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            val result = eventRepository.getPanitiaEvents(authToken, createdByUserId)
            result
                .onSuccess { events ->
                    allEvents = events.map { it.toUiModel() }
                    applyFilterAndSearch()
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Gagal memuat data"
                        )
                    }
                }
        }
    }

    fun onFilterSelected(filter: EventFilter) {
        _uiState.update { it.copy(filter = filter) }
        applyFilterAndSearch()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilterAndSearch()
    }

    /**
     * Terapkan filter status + pencarian ke allEvents,
     * lalu update uiState.events
     */
    private fun applyFilterAndSearch() {
        val state = _uiState.value

        var filtered = when (state.filter) {
            EventFilter.ALL -> allEvents
            EventFilter.UPCOMING -> allEvents.filter { it.status == "Akan Datang" }
            EventFilter.ONGOING -> allEvents.filter { it.status == "Berlangsung" }
            EventFilter.DONE -> allEvents.filter { it.status == "Selesai" }
        }

        val q = state.searchQuery.trim().lowercase()
        if (q.isNotEmpty()) {
            filtered = filtered.filter { ev ->
                ev.title.lowercase().contains(q) ||
                        ev.location.lowercase().contains(q)
            }
        }

        _uiState.update { it.copy(events = filtered) }
    }

    /**
     * Mapping dari model backend (Event) → model UI
     * Di sini kita handle event_date dan event_time yg nullable, dan qr_code → qrCode.
     */
    private fun Event.toUiModel(): EventUiModel {
        return EventUiModel(
            id = id,
            title = title,
            date = event_date ?: "-",   // <- FIX: String? → String
            time = event_time ?: "-",   // <- FIX: String? → String
            location = location,
            status = status,
            participants = participants,
            qrCode = qr_code            // <- FIX: ambil dari field Event.qr_code
        )
    }
}

// Factory untuk inject dependency ke ViewModel
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
