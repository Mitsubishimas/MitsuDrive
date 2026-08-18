package com.mitsudrive.features.map.api.model

data class MapEvent(
    val id: String,
    val eventType: MapEventType,
    val lat: Double,
    val lng: Double,
    val description: String?,
    val userId: String,
    val username: String,
    val createdAt: String,
    val expiresAt: String,
    val confirmations: Int,
    val isConfirmed: Boolean
)

enum class MapEventType {
    ACCIDENT,
    CAMERA,
    DANGER,
    TRAFFIC,
    ROAD_WORK
}

data class NearbyUser(
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val lat: Double,
    val lng: Double,
    val distance: Double,
    val isOnline: Boolean
)

data class CreateMapEventRequest(
    val eventType: MapEventType,
    val lat: Double,
    val lng: Double,
    val description: String?
)
