package com.mitsudrive.core.auth.model

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long
)

data class UserSession(
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val isAuthenticated: Boolean,
    val lastLoginAt: Long
)

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val session: UserSession) : AuthState()
    data class Error(val message: String) : AuthState()
}
