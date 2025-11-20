package com.example.projectakhirpamterapan.model

/* ==============================
   ABSENSI EVENT
   ============================== */

data class EventAttendance(
    val id: Int,
    val event_id: Int,
    val user_id: Int,
    val status: String,
    val checked_in_at: String
)

data class EventAttendanceResponse(
    val success: Boolean,
    val message: String?,
    val data: EventAttendance?
)

data class AttendanceRequest(
    val status: String? = null      // "hadir" | "izin" | "alfa" (opsional; default 'hadir' di backend)
)

/* ==============================
   ANNOUNCEMENT EVENT
   ============================== */

data class EventAnnouncement(
    val id: Int,
    val event_id: Int,
    val title: String,
    val body: String,
    val created_by: Int,
    val created_by_name: String,
    val created_at: String
)

data class EventAnnouncementsResponse(
    val success: Boolean,
    val message: String?,
    val data: List<EventAnnouncement>?
)

data class EventAnnouncementResponse(
    val success: Boolean,
    val message: String?,
    val data: EventAnnouncement?
)

data class CreateAnnouncementRequest(
    val title: String,
    val body: String
)

/* ==============================
   PARTICIPANTS (JOIN events + event_participants + users + attendance)
   ============================== */

data class EventParticipant(
    val user_id: Int,
    val name: String,
    val email: String,
    val role_in_event: String,   // 'peserta' / 'panitia'
    val status: String?,         // 'hadir' | 'izin' | 'alfa' | null (belum absen)
    val checked_in_at: String?   // bisa null kalau belum pernah absen
)

data class EventParticipantsResponse(
    val success: Boolean,
    val message: String?,
    val data: List<EventParticipant>?
)
