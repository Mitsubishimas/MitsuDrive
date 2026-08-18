package com.mitsudrive.core.network.api

import com.mitsudrive.core.network.model.ApiResponse
import retrofit2.http.*

data class SosRequest(
    val lat: Double,
    val lng: Double,
    val message: String?,
    val radius: Double = 5000.0
)

data class SosResponse(
    val sosId: String,
    val notifiedUsers: Int,
    val timestamp: String
)

interface SosApi {
    @POST("sos/send")
    suspend fun sendSos(@Body request: SosRequest): ApiResponse<SosResponse>
    
    @POST("sos/{sosId}/cancel")
    suspend fun cancelSos(@Path("sosId") sosId: String): ApiResponse<Unit>
    
    @GET("sos/active")
    suspend fun getActiveSos(): ApiResponse<List<SosResponse>>
}
