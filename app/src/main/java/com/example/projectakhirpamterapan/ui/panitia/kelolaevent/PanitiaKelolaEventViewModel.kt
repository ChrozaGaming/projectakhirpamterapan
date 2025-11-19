package com.example.projectakhirpamterapan.ui.panitia.kelolaevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectakhirpamterapan.data.EventRepository
import com.example.projectakhirpamterapan.model.EventAnnouncement
import com.example.projectakhirpamterapan.model.EventParticipant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AttendanceSummaryUi(
    val total: Int,
    val hadir: Int,
    val izin: Int,
    val alfa: Int
) {
    val percentHadir: Float get() = if (total == 0) 0f else hadir * 100f / total
    val percentIzin: Float get() = if (total == 0) 0f else izin * 100f / total
    val percentAlfa: Float get() = if (total == 0) 0f else alfa * 100f / total
}

data class ParticipantUi(
    val userId: Int,
    val name: String,
    val email: String,
    val status: String?,
    val checkedInAt: String?
)

data class PanitiaKelolaEventUiState(
    val isLoading: Boolean = true,
    val attendanceSummary: AttendanceSummaryUi? = null,
    val participants: List<ParticipantUi> = emptyList(),
    val announcements: List<EventAnnouncement> = emptyList(),
    val errorMessage: String? = null,
    val isPostingAnnouncement: Boolean = false
)

class PanitiaKelolaEventViewModel(
    private val eventRepository: EventRepository,
    private val authToken: String,
    private val eventId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(PanitiaKelolaEventUiState())
    val uiState: StateFlow<PanitiaKelolaEventUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            var participants: List<EventParticipant> = emptyList()
            var announcements: List<EventAnnouncement> = emptyList()
            var errorMessage: String? = null

            // Peserta + status absensi
            val participantsResult =
                eventRepository.getEventParticipantsForPanitia(authToken, eventId)
            participantsResult
                .onSuccess { list -> participants = list }
                .onFailure { e ->
                    errorMessage = e.message ?: "Gagal memuat peserta event"
                }

            // Announcement
            val announcementsResult =
                eventRepository.getEventAnnouncements(authToken, eventId)
            announcementsResult
                .onSuccess { list -> announcements = list }
                .onFailure { e ->
                    if (errorMessage == null) {
                        errorMessage = e.message ?: "Gagal memuat pengumuman"
                    }
                }

            val summaryUi = buildSummaryFromParticipants(participants)

            _uiState.value = PanitiaKelolaEventUiState(
                isLoading = false,
                attendanceSummary = summaryUi,
                participants = participants.map { it.toUi() },
                announcements = announcements,
                errorMessage = errorMessage
            )
        }
    }

    private fun buildSummaryFromParticipants(list: List<EventParticipant>): AttendanceSummaryUi {
        val total = list.size
        val hadir = list.count { it.status == "hadir" }
        val izin = list.count { it.status == "izin" }
        val alfa = list.count { it.status == "alfa" || it.status == null }
        return AttendanceSummaryUi(
            total = total,
            hadir = hadir,
            izin = izin,
            alfa = alfa
        )
    }

    private fun EventParticipant.toUi(): ParticipantUi {
        return ParticipantUi(
            userId = user_id,
            name = name,
            email = email,
            status = status,
            checkedInAt = checked_in_at
        )
    }

    fun createAnnouncement(
        title: String,
        body: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (title.isBlank() || body.isBlank()) {
                onResult(false, "Judul dan isi pengumuman tidak boleh kosong.")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isPostingAnnouncement = true)

            val result = eventRepository.createEventAnnouncement(
                authToken = authToken,
                eventId = eventId,
                title = title,
                bodyText = body
            )

            result
                .onSuccess { newAnnouncement ->
                    val current = _uiState.value.announcements.toMutableList()
                    current.add(0, newAnnouncement) // terbaru di atas
                    _uiState.value = _uiState.value.copy(
                        announcements = current,
                        isPostingAnnouncement = false
                    )
                    onResult(true, "Pengumuman berhasil dibuat.")
                }
                .onFailure { e ->
                    val msg = e.message ?: "Gagal membuat pengumuman"
                    _uiState.value = _uiState.value.copy(
                        isPostingAnnouncement = false
                    )
                    onResult(false, msg)
                }
        }
    }
}

class PanitiaKelolaEventViewModelFactory(
    private val eventRepository: EventRepository,
    private val authToken: String,
    private val eventId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PanitiaKelolaEventViewModel::class.java)) {
            return PanitiaKelolaEventViewModel(
                eventRepository = eventRepository,
                authToken = authToken,
                eventId = eventId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
