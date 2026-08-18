package com.mitsudrive.features.garage.impl.repository

import com.mitsudrive.features.garage.api.GarageRepository
import com.mitsudrive.features.garage.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GarageRepositoryImpl : GarageRepository {
    
    // Кэш автомобилей
    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    
    // Кэш напоминаний
    private val _reminders = MutableStateFlow<List<ServiceReminder>>(emptyList())
    
    private val mutex = Mutex()
    
    override fun observeCars(): Flow<List<Car>> {
        return _cars.asStateFlow()
    }
    
    override suspend fun addCar(request: CreateCarRequest): Result<Car> {
        return try {
            val car = Car(
                id = "car_${System.currentTimeMillis()}",
                userId = "current_user",
                brand = request.brand,
                model = request.model,
                year = request.year,
                vin = request.vin,
                mileage = request.mileage,
                lastServiceDate = null,
                nextServiceDate = null,
                isPrimary = _cars.value.isEmpty()
            )
            
            _cars.update { current -> current + car }
            Result.success(car)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCar(
        carId: String,
        request: UpdateCarRequest
    ): Result<Car> {
        return try {
            var updatedCar: Car? = null
            
            _cars.update { current ->
                current.map { car ->
                    if (car.id == carId) {
                        val newCar = car.copy(
                            brand = request.brand ?: car.brand,
                            model = request.model ?: car.model,
                            year = request.year ?: car.year,
                            vin = request.vin ?: car.vin,
                            mileage = request.mileage ?: car.mileage,
                            lastServiceDate = request.lastServiceDate ?: car.lastServiceDate,
                            nextServiceDate = request.nextServiceDate ?: car.nextServiceDate
                        )
                        updatedCar = newCar
                        newCar
                    } else {
                        car
                    }
                }
            }
            
            if (updatedCar != null) {
                Result.success(updatedCar!!)
            } else {
                Result.failure(Exception("Car not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCar(carId: String): Result<Unit> {
        return try {
            _cars.update { current -> current.filter { it.id != carId } }
            
            // Удаляем связанные напоминания
            _reminders.update { current -> current.filter { it.carId != carId } }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeReminders(): Flow<List<ServiceReminder>> {
        return _reminders.asStateFlow()
    }
    
    override suspend fun addReminder(reminder: ServiceReminder): Result<Unit> {
        return try {
            _reminders.update { current -> current + reminder }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            _reminders.update { current -> current.filter { it.carId != reminderId } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Загрузка начальных данных
    suspend fun loadInitialData() {
        mutex.withLock {
            // TODO: Загрузка с сервера
            // Имитация данных
            val mockCars = listOf(
                Car(
                    id = "car_1",
                    userId = "current_user",
                    brand = "Mitsubishi",
                    model = "Outlander XL",
                    year = 2010,
                    vin = "JMBXL123456789012",
                    mileage = 145000,
                    lastServiceDate = "2026-06-15",
                    nextServiceDate = "2026-09-15",
                    isPrimary = true
                )
            )
            
            _cars.value = mockCars
            
            val mockReminders = listOf(
                ServiceReminder(
                    carId = "car_1",
                    type = ServiceType.OIL_CHANGE,
                    dueDate = "2026-09-15",
                    mileageDue = 150000
                ),
                ServiceReminder(
                    carId = "car_1",
                    type = ServiceType.FILTERS,
                    dueDate = "2026-09-15",
                    mileageDue = 150000
                )
            )
            
            _reminders.value = mockReminders
        }
    }
}
