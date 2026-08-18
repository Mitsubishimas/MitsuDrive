package com.mitsudrive.features.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsudrive.core.auth.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RegisterUiState(
    val phone: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone, error = null) }
    }
    
    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, error = null) }
    }
    
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }
    
    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, error = null) }
    }
    
    fun register() {
        val state = _uiState.value
        
        if (state.phone.isBlank()) {
            _uiState.update { it.copy(error = "Введите номер телефона") }
            return
        }
        
        if (state.username.isBlank()) {
            _uiState.update { it.copy(error = "Введите имя пользователя") }
            return
        }
        
        if (state.password.length < 6) {
            _uiState.update { it.copy(error = "Пароль должен быть не менее 6 символов") }
            return
        }
        
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(error = "Пароли не совпадают") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            authRepository.register(state.phone, state.username, state.password)
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
                            error = e.message ?: "Ошибка регистрации"
                        )
                    }
                }
        }
    }
}
