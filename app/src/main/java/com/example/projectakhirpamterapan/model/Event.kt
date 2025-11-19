package com.example.projectakhirpamterapan.model

data class Event(
    val id: Int,
    val created_by: Int,
    val title: String,
    val event_date: String?,
    val event_time: String?,
    val location: String,
    val status: String,
    val participants: Int,
    val qr_code: String? = null
)
