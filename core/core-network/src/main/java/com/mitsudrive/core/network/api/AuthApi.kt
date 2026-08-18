package com.mitsudrive.core.network.api

import com.mitsudrive.core.network.model.ApiResponse
import retrofit2.http.*

data class LoginRequest(
    val phone: String,
    val password: String
)

data class RegisterRequest(
    val phone: String,
    val username: String,
    val password: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class UserDto(
    val id: String,
    val username: String,
    val avatarUrl: String?,
    val rating: Double,
    val carsCount: Int
)

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<TokenResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<TokenResponse>
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Header("Authorization") refreshToken: String): ApiResponse<TokenResponse>
    
    @GET("auth/me")
    suspend fun getCurrentUser(): ApiResponse<UserDto>
    
    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>
}
