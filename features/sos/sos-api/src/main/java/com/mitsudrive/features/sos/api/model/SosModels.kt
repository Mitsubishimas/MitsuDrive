package com.mitsudrive.features.sos.api.model

data class SosRequest(
    val lat: Double,
    val lng: Double,
    val message: String?,
    val radius: Double = 5000.0
)

data class SosResponse(
    val sosId: String,
    val notifiedUsers: Int,
    val timestamp: String,
    val status: SosStatus
)

enum class SosStatus {
    ACTIVE,
    CANCELLED,
    RESOLVED,
    EXPIRED
}

data class SosAlert(
    val id: String,
    val userId: String,
    val username: String,
    val lat: Double,
    val lng: Double,
    val message: String?,
    val timestamp: String,
    val distance: Double?,
    val status: SosStatus
)

data class RespondRequest(
    val sosId: String,
    val message: String?
)
