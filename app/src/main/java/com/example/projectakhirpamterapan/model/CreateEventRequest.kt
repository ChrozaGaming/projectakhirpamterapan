package com.example.projectakhirpamterapan.model

data class CreateEventRequest(
    val created_by: Int,
    val title: String,
    val event_date: String,
    val event_time: String,
    val location: String,
    val status: String
)
