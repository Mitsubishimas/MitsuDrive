package com.mitsudrive.features.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsudrive.core.auth.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val smsCode: String = "",
    val isSmsSent: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }
    
    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }
    
    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone, error = null) }
    }
    
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }
    
    fun onConfirmPasswordChange(confirm: String) {
        _uiState.update { it.copy(confirmPassword = confirm, error = null) }
    }
    
    fun onSmsCodeChange(code: String) {
        _uiState.update { it.copy(smsCode = code, error = null) }
    }
    
    fun sendSms() {
        val state = _uiState.value
        
        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.update { it.copy(error = "Введите корректный email") }
            return
        }
        
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Введите имя") }
            return
        }
        
        if (state.phone.isBlank()) {
            _uiState.update { it.copy(error = "Введите телефон") }
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
            
            authRepository.sendSmsCode(state.phone)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSmsSent = true) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Ошибка отправки SMS")
                    }
                }
        }
    }
    
    fun resendSms() {
        val phone = _uiState.value.phone
        viewModelScope.launch {
            authRepository.sendSmsCode(phone)
        }
    }
    
    fun register() {
        val state = _uiState.value
        
        if (state.smsCode.isBlank()) {
            _uiState.update { it.copy(error = "Введите код из SMS") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Сначала проверяем SMS код
            authRepository.verifySmsCode(state.phone, state.smsCode)
                .onSuccess {
                    // Код верный — регистрируем
                    authRepository.register(
                        email = state.email,
                        password = state.password,
                        name = state.name,
                        phone = state.phone
                    ).onSuccess {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }.onFailure { e ->
                        _uiState.update {
                            it.copy(isLoading = false, error = e.message ?: "Ошибка регистрации")
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Неверный SMS код")
                    }
                }
        }
    }
}
