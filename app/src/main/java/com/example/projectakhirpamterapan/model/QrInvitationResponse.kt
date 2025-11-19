package com.example.projectakhirpamterapan.model

data class QrInvitationResponse(
    val success: Boolean,
    val message: String,
    val data: QrInvitation?
)
