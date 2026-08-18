package com.mitsudrive.core.auth

import com.mitsudrive.core.auth.model.AuthState
import com.mitsudrive.core.auth.model.AuthTokens
import com.mitsudrive.core.auth.model.UserSession
import com.mitsudrive.core.auth.storage.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface AuthRepository {
    val authState: StateFlow<AuthState>
    
    suspend fun login(phone: String, password: String): Result<UserSession>
    suspend fun register(phone: String, username: String, password: String): Result<UserSession>
    suspend fun logout()
    suspend fun refreshToken(): Result<AuthTokens>
    suspend fun getCurrentSession(): UserSession?
}

class AuthRepositoryImpl(
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager
) : AuthRepository {
    
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    init {
        // Загружаем сессию при создании
        repositoryScope.launch {
            val session = sessionManager.session.first()
            if (session != null) {
                _authState.value = AuthState.Authenticated(session)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
    
    override suspend fun login(phone: String, password: String): Result<UserSession> {
        return try {
            // TODO: Вызов API для логина
            // val response = authApi.login(LoginRequest(phone, password))
            // tokenStorage.saveTokens(...)
            // sessionManager.saveSession(...)
            
            // Временная заглушка
            val session = UserSession(
                userId = "user_123",
                username = "driver_$phone",
                avatarUrl = null,
                isAuthenticated = true,
                lastLoginAt = System.currentTimeMillis()
            )
            
            sessionManager.saveSession(session)
            _authState.value = AuthState.Authenticated(session)
            Result.success(session)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Login failed")
            Result.failure(e)
        }
    }
    
    override suspend fun register(
        phone: String,
        username: String,
        password: String
    ): Result<UserSession> {
        return try {
            // TODO: Вызов API для регистрации
            
            val session = UserSession(
                userId = "user_${System.currentTimeMillis()}",
                username = username,
                avatarUrl = null,
                isAuthenticated = true,
                lastLoginAt = System.currentTimeMillis()
            )
            
            sessionManager.saveSession(session)
            _authState.value = AuthState.Authenticated(session)
            Result.success(session)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Registration failed")
            Result.failure(e)
        }
    }
    
    override suspend fun logout() {
        sessionManager.clearSession()
        _authState.value = AuthState.Unauthenticated
    }
    
    override suspend fun refreshToken(): Result<AuthTokens> {
        return try {
            // TODO: Вызов API для обновления токена
            val tokens = AuthTokens(
                accessToken = "new_access_token",
                refreshToken = "new_refresh_token",
                expiresAt = System.currentTimeMillis() + 15 * 60 * 1000
            )
            tokenStorage.saveTokens(tokens)
            Result.success(tokens)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentSession(): UserSession? {
        return sessionManager.session.first()
    }
}
