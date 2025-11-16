package com.example.projectakhirpamterapan.data.remote

import com.example.projectakhirpamterapan.model.BasicResponse
import com.example.projectakhirpamterapan.model.CreateEventRequest
import com.example.projectakhirpamterapan.model.Event
import com.example.projectakhirpamterapan.model.EventsResponse
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

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<BasicResponse>

    @GET("events/panitia")
    suspend fun getPanitiaEvents(
        @Header("Authorization") authHeader: String,
        @Query("createdBy") createdByUserId: Int
    ): Response<EventsResponse>

    @POST("events")
    suspend fun createEvent(
        @Header("Authorization") token: String,
        @Body request: CreateEventRequest
    ): Event

    // ========= QR INVITATION =========

    @GET("events/{eventId}/qrinvitation")
    suspend fun getQrInvitation(
        @Header("Authorization") token: String,
        @Path("eventId") eventId: Int
    ): Response<QrInvitationResponse>

    @DELETE("events/{eventId}/qrinvitation")
    suspend fun deleteQrInvitation(
        @Header("Authorization") token: String,
        @Path("eventId") eventId: Int
    ): Response<BasicResponse>
}
