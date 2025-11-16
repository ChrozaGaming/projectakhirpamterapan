package com.example.projectakhirpamterapan.model

import com.google.gson.annotations.SerializedName

data class Event(
    val id: Int,
    val created_by: Int,
    val title: String,
    val event_date: String,
    val event_time: String,
    val location: String,
    val status: String,
    val participants: Int,
    @SerializedName("qr_code")
    val qrCode: String? = null
)
