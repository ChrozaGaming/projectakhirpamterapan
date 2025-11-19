package com.example.projectakhirpamterapan.data

import com.example.projectakhirpamterapan.data.remote.ApiService
import com.example.projectakhirpamterapan.model.BasicResponse
import com.example.projectakhirpamterapan.model.CreateEventRequest
import com.example.projectakhirpamterapan.model.Event
import com.example.projectakhirpamterapan.model.EventsResponse
import com.example.projectakhirpamterapan.model.JoinByQrRequest
import com.example.projectakhirpamterapan.model.JoinEventResponse
import com.example.projectakhirpamterapan.model.QrInvitation
import com.example.projectakhirpamterapan.model.QrInvitationResponse
import retrofit2.Response

class EventRepository(
    private val apiService: ApiService
) {

    private fun handleEventsResponse(
        response: Response<EventsResponse>,
        defaultError: String
    ): Result<List<Event>> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: defaultError))
            }
        } else {
            Result.failure(
                Exception("Error ${response.code()}: ${response.errorBody()?.string()}")
            )
        }
    }

    // =================== PANITIA ===================

    suspend fun getPanitiaEvents(
        authToken: String,
        createdByUserId: Int
    ): Result<List<Event>> {
        return try {
            val response = apiService.getPanitiaEvents(
                authHeader = "Bearer $authToken",
                createdByUserId = createdByUserId
            )
            handleEventsResponse(response, "Gagal memuat event panitia")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(
        authToken: String,
        request: CreateEventRequest
    ): Result<Event> {
        return try {
            val created = apiService.createEvent(
                authHeader = "Bearer $authToken",
                request = request
            )
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================== QR INVITATION ===================

    suspend fun getQrInvitation(
        authToken: String,
        eventId: Int
    ): Result<QrInvitation> {
        return try {
            val response = apiService.getQrInvitation(
                authHeader = "Bearer $authToken",
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
                Result.failure(
                    Exception("Error ${response.code()}: ${response.errorBody()?.string()}")
                )
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
                authHeader = "Bearer $authToken",
                eventId = eventId
            )

            if (response.isSuccessful) {
                val body: BasicResponse? = response.body()
                if (body != null && body.success) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal menghapus QR undangan"))
                }
            } else {
                Result.failure(
                    Exception("Error ${response.code()}: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================== PESERTA ===================

    suspend fun getPesertaEvents(
        authToken: String,
        userId: Int
    ): Result<List<Event>> {
        return try {
            val response = apiService.getPesertaEvents(
                authHeader = "Bearer $authToken",
                userId = userId
            )
            handleEventsResponse(response, "Gagal memuat event peserta")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Join event dengan kode QR invitation.
     * Backend akan pakai req.userId dari token untuk isi event_participants.
     */
    suspend fun joinEventByQr(
        authToken: String,
        qrCode: String
    ): Result<Event> {
        return try {
            val response = apiService.joinEventByQr(
                authHeader = "Bearer $authToken",
                body = JoinByQrRequest(
                    qr_code = qrCode.trim().uppercase()
                )
            )

            if (response.isSuccessful) {
                val body: JoinEventResponse? = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal join event"))
                }
            } else {
                Result.failure(
                    Exception("Error ${response.code()}: ${response.errorBody()?.string()}")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
