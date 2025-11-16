package com.example.projectakhirpamterapan.model

data class QrInvitation(
    val id: Int,
    val event_id: Int,
    val qr_code: String,
    val created_at: String
)

data class QrInvitationResponse(
    val success: Boolean,
    val message: String,
    val data: QrInvitation?
)
