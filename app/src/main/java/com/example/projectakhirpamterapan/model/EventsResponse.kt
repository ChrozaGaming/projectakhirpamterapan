package com.example.projectakhirpamterapan.model

data class EventsResponse(
    val success: Boolean,
    val message: String,
    val data: List<Event>?
)
