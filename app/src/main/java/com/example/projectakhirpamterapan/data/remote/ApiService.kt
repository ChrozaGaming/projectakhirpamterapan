package com.example.projectakhirpamterapan.data.remote

import com.example.projectakhirpamterapan.model.BasicResponse
import com.example.projectakhirpamterapan.model.CreateEventRequest
import com.example.projectakhirpamterapan.model.Event
import com.example.projectakhirpamterapan.model.EventsResponse
import com.example.projectakhirpamterapan.model.JoinByQrRequest
import com.example.projectakhirpamterapan.model.JoinEventResponse
import com.example.projectakhirpamterapan.model.LoginRequest
import com.example.projectakhirpamterapan.model.LoginResponse
import com.example.projectakhirpamterapan.model.QrInvitationResponse
import com.example.projectakhirpamterapan.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ================= AUTH =================

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<BasicResponse>

    // ================= PANITIA =================

    @GET("events/panitia")
    suspend fun getPanitiaEvents(
        @Header("Authorization") authHeader: String,
        @Query("createdBy") createdByUserId: Int
    ): Response<EventsResponse>

    @POST("events")
    suspend fun createEvent(
        @Header("Authorization") authHeader: String,
        @Body request: CreateEventRequest
    ): Event

    // ================= QR INVITATION =================

    @GET("events/{eventId}/qrinvitation")
    suspend fun getQrInvitation(
        @Header("Authorization") authHeader: String,
        @Path("eventId") eventId: Int
    ): Response<QrInvitationResponse>

    @DELETE("events/{eventId}/qrinvitation")
    suspend fun deleteQrInvitation(
        @Header("Authorization") authHeader: String,
        @Path("eventId") eventId: Int
    ): Response<BasicResponse>

    // ================= PESERTA =================

    @GET("events/peserta")
    suspend fun getPesertaEvents(
        @Header("Authorization") authHeader: String,
        @Query("userId") userId: Int
    ): Response<EventsResponse>

    @POST("events/join-by-qr")
    suspend fun joinEventByQr(
        @Header("Authorization") authHeader: String,
        @Body body: JoinByQrRequest   // body: { "qr_code": "ABC123..." }
    ): Response<JoinEventResponse>
}
