package com.mitsudrive.features.garage.api

import com.mitsudrive.features.garage.api.model.*
import kotlinx.coroutines.flow.Flow

interface GarageRepository {
    // Список автомобилей
    fun observeCars(): Flow<List<Car>>
    
    // Добавление автомобиля
    suspend fun addCar(request: CreateCarRequest): Result<Car>
    
    // Обновление автомобиля
    suspend fun updateCar(
        carId: String,
        request: UpdateCarRequest
    ): Result<Car>
    
    // Удаление автомобиля
    suspend fun deleteCar(carId: String): Result<Unit>
    
    // Напоминания о ТО
    fun observeReminders(): Flow<List<ServiceReminder>>
    
    // Добавление напоминания
    suspend fun addReminder(reminder: ServiceReminder): Result<Unit>
    
    // Удаление напоминания
    suspend fun deleteReminder(reminderId: String): Result<Unit>
}
