package com.mitsudrive.features.sos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsudrive.core.location.LocationManager
import com.mitsudrive.features.sos.api.SosRepository
import com.mitsudrive.features.sos.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SosUiState(
    val activeAlerts: List<SosAlert> = emptyList(),
    val myActiveSos: SosAlert? = null,
    val isSending: Boolean = false,
    val message: String = "",
    val error: String? = null
)

class SosViewModel(
    private val sosRepository: SosRepository,
    private val locationManager: LocationManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SosUiState())
    val uiState: StateFlow<SosUiState> = _uiState.asStateFlow()
    
    init {
        observeActiveAlerts()
        observeMyActiveSos()
    }
    
    private fun observeActiveAlerts() {
        viewModelScope.launch {
            sosRepository.observeActiveAlerts().collect { alerts ->
                _uiState.update { it.copy(activeAlerts = alerts) }
            }
        }
    }
    
    private fun observeMyActiveSos() {
        viewModelScope.launch {
            sosRepository.observeMyActiveSos().collect { sos ->
                _uiState.update { it.copy(myActiveSos = sos) }
            }
        }
    }
    
    fun onMessageChange(message: String) {
        _uiState.update { it.copy(message = message) }
    }
    
    fun sendSos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, error = null) }
            
            val location = locationManager.getLastLocation()
            if (location != null) {
                sosRepository.sendSos(
                    SosRequest(
                        lat = location.latitude,
                        lng = location.longitude,
                        message = _uiState.value.message.ifBlank { null },
                        radius = 5000.0
                    )
                ).onSuccess {
                    _uiState.update { it.copy(isSending = false, message = "") }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(isSending = false, error = e.message)
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSending = false,
                        error = "Не удалось определить местоположение"
                    )
                }
            }
        }
    }
    
    fun cancelSos() {
        val sosId = _uiState.value.myActiveSos?.id ?: return
        
        viewModelScope.launch {
            sosRepository.cancelSos(sosId)
        }
    }
    
    fun respondToSos(sosId: String) {
        viewModelScope.launch {
            sosRepository.respondToSos(sosId, "Готов помочь!")
        }
    }
    
    fun resolveSos(sosId: String) {
        viewModelScope.launch {
            sosRepository.resolveSos(sosId)
        }
    }
}
