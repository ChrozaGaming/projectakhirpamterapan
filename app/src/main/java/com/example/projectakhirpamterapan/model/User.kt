package com.example.projectakhirpamterapan.model
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "peserta"
)

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val token: String?,
    val user: User?
)

data class BasicResponse(
    val success: Boolean,
    val message: String?,
    val user: User?
)
