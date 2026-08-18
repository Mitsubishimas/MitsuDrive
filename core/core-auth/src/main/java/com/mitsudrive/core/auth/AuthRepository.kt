package com.mitsudrive.core.auth

import android.content.Context
import android.provider.Settings
import com.mitsudrive.core.auth.model.AuthState
import com.mitsudrive.core.auth.model.UserSession
import com.mitsudrive.core.auth.storage.TokenStorage
import com.mitsudrive.core.network.api.SiteApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface AuthRepository {
    val authState: StateFlow<AuthState>
    
    suspend fun login(email: String, password: String): Result<UserSession>
    suspend fun register(email: String, password: String, name: String, phone: String): Result<UserSession>
    suspend fun sendSmsCode(phone: String): Result<Unit>
    suspend fun verifySmsCode(phone: String, code: String): Result<Unit>
    suspend fun logout()
    suspend fun checkToken(): Boolean
    suspend fun getCurrentSession(): UserSession?
}

class AuthRepositoryImpl(
    private val context: Context,
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
    private val siteApiClient: SiteApiClient = SiteApiClient()
) : AuthRepository {
    
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    init {
        // Проверяем сохранённый токен
        repositoryScope.launch {
            val session = sessionManager.session.first()
            if (session != null) {
                _authState.value = AuthState.Authenticated(session)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
    
    override suspend fun login(email: String, password: String): Result<UserSession> {
        return try {
            val deviceId = getDeviceId()
            val response = siteApiClient.login(email, password, deviceId)
            
            if (response.status == "success" && response.token != null) {
                val session = UserSession(
                    userId = response.userId?.toString() ?: response.user?.id?.toString() ?: "0",
                    username = response.name ?: response.user?.name ?: email.substringBefore("@"),
                    avatarUrl = null,
                    isAuthenticated = true,
                    lastLoginAt = System.currentTimeMillis()
                )
                
                tokenStorage.saveTokens(
                    com.mitsudrive.core.auth.model.AuthTokens(
                        accessToken = response.token!!,
                        refreshToken = response.token!!,
                        expiresAt = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000 // 30 дней
                    )
                )
                sessionManager.saveSession(session)
                _authState.value = AuthState.Authenticated(session)
                Result.success(session)
            } else {
                _authState.value = AuthState.Error(response.message ?: "Ошибка входа")
                Result.failure(Exception(response.message ?: "Ошибка входа"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Ошибка сети")
            Result.failure(e)
        }
    }
    
    override suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String
    ): Result<UserSession> {
        return try {
            val deviceId = getDeviceId()
            val response = siteApiClient.register(email, password, name, phone, deviceId)
            
            if (response.status == "success" && response.token != null) {
                val session = UserSession(
                    userId = response.userId?.toString() ?: response.user?.id?.toString() ?: "0",
                    username = response.name ?: name,
                    avatarUrl = null,
                    isAuthenticated = true,
                    lastLoginAt = System.currentTimeMillis()
                )
                
                tokenStorage.saveTokens(
                    com.mitsudrive.core.auth.model.AuthTokens(
                        accessToken = response.token!!,
                        refreshToken = response.token!!,
                        expiresAt = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000
                    )
                )
                sessionManager.saveSession(session)
                _authState.value = AuthState.Authenticated(session)
                Result.success(session)
            } else {
                Result.failure(Exception(response.message ?: "Ошибка регистрации"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun sendSmsCode(phone: String): Result<Unit> {
        return try {
            val response = siteApiClient.sendSmsCode(phone)
            if (response.status == "success") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Ошибка отправки SMS"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifySmsCode(phone: String, code: String): Result<Unit> {
        return try {
            val response = siteApiClient.verifySmsCode(phone, code)
            if (response.status == "success") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Неверный код"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout() {
        val token = tokenStorage.getAccessToken()
        if (token != null) {
            try {
                siteApiClient.logout(token)
            } catch (e: Exception) {
                // Игнорируем ошибки при выходе
            }
        }
        
        sessionManager.clearSession()
        _authState.value = AuthState.Unauthenticated
    }
    
    override suspend fun checkToken(): Boolean {
        val token = tokenStorage.getAccessToken() ?: return false
        
        return try {
            val response = siteApiClient.checkToken(token)
            response.status == "success"
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun getCurrentSession(): UserSession? {
        return sessionManager.session.first()
    }
    
    private fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown_device"
    }
}
