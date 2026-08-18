package com.mitsudrive.features.map.api

import com.mitsudrive.features.map.api.model.*
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    // События в области
    fun observeEvents(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<List<MapEvent>>
    
    // Создание события
    suspend fun createEvent(request: CreateMapEventRequest): Result<MapEvent>
    
    // Подтверждение события
    suspend fun confirmEvent(eventId: String): Result<Unit>
    
    // Отмена подтверждения
    suspend fun unconfirmEvent(eventId: String): Result<Unit>
    
    // Пользователи рядом
    suspend fun getNearbyUsers(
        lat: Double,
        lng: Double,
        radius: Double
    ): Result<List<NearbyUser>>
    
    // Обновление позиции
    suspend fun updateLocation(
        lat: Double,
        lng: Double
    ): Result<Unit>
    
    // Удаление события
    suspend fun deleteEvent(eventId: String): Result<Unit>
}
