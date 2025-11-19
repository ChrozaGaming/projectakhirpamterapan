package com.example.projectakhirpamterapan.ui.peserta.kelolaevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectakhirpamterapan.data.EventRepository
import com.example.projectakhirpamterapan.model.EventAnnouncement
import com.example.projectakhirpamterapan.model.EventAttendance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KelolaEventUiState(
    val isLoading: Boolean = true,
    val attendance: EventAttendance? = null,
    val announcements: List<EventAnnouncement> = emptyList(),
    val errorMessage: String? = null,
    val isCheckingIn: Boolean = false
)

class PesertaKelolaEventViewModel(
    private val eventRepository: EventRepository,
    private val authToken: String,
    private val eventId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(KelolaEventUiState())
    val uiState: StateFlow<KelolaEventUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            var attendance: EventAttendance? = null
            var announcements: List<EventAnnouncement> = emptyList()
            var error: String? = null

            val attendanceResult = eventRepository.getMyAttendance(authToken, eventId)
            attendanceResult
                .onSuccess { attendance = it }
                .onFailure { e ->
                    error = e.message ?: "Gagal memuat data absensi"
                }

            val announcementsResult = eventRepository.getEventAnnouncements(authToken, eventId)
            announcementsResult
                .onSuccess { announcements = it }
                .onFailure { e ->
                    error = error ?: (e.message ?: "Gagal memuat announcement")
                }

            _uiState.value = KelolaEventUiState(
                isLoading = false,
                attendance = attendance,
                announcements = announcements,
                errorMessage = error
            )
        }
    }

    fun checkIn(
        status: String? = null,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCheckingIn = true,
                errorMessage = null
            )

            val result = eventRepository.checkInAttendance(authToken, eventId, status)
            result
                .onSuccess { attendance ->
                    _uiState.value = _uiState.value.copy(
                        attendance = attendance,
                        isCheckingIn = false
                    )
                    onResult(true, "Absensi berhasil dicatat")
                }
                .onFailure { e ->
                    val msg = e.message ?: "Gagal mencatat absensi"
                    _uiState.value = _uiState.value.copy(
                        isCheckingIn = false,
                        errorMessage = msg
                    )
                    onResult(false, msg)
                }
        }
    }
}

class PesertaKelolaEventViewModelFactory(
    private val eventRepository: EventRepository,
    private val authToken: String,
    private val eventId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PesertaKelolaEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PesertaKelolaEventViewModel(eventRepository, authToken, eventId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
