package com.mitsudrive.core.network.api

import com.mitsudrive.core.network.model.ApiResponse
import retrofit2.http.*

data class MapEventDto(
    val id: String,
    val eventType: String,
    val lat: Double,
    val lng: Double,
    val description: String?,
    val userId: String,
    val createdAt: String,
    val expiresAt: String,
    val confirmations: Int
)

data class CreateEventRequest(
    val eventType: String,
    val lat: Double,
    val lng: Double,
    val description: String?
)

data class NearbyUsersRequest(
    val lat: Double,
    val lng: Double,
    val radius: Double = 5000.0 // метры
)

data class NearbyUserDto(
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val lat: Double,
    val lng: Double,
    val distance: Double,
    val isOnline: Boolean
)

interface MapApi {
    @GET("map/events")
    suspend fun getEvents(
        @Query("minLat") minLat: Double,
        @Query("maxLat") maxLat: Double,
        @Query("minLng") minLng: Double,
        @Query("maxLng") maxLng: Double
    ): ApiResponse<List<MapEventDto>>
    
    @POST("map/events")
    suspend fun createEvent(@Body request: CreateEventRequest): ApiResponse<MapEventDto>
    
    @POST("map/events/{eventId}/confirm")
    suspend fun confirmEvent(@Path("eventId") eventId: String): ApiResponse<Unit>
    
    @POST("map/users/nearby")
    suspend fun getNearbyUsers(@Body request: NearbyUsersRequest): ApiResponse<List<NearbyUserDto>>
    
    @POST("map/location")
    suspend fun updateLocation(
        @Body request: UpdateLocationRequest
    ): ApiResponse<Unit>
}

data class UpdateLocationRequest(
    val lat: Double,
    val lng: Double,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val heading: Float? = null
)
