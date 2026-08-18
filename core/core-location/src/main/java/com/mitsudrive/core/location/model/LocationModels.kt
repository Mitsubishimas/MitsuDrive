package com.mitsudrive.core.location.model

data class DriveLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val heading: Float? = null,
    val altitude: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class LocationUpdateMode {
    STATIONARY,  // Стоит: раз в 5 минут
    CITY,        // Город: раз в 15 секунд
    HIGHWAY,     // Трасса: раз в 30 секунд
    SOS          // Экстренный: раз в 3 секунды
}

data class LocationConfig(
    val mode: LocationUpdateMode = LocationUpdateMode.CITY,
    val minDistance: Float = 10f, // метры
    val minTimeMs: Long = 15_000L // миллисекунды
) {
    companion object {
        fun fromSpeed(speed: Float?): LocationConfig {
            return when {
                speed == null -> LocationConfig(LocationUpdateMode.STATIONARY, 50f, 300_000L)
                speed < 5 -> LocationConfig(LocationUpdateMode.STATIONARY, 50f, 300_000L)
                speed < 50 -> LocationConfig(LocationUpdateMode.CITY, 10f, 15_000L)
                else -> LocationConfig(LocationUpdateMode.HIGHWAY, 30f, 30_000L)
            }
        }
    }
}

sealed class LocationState {
    object Loading : LocationState()
    data class Location(val location: DriveLocation) : LocationState()
    data class Error(val message: String) : LocationState()
    object PermissionDenied : LocationState()
}
