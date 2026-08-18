package com.mitsudrive.features.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsudrive.core.auth.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoginUiState(
    val phone: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone, error = null) }
    }
    
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }
    
    fun login() {
        val state = _uiState.value
        
        if (state.phone.isBlank()) {
            _uiState.update { it.copy(error = "Введите номер телефона") }
            return
        }
        
        if (state.password.isBlank()) {
            _uiState.update { it.copy(error = "Введите пароль") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            authRepository.login(state.phone, state.password)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Ошибка входа"
                        )
                    }
                }
        }
    }
}
