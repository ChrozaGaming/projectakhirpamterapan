package com.example.projectakhirpamterapan.data

import com.example.projectakhirpamterapan.data.remote.ApiService
import com.example.projectakhirpamterapan.model.CreateEventRequest
import com.example.projectakhirpamterapan.model.Event
import com.example.projectakhirpamterapan.model.EventsResponse
import com.example.projectakhirpamterapan.model.QrInvitation
import com.example.projectakhirpamterapan.model.QrInvitationResponse

class EventRepository(
    private val apiService: ApiService
) {

    suspend fun getPanitiaEvents(
        authToken: String,
        createdByUserId: Int
    ): Result<List<Event>> {
        return try {
            val response = apiService.getPanitiaEvents(
                authHeader = "Bearer $authToken",
                createdByUserId = createdByUserId
            )

            if (response.isSuccessful) {
                val body: EventsResponse? = response.body()

                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal memuat event"))
                }

            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.errorBody()?.string()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(
        authToken: String,
        request: CreateEventRequest
    ): Result<Event> {
        return try {
            val event = apiService.createEvent(
                token = "Bearer $authToken",
                request = request
            )
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========= QR INVITATION =========

    suspend fun getQrInvitation(
        authToken: String,
        eventId: Int
    ): Result<QrInvitation> {
        return try {
            val response = apiService.getQrInvitation(
                token = "Bearer $authToken",
                eventId = eventId
            )

            if (response.isSuccessful) {
                val body: QrInvitationResponse? = response.body()

                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal memuat QR undangan"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteQrInvitation(
        authToken: String,
        eventId: Int
    ): Result<Unit> {
        return try {
            val response = apiService.deleteQrInvitation(
                token = "Bearer $authToken",
                eventId = eventId
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal menghapus QR undangan"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
