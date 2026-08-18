package com.mitsudrive.features.garage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsudrive.features.garage.api.GarageRepository
import com.mitsudrive.features.garage.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GarageUiState(
    val cars: List<Car> = emptyList(),
    val reminders: List<ServiceReminder> = emptyList(),
    val isAddingCar: Boolean = false,
    val newCarBrand: String = "",
    val newCarModel: String = "",
    val newCarYear: String = "",
    val newCarMileage: String = "",
    val error: String? = null
)

class GarageViewModel(
    private val garageRepository: GarageRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GarageUiState())
    val uiState: StateFlow<GarageUiState> = _uiState.asStateFlow()
    
    init {
        observeCars()
        observeReminders()
    }
    
    private fun observeCars() {
        viewModelScope.launch {
            garageRepository.observeCars().collect { cars ->
                _uiState.update { it.copy(cars = cars) }
            }
        }
    }
    
    private fun observeReminders() {
        viewModelScope.launch {
            garageRepository.observeReminders().collect { reminders ->
                _uiState.update { it.copy(reminders = reminders) }
            }
        }
    }
    
    fun toggleAddCar() {
        _uiState.update { it.copy(isAddingCar = !it.isAddingCar) }
    }
    
    fun onBrandChange(brand: String) {
        _uiState.update { it.copy(newCarBrand = brand) }
    }
    
    fun onModelChange(model: String) {
        _uiState.update { it.copy(newCarModel = model) }
    }
    
    fun onYearChange(year: String) {
        _uiState.update { it.copy(newCarYear = year) }
    }
    
    fun onMileageChange(mileage: String) {
        _uiState.update { it.copy(newCarMileage = mileage) }
    }
    
    fun addCar() {
        val state = _uiState.value
        
        if (state.newCarBrand.isBlank() || state.newCarModel.isBlank()) {
            _uiState.update { it.copy(error = "Введите марку и модель") }
            return
        }
        
        viewModelScope.launch {
            garageRepository.addCar(
                CreateCarRequest(
                    brand = state.newCarBrand,
                    model = state.newCarModel,
                    year = state.newCarYear.toIntOrNull(),
                    vin = null,
                    mileage = state.newCarMileage.toIntOrNull() ?: 0
                )
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isAddingCar = false,
                        newCarBrand = "",
                        newCarModel = "",
                        newCarYear = "",
                        newCarMileage = "",
                        error = null
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun deleteCar(carId: String) {
        viewModelScope.launch {
            garageRepository.deleteCar(carId)
        }
    }
    
    fun addReminder(
        carId: String,
        type: ServiceType,
        dueDate: String
    ) {
        viewModelScope.launch {
            garageRepository.addReminder(
                ServiceReminder(
                    carId = carId,
                    type = type,
                    dueDate = dueDate,
                    mileageDue = null
                )
            )
        }
    }
}
