package com.example.projectakhirpamterapan.data

import com.example.projectakhirpamterapan.data.remote.ApiService
import com.example.projectakhirpamterapan.model.BasicResponse
import com.example.projectakhirpamterapan.model.LoginRequest
import com.example.projectakhirpamterapan.model.LoginResponse

class AuthRepository(
    private val apiService: ApiService
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.token != null && body.user != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.message ?: "Login gagal"))
                }
            } else {
                // 401 dll → di kasih pesan yang jelas ya bang
                val message = when (response.code()) {
                    401 -> "Email atau password salah"
                    else -> "Login gagal (${response.code()})"
                }
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<BasicResponse> {
        return try {
            val response = apiService.register(
                com.example.projectakhirpamterapan.model.RegisterRequest(
                    name = name,
                    email = email,
                    password = password
                )
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.message ?: "Registrasi gagal"))
                }
            } else {
                val message = when (response.code()) {
                    400 -> "Email sudah terdaftar"
                    else -> "Registrasi gagal (${response.code()})"
                }
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
