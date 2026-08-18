package com.mitsudrive.features.sos.api

import com.mitsudrive.features.sos.api.model.*
import kotlinx.coroutines.flow.Flow

interface SosRepository {
    // Отправка SOS
    suspend fun sendSos(request: SosRequest): Result<SosResponse>
    
    // Отмена SOS
    suspend fun cancelSos(sosId: String): Result<Unit>
    
    // Получение активных SOS
    fun observeActiveAlerts(): Flow<List<SosAlert>>
    
    // Ответ на SOS
    suspend fun respondToSos(sosId: String, message: String?): Result<Unit>
    
    // Отметить как решённый
    suspend fun resolveSos(sosId: String): Result<Unit>
    
    // Проверка активного SOS
    fun observeMyActiveSos(): Flow<SosAlert?>
}
