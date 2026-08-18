package com.mitsudrive.features.map.impl.repository

import com.mitsudrive.features.map.api.MapRepository
import com.mitsudrive.features.map.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MapRepositoryImpl : MapRepository {
    
    // Кэш событий
    private val _events = MutableStateFlow<List<MapEvent>>(emptyList())
    
    // Кэш пользователей рядом
    private val _nearbyUsers = MutableStateFlow<List<NearbyUser>>(emptyList())
    
    private val mutex = Mutex()
    
    override fun observeEvents(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<List<MapEvent>> {
        return _events.map { events ->
            events.filter { event ->
                event.lat in minLat..maxLat &&
                event.lng in minLng..maxLng &&
                event.expiresAt.toLong() > System.currentTimeMillis()
            }
        }.distinctUntilChanged()
    }
    
    override suspend fun createEvent(
        request: CreateMapEventRequest
    ): Result<MapEvent> {
        return try {
            val event = MapEvent(
                id = "event_${System.currentTimeMillis()}",
                eventType = request.eventType,
                lat = request.lat,
                lng = request.lng,
                description = request.description,
                userId = "current_user",
                username = "Вы",
                createdAt = System.currentTimeMillis().toString(),
                expiresAt = (System.currentTimeMillis() + 4 * 60 * 60 * 1000).toString(),
                confirmations = 1,
                isConfirmed = true
            )
            
            _events.update { current -> listOf(event) + current }
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun confirmEvent(eventId: String): Result<Unit> {
        return try {
            _events.update { current ->
                current.map { event ->
                    if (event.id == eventId && !event.isConfirmed) {
                        event.copy(
                            confirmations = event.confirmations + 1,
                            isConfirmed = true
                        )
                    } else {
                        event
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun unconfirmEvent(eventId: String): Result<Unit> {
        return try {
            _events.update { current ->
                current.map { event ->
                    if (event.id == eventId && event.isConfirmed) {
                        event.copy(
                            confirmations = (event.confirmations - 1).coerceAtLeast(0),
                            isConfirmed = false
                        )
                    } else {
                        event
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getNearbyUsers(
        lat: Double,
        lng: Double,
        radius: Double
    ): Result<List<NearbyUser>> {
        return try {
            // TODO: Запрос к серверу
            // Имитация данных
            val mockUsers = listOf(
                NearbyUser(
                    userId = "user_1",
                    username = "Дмитрий",
                    avatarUrl = null,
                    lat = lat + 0.001,
                    lng = lng + 0.001,
                    distance = 150.0,
                    isOnline = true
                ),
                NearbyUser(
                    userId = "user_2",
                    username = "Алексей",
                    avatarUrl = null,
                    lat = lat - 0.002,
                    lng = lng + 0.001,
                    distance = 300.0,
                    isOnline = true
                )
            )
            
            _nearbyUsers.value = mockUsers
            Result.success(mockUsers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateLocation(
        lat: Double,
        lng: Double
    ): Result<Unit> {
        return try {
            // TODO: Отправка на сервер
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            _events.update { current -> current.filter { it.id != eventId } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Загрузка событий при открытии карты
    suspend fun loadEvents(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ) {
        mutex.withLock {
            // TODO: Загрузка с сервера
            // Имитация данных
            val mockEvents = createMockEvents()
            _events.update { current ->
                (current + mockEvents).distinctBy { it.id }
            }
        }
    }
    
    private fun createMockEvents(): List<MapEvent> {
        val now = System.currentTimeMillis()
        return listOf(
            MapEvent(
                id = "event_1",
                eventType = MapEventType.ACCIDENT,
                lat = 55.7558,
                lng = 37.6173,
                description = "ДТП на перекрёстке",
                userId = "user_1",
                username = "Дмитрий",
                createdAt = now.toString(),
                expiresAt = (now + 4 * 60 * 60 * 1000).toString(),
                confirmations = 5,
                isConfirmed = false
            ),
            MapEvent(
                id = "event_2",
                eventType = MapEventType.CAMERA,
                lat = 55.7458,
                lng = 37.6273,
                description = "Камера на столбе",
                userId = "user_2",
                username = "Алексей",
                createdAt = now.toString(),
                expiresAt = (now + 2 * 60 * 60 * 1000).toString(),
                confirmations = 3,
                isConfirmed = false
            ),
            MapEvent(
                id = "event_3",
                eventType = MapEventType.TRAFFIC,
                lat = 55.7658,
                lng = 37.6073,
                description = "Пробка 5 км",
                userId = "user_3",
                username = "Сергей",
                createdAt = now.toString(),
                expiresAt = (now + 60 * 60 * 1000).toString(),
                confirmations = 8,
                isConfirmed = false
            )
        )
    }
}
