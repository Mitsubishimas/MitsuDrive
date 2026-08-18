package com.mitsudrive.features.map.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsudrive.core.location.LocationManager
import com.mitsudrive.core.location.model.DriveLocation
import com.mitsudrive.features.map.api.MapRepository
import com.mitsudrive.features.map.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MapUiState(
    val events: List<MapEvent> = emptyList(),
    val currentLocation: DriveLocation? = null,
    val nearbyUsers: List<NearbyUser> = emptyList(),
    val selectedEvent: MapEvent? = null,
    val isCreatingEvent: Boolean = false,
    val isSosActive: Boolean = false,
    val error: String? = null
)

class MapViewModel(
    private val mapRepository: MapRepository,
    private val locationManager: LocationManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
    
    // Текущие границы карты
    private var currentBounds: Pair<Pair<Double, Double>, Pair<Double, Double>>? = null
    
    init {
        observeLocation()
        observeEvents()
        startLocationUpdates()
    }
    
    private fun observeLocation() {
        viewModelScope.launch {
            locationManager.locationUpdates.collect { location ->
                _uiState.update { it.copy(currentLocation = location) }
                
                // Загружаем пользователей рядом
                loadNearbyUsers(location.latitude, location.longitude)
            }
        }
    }
    
    private fun observeEvents() {
        viewModelScope.launch {
            // Наблюдаем за событиями в широкой области (Москва)
            mapRepository.observeEvents(
                minLat = 55.3,
                maxLat = 56.1,
                minLng = 37.2,
                maxLng = 38.2
            ).collect { events ->
                _uiState.update { it.copy(events = events) }
            }
        }
    }
    
    private fun loadNearbyUsers(lat: Double, lng: Double) {
        viewModelScope.launch {
            mapRepository.getNearbyUsers(lat, lng, 5000.0)
                .onSuccess { users ->
                    _uiState.update { it.copy(nearbyUsers = users) }
                }
        }
    }
    
    fun createEvent(
        eventType: MapEventType,
        description: String?
    ) {
        val location = _uiState.value.currentLocation ?: return
        
        viewModelScope.launch {
            mapRepository.createEvent(
                CreateMapEventRequest(
                    eventType = eventType,
                    lat = location.latitude,
                    lng = location.longitude,
                    description = description
                )
            ).onSuccess {
                _uiState.update { state ->
                    state.copy(isCreatingEvent = false)
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun confirmEvent(eventId: String) {
        viewModelScope.launch {
            mapRepository.confirmEvent(eventId)
        }
    }
    
    fun unconfirmEvent(eventId: String) {
        viewModelScope.launch {
            mapRepository.unconfirmEvent(eventId)
        }
    }
    
    fun selectEvent(event: MapEvent?) {
        _uiState.update { it.copy(selectedEvent = event) }
    }
    
    fun toggleCreateEvent() {
        _uiState.update { it.copy(isCreatingEvent = !it.isCreatingEvent) }
    }
    
    fun toggleSos() {
        _uiState.update { it.copy(isSosActive = !it.isSosActive) }
    }
    
    fun startLocationUpdates() {
        locationManager.startLocationUpdates()
    }
    
    fun stopLocationUpdates() {
        locationManager.stopLocationUpdates()
    }
    
    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}
