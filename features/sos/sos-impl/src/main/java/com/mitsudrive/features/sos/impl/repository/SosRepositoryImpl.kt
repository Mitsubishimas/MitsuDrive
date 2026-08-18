package com.mitsudrive.features.sos.impl.repository

import com.mitsudrive.features.sos.api.SosRepository
import com.mitsudrive.features.sos.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SosRepositoryImpl : SosRepository {
    
    // Активные SOS-алерты
    private val _activeAlerts = MutableStateFlow<List<SosAlert>>(emptyList())
    
    // Мой активный SOS
    private val _myActiveSos = MutableStateFlow<SosAlert?>(null)
    
    private val mutex = Mutex()
    
    override suspend fun sendSos(request: SosRequest): Result<SosResponse> {
        return try {
            val sosResponse = SosResponse(
                sosId = "sos_${System.currentTimeMillis()}",
                notifiedUsers = 5,
                timestamp = System.currentTimeMillis().toString(),
                status = SosStatus.ACTIVE
            )
            
            val alert = SosAlert(
                id = sosResponse.sosId,
                userId = "current_user",
                username = "Вы",
                lat = request.lat,
                lng = request.lng,
                message = request.message,
                timestamp = sosResponse.timestamp,
                distance = null,
                status = SosStatus.ACTIVE
            )
            
            _myActiveSos.value = alert
            
            Result.success(sosResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelSos(sosId: String): Result<Unit> {
        return try {
            _myActiveSos.value = null
            _activeAlerts.update { current ->
                current.filter { it.id != sosId }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeActiveAlerts(): Flow<List<SosAlert>> {
        return _activeAlerts.asStateFlow()
    }
    
    override suspend fun respondToSos(sosId: String, message: String?): Result<Unit> {
        return try {
            // TODO: Отправка ответа на сервер
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resolveSos(sosId: String): Result<Unit> {
        return try {
            _activeAlerts.update { current ->
                current.map { alert ->
                    if (alert.id == sosId) {
                        alert.copy(status = SosStatus.RESOLVED)
                    } else {
                        alert
                    }
                }
            }
            
            if (_myActiveSos.value?.id == sosId) {
                _myActiveSos.value = null
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeMyActiveSos(): Flow<SosAlert?> {
        return _myActiveSos.asStateFlow()
    }
    
    // Загрузка активных SOS при открытии
    suspend fun loadActiveAlerts() {
        mutex.withLock {
            // TODO: Загрузка с сервера
            // Имитация данных
            val mockAlerts = listOf(
                SosAlert(
                    id = "sos_1",
                    userId = "user_1",
                    username = "Дмитрий",
                    lat = 55.7558,
                    lng = 37.6173,
                    message = "Нужна помощь! Спустило колесо.",
                    timestamp = System.currentTimeMillis().toString(),
                    distance = 250.0,
                    status = SosStatus.ACTIVE
                )
            )
            
            _activeAlerts.value = mockAlerts
        }
    }
}
