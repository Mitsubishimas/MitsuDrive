package com.mitsudrive.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.mitsudrive.core.location.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class LocationManager(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val _locationState = MutableStateFlow<LocationState>(LocationState.Loading)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()
    
    private val _locationUpdates = MutableSharedFlow<DriveLocation>(
        replay = 1,
        extraBufferCapacity = 10,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val locationUpdates: SharedFlow<DriveLocation> = _locationUpdates.asSharedFlow()
    
    private var locationCallback: LocationCallback? = null
    private var currentConfig: LocationConfig = LocationConfig()
    
    // Получение последней известной локации
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): DriveLocation? {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                DriveLocation(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    accuracy = it.accuracy,
                    speed = it.speed,
                    heading = it.bearing,
                    altitude = it.altitude,
                    timestamp = it.time
                )
            }
        } catch (e: Exception) {
            null
        }
    }
    
    // Начало отслеживания локации
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(config: LocationConfig? = null) {
        currentConfig = config ?: currentConfig
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            currentConfig.minTimeMs
        ).apply {
            setMinUpdateDistanceMeters(currentConfig.minDistance)
            setMaxUpdateDelayMillis(currentConfig.minTimeMs * 2)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        }.build()
        
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val driveLocation = location.toDriveLocation()
                    _locationUpdates.tryEmit(driveLocation)
                    _locationState.value = LocationState.Location(driveLocation)
                    
                    // Автоматически обновляем конфигурацию при изменении скорости
                    val newConfig = LocationConfig.fromSpeed(location.speed)
                    if (newConfig.mode != currentConfig.mode) {
                        currentConfig = newConfig
                        startLocationUpdates(newConfig)
                    }
                }
            }
        }
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        ).addOnFailureListener { e ->
            _locationState.value = LocationState.Error(e.message ?: "Location update failed")
        }
    }
    
    // Остановка отслеживания
    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationCallback = null
    }
    
    // Проверка разрешений
    suspend fun hasLocationPermission(): Boolean {
        return try {
            val result = fusedLocationClient.lastLocation.await()
            result != null
        } catch (e: SecurityException) {
            false
        }
    }
    
    // Вычисление расстояния между точками
    fun calculateDistance(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0]
    }
    
    // Проверка, находится ли точка в радиусе
    fun isWithinRadius(
        centerLat: Double,
        centerLng: Double,
        pointLat: Double,
        pointLng: Double,
        radiusMeters: Double
    ): Boolean {
        val distance = calculateDistance(centerLat, centerLng, pointLat, pointLng)
        return distance <= radiusMeters
    }
    
    private fun Location.toDriveLocation(): DriveLocation {
        return DriveLocation(
            latitude = latitude,
            longitude = longitude,
            accuracy = accuracy,
            speed = speed,
            heading = bearing,
            altitude = altitude,
            timestamp = time
        )
    }
}
